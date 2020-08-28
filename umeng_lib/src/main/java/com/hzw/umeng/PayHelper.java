package com.hzw.umeng;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import com.hzw.umeng.entity.Configs;
import com.hzw.umeng.pay.AliPay;
import com.hzw.umeng.pay.WeChatPay;
import com.hzw.umeng.result.PayResultListener;
import com.hzw.umeng.utils.Utils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;

/**
 * author:HZWei
 * date:  2020/8/21
 * desc:
 */
@SuppressWarnings("all")
public class PayHelper {


    public <T extends Builder> PayHelper(T t) {

        if (Utils.isContextNull(t.mActivity)) return;
        if (t instanceof WXBuilder){
            WXBuilder builder=(WXBuilder)t;
            if (!isConditionsOK(builder)) return;

            String appid = Utils.getApplicationMetaData(t.mActivity, Configs.WX_APP_ID);
            IWXAPI api = WXAPIFactory.createWXAPI(t.mActivity, appid);
            api.registerApp(appid);
            PayReq request = new PayReq();
            request.appId = appid;
            request.partnerId = builder.mPartnerId;
            request.prepayId = builder.mPrepayId;
            request.packageValue = "Sign=WXPay";
            request.nonceStr = builder.mNonceStr;
            request.timeStamp = builder.mTimeStamp;
            request.sign = builder.mSign;
            api.sendReq(request);
            WeChatPay.INSTANCE.setPayResultListener(builder.mPayResultListener);

        }else if (t instanceof AliBuilder){
            AliBuilder builder=(AliBuilder) t;
            if (TextUtils.isEmpty(builder.mOrderInfo)){
                showToast(builder.mActivity,R.string.ali_pay_info_empty);
                return;
            }
            AliPay.pay(builder.mActivity,builder.mOrderInfo,builder.mPayResultListener);

        }


    }

    private boolean isConditionsOK(WXBuilder builder) {
        if (TextUtils.isEmpty(builder.mPartnerId)){
            showToast(builder.mActivity,R.string.wx_pay_partnerid_empty);
            return false;
        }else if (TextUtils.isEmpty(builder.mPrepayId)){
            showToast(builder.mActivity,R.string.wx_pay_prepayid_empty);
            return false;
        }else if (TextUtils.isEmpty(builder.mNonceStr)){
            showToast(builder.mActivity,R.string.wx_pay_noncestr_empty);
            return false;
        }else if (TextUtils.isEmpty(builder.mTimeStamp)){
            showToast(builder.mActivity,R.string.wx_pay_timestamp_empty);
            return false;
        }else if (TextUtils.isEmpty(builder.mSign)){
            showToast(builder.mActivity,R.string.wx_pay_sign_empty);
            return false;
        }
        return true;
    }

    private void  showToast(Activity activity,@StringRes int msg){
        Utils.showShort(activity,activity.getString(msg));
    }


    public static WXBuilder createWXPayBuilder(Activity activity){
        return new WXBuilder(activity);
    }


    public static AliBuilder createAliPayBuilder(Activity activity){
        return  new AliBuilder(activity);
    }


    public static class WXBuilder extends Builder<WXBuilder> {
        //商户ID
        private String mPartnerId;
        //预支付ID
        private String mPrepayId;

        private String mNonceStr;
        //时间戳
        private String mTimeStamp;
        //签名
        private String mSign;

        public WXBuilder(Activity activity) {
            super(activity);
        }

        public WXBuilder setPartnerId(String partnerId) {
            mPartnerId = partnerId;
            return this;
        }

        public WXBuilder setPrepayId(String prepayId) {
            mPrepayId = prepayId;
            return this;
        }



        public WXBuilder setNonceStr(String nonceStr) {
            mNonceStr = nonceStr;
            return this;
        }

        public WXBuilder setTimeStamp(String timeStamp) {
            mTimeStamp = timeStamp;
            return this;
        }

        public WXBuilder setSign(String sign) {
            mSign = sign;
            return this;
        }

        @Override
        public PayHelper pay() {
            return super.pay();
        }
    }

    public static class AliBuilder extends Builder<AliBuilder> {

        private String mOrderInfo;

        public AliBuilder(Activity activity) {
            super(activity);
        }

        public AliBuilder setOrderInfo(String orderInfo) {
            mOrderInfo = orderInfo;
            return this;
        }

        @Override
        public PayHelper pay() {
            return super.pay();
        }
    }

    public static class Builder<T extends Builder> {

         Activity mActivity;
         PayResultListener mPayResultListener;

        public Builder(Activity activity) {
            mActivity = new WeakReference<>(activity).get();
        }

        public T setPayResultListener(PayResultListener listener) {
            mPayResultListener = listener;
            return (T) this;
        }

        public PayHelper pay() {
            return new PayHelper((T) this);
        }
    }
}
