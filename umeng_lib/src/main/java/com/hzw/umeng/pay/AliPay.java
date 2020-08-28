package com.hzw.umeng.pay;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.hzw.umeng.result.PayResultListener;
import com.hzw.umeng.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * author:HZWei
 * date:  2020/8/24
 * desc:
 */
public class AliPay {

    private static final int ALI_PAY_FLAG = 100;


    public static void pay(Activity activity,String orderInfo,PayResultListener listener){
        Thread thread = new Thread(new PayRunnable(activity, orderInfo, listener));
        thread.start();
    }


    public static class PayRunnable implements Runnable{

        private Activity mActivity;
        private String mOrderInfo;
        private AliPayHandler mHandler;

        public PayRunnable(Activity activity, String orderInfo, PayResultListener listener) {
            mActivity = new WeakReference<>(activity).get();
            mOrderInfo = orderInfo;
            mHandler = new AliPayHandler(listener);
        }

        @Override
        public void run() {
            mHandler.removeMessages(ALI_PAY_FLAG);
            if (Utils.isContextNull(mActivity)) return;
            PayTask task = new PayTask(mActivity);
            Map<String, String> result = task.payV2(mOrderInfo, true);
            Message msg = new Message();
            msg.what = ALI_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }



    @SuppressWarnings("all")
    public static class AliPayHandler extends Handler {
        private String status;
        private String result;
        private String memo;

        private PayResultListener mPayResultListener;

        public AliPayHandler(PayResultListener listener) {
            super();
            mPayResultListener = new WeakReference<>(listener).get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ALI_PAY_FLAG) {
                if (msg.obj instanceof Map) {
                    Map<String, String> map = (Map<String, String>) msg.obj;
                    String resultStr = Utils.mapToString(map);
                    Utils.debug(resultStr);
                    for (String key : map.keySet()) {
                        if (TextUtils.equals(key, "resultStatus")) {
                            status = map.get(key);
                        } else if (TextUtils.equals(key, "result")) {
                            result = map.get(key);
                        } else if (TextUtils.equals(key, "memo")) {
                            memo = map.get(key);
                        }
                    }

                    switch (status) {
                        case "9000":
                            //支付成功
                            if (mPayResultListener!=null){
                                mPayResultListener.onSuccess();
                            }
                            break;
                        case "6001":
                            //支付失败
                            if (mPayResultListener!=null){
                                mPayResultListener.onFailed(resultStr);
                            }
                            break;
                        default:
                            if (mPayResultListener!=null){
                                mPayResultListener.onCancel();
                            }
                            break;
                    }


                }

            }
        }
    }


}
