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

    //���ڱ�����󣬾����������֪ͨ��������־�����
    public static Messager messager;
    //���ڶ�Ԫ�صĲ���ʵ��
    public static Elements elements;
    //���ڴ����µ�Դ�ļ������ļ������ļ����ļ�������
    public static Filer filer;
    public static Types types;

    public static void init(ProcessingEnvironment env){
        messager = env.getMessager();
        elements = env.getElementUtils();
        filer = env.getFiler();
        types = env.getTypeUtils();
    }


}
