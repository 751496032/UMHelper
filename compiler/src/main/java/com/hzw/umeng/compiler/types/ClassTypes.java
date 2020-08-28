package com.hzw.umeng.compiler.types;

import com.squareup.javapoet.ClassName;

/**
 * author:HZWei
 * date:  2020/8/25
 * desc:
 */
public class ClassTypes {

    public static ClassName ACTIVITY = ClassName.get("android.app", "Activity");
    public static ClassName BUNDLE=  ClassName.get("android.os","Bundle");
    public static ClassName INTENT=  ClassName.get("android.content","Intent");

    public static ClassName WX_CALLBACK_ACTIVITY = ClassName.get("com.umeng.socialize.weixin.view", "WXCallbackActivity");
    public static ClassName WX_IAPI_EVENT_HANDLER = ClassName.get("com.tencent.mm.opensdk.openapi", "IWXAPIEventHandler");
    public static ClassName WX_BASE_REQ=ClassName.get("com.tencent.mm.opensdk.modelbase","BaseReq");
    public static ClassName WX_BASE_RESP=ClassName.get("com.tencent.mm.opensdk.modelbase","BaseResp");
    public static ClassName WX_IAPI=ClassName.get("com.tencent.mm.opensdk.openapi","IWXAPI");
    public static ClassName WX_API_FACTORY=ClassName.get("com.tencent.mm.opensdk.openapi","WXAPIFactory");


    public static ClassName UTILS =ClassName.get("com.hzw.umeng.utils","Utils");
    public static ClassName CONFIGS =ClassName.get("com.hzw.umeng.entity","Configs");
    public static ClassName WECHAT_PAY=ClassName.get("com.hzw.umeng.pay","WeChatPay");





}
