package com.hzw.umeng.compiler.utils;

import com.hzw.umeng.compiler.AptContext;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.tools.Diagnostic;

/**
 * author:HZWei
 * date:  2020/8/19
 * desc:
 */
public class Utils {

    public static void warn(String msg) {
        AptContext.messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    public static void error(String msg) {
        AptContext.messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }


    /**
     * <p>参数化类型名称</p>
     * {@link com.squareup.javapoet.ClassName}
     * @param className  <code>android.os.Bundle</code> 全类名称
     * @return
     */
    public static TypeName getClassTypeName(String className) {
        return ParameterizedTypeName.get(AptContext.elements.getTypeElement(className).asType());
    }

}
