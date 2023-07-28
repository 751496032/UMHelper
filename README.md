
[toc]
## 介绍
UMHelper 主要是基于友盟SDK进行再次封装，可以降低项目接入的成本，使用简便。

目前主要功能如下：
- 微信支付
- 支付宝支付
- 微信授权
- 微信分享（包括图片、网页、视频）
- 统计
- 异常捕获

## 版本
最新版本：
[![](https://jitpack.io/v/com.gitee.common-apps/umhelper.svg)](https://jitpack.io/#com.gitee.common-apps/umhelper)

### 版本更新记录

**1.0.9**

- 修复Android11 兼容性问题（微信分享问题）

**1.0.8**

- 修复支付宝闪退问题


## 项目接入方式？

项目仓库配置，在项目的根build.grade文件加入：


```
buildscript{

    repositories｛
         maven { url 'https://dl.bintray.com/umsdk/release' }
    ｝
}

allprojects{
    repositories｛
        maven { url "https://jitpack.io" }
        maven { url 'https://dl.bintray.com/umsdk/release' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven {
            credentials {
                username 'FX7Fyi'
                password 'BUE4Ry8juk'
            }
            url 'https://repo.rdc.aliyun.com/repository/136106-release-Z4Ub7x/'
        }
    }
}
```

项目依赖配置：


```
dependencies {
    implementation 'com.gitee.common-apps.umhelper:runtime:版本号'
    annotationProcessor 'com.gitee.common-apps.umhelper:compiler:版本号'
}
```


## 具体使用

只需两步就可以集成使用：
1. **文件配置**
1. **初始化**


### 文件配置

**一、在AndroidMainfest文件中，配置如下元数据mate-data**

配置mate-data：

```
<meta-data android:name="UMENG_APPKEY" android:value="xxxxxxx"/>
<meta-data android:name="UMENG_CHANNEL" android:value="xxxxxxx"/>
<meta-data android:name="WX_APP_ID" android:value="xxxxxxx"/>
<meta-data android:name="WX_APP_SECRET" android:value="xxxxxxx"/>
```
**二、适配Android11系统**

主要适配两点：

1. **软件包可见性**
1. **强制执行分区存储权限**


**1> 适配软件包可见性**

修改了软件包可见性，主要会影响到已安装的应用与当前应用交互的方式，比如在判断某个应用是否安装，如果targetSdkVersion=30时，就会抛出未安装等提示。

```xml
 <queries>
        <package android:name="com.tencent.mm" />
 </queries>
```

**2> 适配强制执行分区存储权限**
<center>
![](https://www.showdoc.com.cn/server/api/attachment/visitfile/sign/0aa1dcbaa0c0c3210a1d89c0a185475c)

</center>

**file_provider_paths:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path
        name="umeng_cache"
        path="umeng_cache/" />
    <root-path
        name="umeng_cache"
        path="." />
</paths>
```

在AndroidMainfest声明：

```xml
 <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider_paths" />

 </provider>
```



**四、在AndroidMainfest注册微信分享授权、支付的Activity**

这个在`dependencies`中引入依赖包后，同步后会自动生成**WXEntryActivity、WXPayEntryActivity**这个两个类，不需要手动去编写，只需要在清单文件中注册声明即可

```
<activity
            android:name="包名.wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />

<activity
            android:name="包名.wxapi.WXPayEntryActivity"
            android:exported="true"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:launchMode="singleTop"/>
```



### UMHelper初始化
在**Application**的**onCreate**方法中初始化，同时需要在Application中添加`@WXBuilder("xxxx")`类注解，其中`xxxx`是应用的**applicationId**


```
@WXBuilder("xxxxx")
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化UMApiHelper
        UMApiHelper.init(this,"com.xxxxx.fileprovider");
    }
}

```


另外，可以通过UMUtils的Log日志来查看缺少配置的mate-data

<center>
![输入图片说明](https://images.gitee.com/uploads/images/2020/0828/155753_af408bb9_553126.png "屏幕截图.png")
</center>


###  使用用例

提供了三个功能实现类，都是链式调用的方式，分别是：
- LoginHelper：微信授权及删除授权
- PayHelper: 微信支付、支付宝支付
- ShareHelper: 微信分享（图片、视频、网页）


####  微信登录授权


```
LoginHelper.createLoginBuilder(this)
                .setAuthPlatform(SHARE_MEDIA.WEIXIN)
                .setNeedAuthOnGetUserInfo(true)
                .setUMAuthListener(new UMAuthResultImpl(this) {
                    @Override
                    public void onResult(UMAuthResult result, SHARE_MEDIA media, Map<String, String> map) {
                        super.onResult(result, media, map);
                        mTvHello.setText(Utils.mapToString(map));
                    }
                }).login();
```


#### 支付


```
 PayHelper.createWXPayBuilder(this)
                .setNonceStr("xxxxxx")
                .setTimeStamp("xxxxxx")
                .setPrepayId("xxxxxx")
                .setPartnerId("xxxxxx")
                .setSign("xxxxxx")
                .setPayResultListener(new PayResultListener() {
                    @Override
                    public void onCancel() {
                        mTvHello.setText("wx pay cancel");
                    }

                    @Override
                    public void onSuccess() {
                        mTvHello.setText("wx pay success");
                    }

                    @Override
                    public void onFailed(String error) {
                        mTvHello.setText("wx pay error: " + error);
                    }
                }).pay();
```

#### 分享
```
ShareHelper.createWebBuilder(this)
                .setSharePlatform(SHARE_MEDIA.WEIXINE)
                .setWebUrl("https://android.myapp.com/myapp/detail.htm?apkName=com.jingdong.app.mall")
                .setTitle("-----多快好省，不负每一份热爱-----")
                .setDescription("京东APP是一款移动购物软件，具有商品搜索[浏览、评论查阅、商品购买、在线支付/货到付款、订单查询、物流跟踪、晒单/评价、返修退换货等功能，为您打造简单、快乐")
                .setThumbnailImage("https://pp.myapp.com/ma_icon/0/icon_7193_1598341618/96")
                .share();
```


## 打包混淆配置


```
-keep  class com.hzw.umeng.*{ *;}
```

另外还需要加入友盟的混淆配置：


```
# 友盟统计、crash
-keep class com.umeng.** {*;}
-keep class com.uc.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.zui.** {*;}
-keep class com.miui.** {*;}
-keep class com.heytap.** {*;}
-keep class a.** {*;}
-keep class com.vivo.** {*;}
#这里注意需要自己项目的包名
-keep public class 包名.R$*{
public static final int *;
}
#友盟社会化分享
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}


-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}

-keep class com.twitter.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keepattributes Signature
```










