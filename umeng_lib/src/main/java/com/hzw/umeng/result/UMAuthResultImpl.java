package com.hzw.umeng.result;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;

import com.hzw.umeng.R;
import com.hzw.umeng.entity.UMAuthResult;
import com.hzw.umeng.utils.Utils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * author:HZWei
 * date:  2020/8/21
 * desc:
 */
public class UMAuthResultImpl implements UMAuthListener, UMAuthResultListener {
    private static final String TAG = "UMAuthResultImpl";

    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private boolean mIsDeleteAuth;


    public UMAuthResultImpl(Activity activity, boolean deleteAuth) {
        this.mActivity = new WeakReference<>(activity).get();
        this.mIsDeleteAuth = deleteAuth;
    }

    public UMAuthResultImpl(Activity activity) {
        this.mActivity = new WeakReference<>(activity).get();
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        if (!mIsDeleteAuth) {
            mProgressDialog = Utils.createProgressDialog(mActivity, R.string.login_auth_loading);
            Utils.showDialog(mProgressDialog);
        }

    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        Utils.cancelDialog(mProgressDialog);
        UMAuthResult result = new UMAuthResult();
        if (map != null) {
            Utils.debug(Utils.mapToString(map));
            if (map.containsKey("uid")) result.setUid(map.get("uid"));
            if (map.containsKey("name")) result.setName(map.get("name"));
            if (map.containsKey("gender")) result.setGender(map.get("gender"));
            if (map.containsKey("iconurl")) result.setIconUrl(map.get("iconurl"));
            if (map.containsKey("openid")) result.setOpenId(map.get("openid"));
        }
        onResult(result, share_media, map);

    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        if (!mIsDeleteAuth) Utils.showShort(mActivity, mActivity.getString(R.string.login_auth_fail) + throwable.getMessage());
        Utils.error(throwable.getMessage());
        Utils.cancelDialog(mProgressDialog);
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        if (!mIsDeleteAuth) Utils.showShort(mActivity, mActivity.getString(R.string.login_auth_canceled));
        Utils.cancelDialog(mProgressDialog);
    }


    @Override
    public void onResult(UMAuthResult result, SHARE_MEDIA media, Map<String, String> map) {

    }
}
