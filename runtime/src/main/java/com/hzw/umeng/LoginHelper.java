package com.hzw.umeng;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.hzw.umeng.result.UMAuthResultImpl;
import com.hzw.umeng.utils.Utils;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;

/**
 * author:HZWei
 * date:  2020/8/21
 * desc:
 */
public class LoginHelper {


    private LoginHelper(Builder builder,boolean deleteAuth) {
        if (Utils.isContextNull(builder.mActivity)) return;
        if (builder.mAuthPlatform == null) {
            Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.login_platform_empty));
            return;
        }if (builder.mUMAuthListener==null){
            Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.login_auth_listener_empty));
            return;
        }
        if (deleteAuth){
            UMShareAPI.get(builder.mActivity).deleteOauth(builder.mActivity,builder.mAuthPlatform,builder.mUMAuthListener);
        }else {
            UMShareConfig config = new UMShareConfig();
            config.isNeedAuthOnGetUserInfo(builder.mIsNeedAuthOnGetUserInfo);
            UMShareAPI.get(builder.mActivity).setShareConfig(config);
            UMShareAPI.get(builder.mActivity).getPlatformInfo(builder.mActivity, builder.mAuthPlatform, builder.mUMAuthListener);
        }



    }

    public static LoginHelper.Builder createLoginBuilder(Activity activity) {
        return new LoginHelper.Builder(activity);
    }

    public static final class Builder {

        private Activity mActivity;
        private SHARE_MEDIA mAuthPlatform;
        private UMAuthResultImpl mUMAuthListener;
        /**
         * <p>
         *     <code>true</code> 每次登录拉取确认界面
         * </p>
         */
        private boolean mIsNeedAuthOnGetUserInfo;

         Builder(Activity activity) {
            mActivity = new WeakReference<>(activity).get();
        }

        public Builder setAuthPlatform(@NonNull SHARE_MEDIA media) {
            mAuthPlatform = media;
            return this;
        }

        public Builder setUMAuthListener(UMAuthResultImpl listener) {
            mUMAuthListener = listener;
            return this;
        }

        public Builder setNeedAuthOnGetUserInfo(boolean needAuthOnGetUserInfo) {
            mIsNeedAuthOnGetUserInfo = needAuthOnGetUserInfo;
            return this;
        }

        /**
         * <p>
         *     授权登录
         * </p>
         * @return
         */
        public LoginHelper login() {
            return new LoginHelper(this,false);
        }

        /**
         * <p>
         *     删除授权
         * </p>
         * @return
         */
        public LoginHelper deleteAuth(){
             return  new LoginHelper(this,true);
        }
    }
}
