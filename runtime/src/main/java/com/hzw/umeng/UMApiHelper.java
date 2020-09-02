package com.hzw.umeng;

import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.hzw.umeng.annotations.WXBuilder;
import com.hzw.umeng.entity.Configs;
import com.hzw.umeng.utils.Utils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * author:HZWei
 * date:  2020/8/21
 * desc:
 */
public class UMApiHelper {

    private static final List<String> metaDataKeys = Arrays.asList(Configs.UMENG_APPKEY, Configs.UMENG_CHANNEL,
            Configs.WX_APP_ID, Configs.WX_APP_SECRET);

    /**
     * <p>
     * 初始化之前，需要配置<code>mate-data</code>
     * 元数据名称{@link Configs}
     * </p>
     *
     * @param context
     */
    public static void init(Context context) {

        checkConfiguration(context);

        String appkey = AnalyticsConfig.getAppkey(context);
        String channel = AnalyticsConfig.getChannel(context);
        String wxAppId = Utils.getApplicationMetaData(context, Configs.WX_APP_ID);
        String wxAppSecret = Utils.getApplicationMetaData(context, Configs.WX_APP_SECRET);

        // 初始化UM
        UMConfigure.init(context, appkey,
                channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setEncryptEnabled(!BuildConfig.DEBUG);
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        //配置WX参数
        PlatformConfig.setWeixin(wxAppId, wxAppSecret);


    }


    public static void init(Context context, boolean isDebug) {
        Utils.isDebug = isDebug;
        init(context);
    }


    private static void checkConfiguration(Context context) {
        if (!Utils.isDebug) return;
        checkMetaData(context);
        checkRegisterActivitys(context);
        checkAnnotations(context);

    }

    private static void checkAnnotations(Context context) {
        if (context instanceof Application) {
            Class<? extends Context> contextClass = context.getClass();
            WXBuilder annotation = contextClass.getAnnotation(WXBuilder.class);
            if (annotation == null) {
                Utils.error(contextClass.getSimpleName() + "中缺少类注解：WXBuilder");
            } else if (TextUtils.isEmpty(annotation.value())) {
                Utils.error("WXBuilder类注解中缺少value值：" + context.getPackageName());
            }

        }
    }

    private static void checkRegisterActivitys(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo.activities == null) return;
            boolean isWXActivityRegistered = false, isWXPayActivityRegistered = false;
            for (ActivityInfo activityInfo : packageInfo.activities) {
                if (activityInfo == null || TextUtils.isEmpty(activityInfo.name)) continue;
                if (activityInfo.name.contains(".wxapi.WXEntryActivity"))
                    isWXActivityRegistered = true;
                if (activityInfo.name.contains(".wxapi.WXPayEntryActivity"))
                    isWXPayActivityRegistered = true;
                if (isWXActivityRegistered && isWXPayActivityRegistered) break;
            }
            if (!isWXActivityRegistered) {
                Utils.error("AndroidManifest中缺少：" + context.getPackageName() + ".wxapi.WXEntryActivity");
            } else if (!isWXPayActivityRegistered) {
                Utils.error("AndroidManifest中缺少：" + context.getPackageName() + ".wxapi.WXPayEntryActivity");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }


    private static void checkMetaData(Context context) {

        try {
            ApplicationInfo info = Utils.getApplicationInfo(context, PackageManager.GET_META_DATA);
            Bundle bundle = info.metaData;
            //android 9.0 对 getMap添加了@hide，无法通过反射调用
//            Class<?> superclass = bundle.getClass().getSuperclass();
//            if (superclass != null) {
//                Method method = superclass.getDeclaredMethod("getMap");
//                method.setAccessible(true);
//                Object o = method.invoke(bundle);
//                if (o instanceof ArrayMap) {
//                    ArrayMap map = (ArrayMap) o;
//                    Utils.debug(Utils.mapToJSON(map));
//                    StringBuilder builder = new StringBuilder();
//                    for (String key : metaDataKeys) {
//                        if (!map.containsKey(key)) {
//                            builder.append(key).append("\u3000");
//                        }
//                    }
//                    if (!TextUtils.isEmpty(builder)) {
//                        Utils.error("AndroidManifest中缺少缺少配置 meta-data ：" + builder.toString());
//                    }
//
//                }
//            }

            StringBuilder keys = new StringBuilder();
            JSONObject jsonObject = new JSONObject();
            for (String key : metaDataKeys) {
                String value = bundle.getString(key);
                if (TextUtils.isEmpty(value)) {
                    keys.append(key).append("\u3000");
                }else {
                    jsonObject.put(key,value);
                }
            }
            if (!TextUtils.isEmpty(jsonObject.toString())){
                Utils.debug(jsonObject.toString());
            }
            if (!TextUtils.isEmpty(keys)) {
                Utils.error("AndroidManifest中缺少缺少配置 meta-data ：" + keys.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
