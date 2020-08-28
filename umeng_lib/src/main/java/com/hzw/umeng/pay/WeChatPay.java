package com.hzw.umeng.pay;

import com.hzw.umeng.result.PayResultListener;

/**
 * author:HZWei
 * date:  2020/8/25
 * desc:
 */
public class WeChatPay {

    public static final  WeChatPay INSTANCE=new WeChatPay();

    private PayResultListener mPayResultListener;

    public void setPayResultListener(PayResultListener listener) {
        mPayResultListener = listener;
    }

    public void paySuccess(){
        if (mPayResultListener!=null){
            mPayResultListener.onSuccess();
        }
    }

    public void payFailed(String error){
        if (mPayResultListener!=null){
            mPayResultListener.onFailed(error);
        }
    }

    public void payCancel(){
        if (mPayResultListener!=null){
            mPayResultListener.onCancel();
        }
    }

}
