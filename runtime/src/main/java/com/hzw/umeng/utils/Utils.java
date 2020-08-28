package com.hzw.umeng.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.hzw.umeng.BuildConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * author:HZWei
 * date:  2020/8/20
 * desc:
 */
public class Utils {

    private static final String TAG = "UMUtils";

    public static boolean isShow=BuildConfig.DEBUG;

    public static void showShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void error(String msg){
        error(TAG,msg);
    }


    public static void debug(String msg){
        debug(TAG,msg);
    }

    public static void error(String tag, String msg) {
        if (isShow) {
            Log.e(tag, msg);
        }
    }

    public static void debug(String tag,String msg){
        if (isShow){
            Log.d(tag, msg);
        }
    }


    public static String getApplicationMetaData(Context context,String mateDataName){
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String metaDataValue = applicationInfo.metaData.getString(mateDataName);
            debug("mate-data name: "+mateDataName+" value: "+metaDataValue);
            if (TextUtils.isEmpty(metaDataValue)){
                error("检查是否配置 "+mateDataName+" mate-data");
            }
            return metaDataValue;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            error("mate-data name: "+mateDataName+" error: "+e.getMessage());
        }
        return "t";

    }


    public static boolean isContextNull(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return true;
        }
        return false;
    }


    public static String mapToString(Map<String, String> map){
        if (map==null || map.isEmpty()) return "";
        StringBuilder builder=new StringBuilder();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> entry:entrySet){
            builder.append(entry.getKey()).append("：").append(entry.getValue()).append("\n");
        }
        return builder.toString();
    }

    public static String mapToJSON(Map<String, Object> map){
        if (map==null || map.isEmpty()) return "";
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }


    public static ProgressDialog createProgressDialog(Activity activity, @StringRes int resId) {
        if (isContextNull(activity)) return null;
        ProgressDialog dialog = new ProgressDialog(activity);
        if (resId != 0)
            dialog.setMessage(activity.getString(resId));
        return dialog;
    }

    public static void showDialog(Dialog dialog) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }


    public static void cancelDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }



    public static boolean isClientInstall(Activity context, SHARE_MEDIA media) {
        return UMShareAPI.get(context).isInstall(context, media);
    }


    /**
     *
     * <p>
     *     内存泄漏解决方案
     *     在使用分享或者授权的Activity中，重写onDestory()方法中调用
     * </p>
     * @param activity
     */
    public static void release(Activity  activity){
        UMShareAPI.get(activity).release();
    }

}
