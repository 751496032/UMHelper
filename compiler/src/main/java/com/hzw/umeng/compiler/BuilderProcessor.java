package com.hzw.umeng.compiler;

import com.google.auto.service.AutoService;
import com.hzw.umeng.annotations.WXBuilder;
import com.hzw.umeng.compiler.types.ClassTypes;
import com.hzw.umeng.compiler.utils.Utils;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * author:HZWei
 * date:  2020/8/25
 * desc:
 */
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {


    private static final List<String> supportedAnnotations = Collections.singletonList(WXBuilder.class.getCanonicalName());


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        AptContext.init(processingEnv);
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(supportedAnnotations);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        process(WXBuilder.class, roundEnv);
        return true;
    }


    private void process(Class<? extends Annotation> clazz, RoundEnvironment env) {
        env.getElementsAnnotatedWith(clazz).forEach((Consumer<Element>) element -> {
            if (element.getKind().isClass()) {
//                String packageName = AptContext.elements.getPackageOf(element).getQualifiedName().toString();
                WXBuilder wxBuilder = element.getAnnotation(WXBuilder.class);
                if (wxBuilder != null) {
                    String value = wxBuilder.value();
                    buildWXCallbackActivity(value);
                    buildWXPayActivity(value);
                    Utils.warn(value);
                }


            }

        });


    }

    private void buildWXCallbackActivity(String packageName) {
        TypeSpec typeSpec = TypeSpec.classBuilder("WXEntryActivity")
                .superclass(ClassTypes.WX_CALLBACK_ACTIVITY)
                .addModifiers(Modifier.PUBLIC)
                .build();
        buildFile(packageName + ".wxapi", typeSpec);

    }

    private void buildWXPayActivity(String packageName) {
        TypeSpec typeSpec = TypeSpec.classBuilder("WXPayEntryActivity")
                .superclass(ClassTypes.ACTIVITY)
                .addSuperinterface(ClassTypes.WX_IAPI_EVENT_HANDLER)
                .addModifiers(Modifier.PUBLIC)
                .addField(buildWXApiField())
                .addMethod(buildOnCreateMethod())
                .addMethod(buildOnNewIntentMethod())
                .addMethod(buildWXOnReqMethod())
                .addMethod(buildWXOnRespMethod())
                .build();
        buildFile(packageName + ".wxapi", typeSpec);

    }

    private FieldSpec buildWXApiField() {
        return FieldSpec.builder(ClassTypes.WX_IAPI, "mAPI")
                .addModifiers(Modifier.PRIVATE)
                .build();
    }


    private MethodSpec buildOnCreateMethod() {
        return MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassTypes.BUNDLE, "savedInstanceState")
                .addStatement("super.onCreate($N)", "savedInstanceState")
                .addStatement("String appId=$T.getApplicationMetaData(this,\"WX_APP_ID\")",ClassTypes.UTILS)
                .addStatement("$N=$T.createWXAPI(this,$N)","mAPI",ClassTypes.WX_API_FACTORY,"appId")
                .addStatement("$N.handleIntent(getIntent(),this)","mAPI")
                .build();
    }

    private MethodSpec buildOnNewIntentMethod(){
        return MethodSpec.methodBuilder("onNewIntent")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ClassTypes.INTENT,"intent")
                .addStatement("super.onNewIntent(intent)")
                .addStatement("setIntent(intent)")
                .addStatement("$N.handleIntent(intent, this)","mAPI").build();

    }


    private MethodSpec buildWXOnReqMethod() {
        return MethodSpec.methodBuilder("onReq")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassTypes.WX_BASE_REQ, "baseReq").build();

    }



    private MethodSpec buildWXOnRespMethod() {

        return MethodSpec.methodBuilder("onResp")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassTypes.WX_BASE_RESP, "resp")
                .addStatement("int errCode=$N.errCode","resp")
                .beginControlFlow("if(errCode==0)")
                .addStatement("$T.INSTANCE.paySuccess()",ClassTypes.WECHAT_PAY)
                .nextControlFlow("else if(errCode==-1)")
                .addStatement("$T.INSTANCE.payFailed(\"errCode=\"+errCode+\"errStr=\"+resp.errStr)",ClassTypes.WECHAT_PAY)
                .nextControlFlow("else if(errCode==-2)")
                .addStatement("$T.INSTANCE.payCancel()",ClassTypes.WECHAT_PAY)
                .nextControlFlow("else")
                .addStatement("$T.INSTANCE.payFailed(\"errCode=\"+errCode+\"errStr=\"+resp.errStr)",ClassTypes.WECHAT_PAY)
                .endControlFlow()
                .addStatement("finish()")
                .build();
    }


    private void buildFile(String packageName, TypeSpec typeSpec) {
        try {
            JavaFile.builder(packageName, typeSpec).build().writeTo(AptContext.filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
