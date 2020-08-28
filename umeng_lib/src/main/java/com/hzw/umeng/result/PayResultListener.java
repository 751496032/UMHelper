package com.hzw.umeng.result;

/**
 * author:HZWei
 * date:  2020/8/24
 * desc:
 */
public interface PayResultListener {

    void onCancel();

    void onSuccess();

    void onFailed(String error);
}
