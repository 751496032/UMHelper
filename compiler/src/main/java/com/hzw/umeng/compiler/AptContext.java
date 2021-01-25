package com.hzw.umeng.compiler;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * author:HZWei
 * date:  2020/8/16
 * desc:
 */
public class AptContext {

    //用于报告错误
    public static Messager messager;
    //用于对元素的操作实现
    public static Elements elements;
    //用于创建新的源文件，类文件或辅助文件的文件管理器
    public static Filer filer;
    public static Types types;

    public static void init(ProcessingEnvironment env){
        messager = env.getMessager();
        elements = env.getElementUtils();
        filer = env.getFiler();
        types = env.getTypeUtils();
    }


}
