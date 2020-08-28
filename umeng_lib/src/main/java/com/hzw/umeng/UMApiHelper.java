package com.hzw.umeng;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.hzw.umeng.entity.Configs;
import com.hzw.umeng.utils.Utils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
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


    private static void checkConfiguration(Context context) {
        if (!BuildConfig.DEBUG) return;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.metaData;
            Class<?> superclass = bundle.getClass().getSuperclass();
            if (superclass != null) {
                Method method = superclass.getDeclaredMethod("getMap");
                method.setAccessible(true);
                Object o = method.invoke(bundle);
                if (o instanceof ArrayMap) {
                    ArrayMap map = (ArrayMap) o;
                    StringBuilder builder=new StringBuilder();
                    for (String key: metaDataKeys){
                        if (!map.containsKey(key)){
                            builder.append(key).append("\u3000");
                        }
                    }
                    if (!TextUtils.isEmpty(builder)){
                        Utils.error("缺少配置元数据 meta-data ："+builder.toString());
                    }
                    Utils.debug(Utils.mapToJSON(map));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utils.error(e.getMessage());
        }

    }


}
