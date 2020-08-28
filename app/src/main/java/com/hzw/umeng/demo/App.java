package com.hzw.umeng.demo;

import android.app.Application;

import com.hzw.umeng.UMApiHelper;
import com.hzw.umeng.annotations.WXBuilder;
import com.hzw.umeng.utils.Utils;

/**
 * author:HZWei
 * date:  2020/8/25
 * desc:
 */
@WXBuilder("com.baby.babyroom")
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UMApiHelper.init(this);

    }
}
