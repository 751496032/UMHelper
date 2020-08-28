package com.hzw.umeng.result;

import android.app.Activity;
import android.app.ProgressDialog;

import com.hzw.umeng.R;
import com.hzw.umeng.utils.Utils;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;

/**
 * author:HZWei
 * date:  2020/8/20
 * desc:
 */
public class UMShareResultImpl implements UMShareListener {

    private static final String TAG = "UMShareResultImpl";

    private Activity mActivity;
    private ProgressDialog mProgressDialog;


    public UMShareResultImpl(Activity activity) {
        mActivity = new WeakReference<>(activity).get();
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        mProgressDialog = Utils.createProgressDialog(mActivity, R.string.share_preparing_data);
        Utils.showDialog(mProgressDialog);
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        Utils.cancelDialog(mProgressDialog);
        Utils.showShort(mActivity,mActivity.getString(R.string.share_success));
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        Utils.error(throwable.getMessage());
        Utils.cancelDialog(mProgressDialog);
        Utils.showLong(mActivity,mActivity.getString(R.string.share_fail)+throwable.getMessage());
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        Utils.cancelDialog(mProgressDialog);
        Utils.showShort(mActivity,mActivity.getString(R.string.share_canceled));
    }
}
