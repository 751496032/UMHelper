package com.hzw.umeng.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hzw.umeng.LoginHelper;
import com.hzw.umeng.PayHelper;
import com.hzw.umeng.ShareHelper;
import com.hzw.umeng.entity.UMAuthResult;
import com.hzw.umeng.result.PayResultListener;
import com.hzw.umeng.result.UMAuthResultImpl;
import com.hzw.umeng.utils.Utils;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mTvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvHello = findViewById(R.id.hello);


    }


    private void login() {
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
    }


    private void payWX() {
        Utils.debug("payWX");
        PayHelper.createWXPayBuilder(this)
                .setNonceStr("vvAiz99Y5zf1a1UA")
                .setTimeStamp("1598595434")
                .setPrepayId("wx28141714151115c01928d797df37fd0000")
                .setPartnerId("1596089991")
                .setSign("280A1F5864B210870ECF9BF1F697D7DF")
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


    }

    private void shareWeb() {


        ShareHelper.createWebBuilder(this)
                .setSharePlatform(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE)
                .setWebUrl("https://android.myapp.com/myapp/detail.htm?apkName=com.jingdong.app.mall")
                .setTitle("-----多快好省，不负每一份热爱-----")
                .setDescription("京东APP是一款移动购物软件，具有商品搜索[浏览、评论查阅、商品购买、在线支付/货到付款、订单查询、物流跟踪、晒单/评价、返修退换货等功能，为您打造简单、快乐")
                .setThumbnailImage("https://pp.myapp.com/ma_icon/0/icon_7193_1598341618/96")
                .share();


    }

    public void onViewClick(View view) {
        int granted =0;
        if(Build.VERSION.SDK_INT>=23){
            String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            for (int i = 0; i < permissionList.length; i++) {
                if (ActivityCompat.checkSelfPermission(this,permissionList[i])==PackageManager.PERMISSION_DENIED){
                    granted=-1;
                    break;
                }
            }
            if (granted==-1){
                ActivityCompat.requestPermissions(this,permissionList,100);
            }
        }
        if (granted==-1) return;

        switch (view.getId()) {
            case R.id.btn_auth:
                login();
                break;
            case R.id.btn_wx_pay:
                payWX();
                break;
            case R.id.btn_ali_pay:
                payAli();
                break;
            case R.id.btn_share_web:
                shareWeb();
                break;
            case R.id.btn_share_image:
                shareImage();
                break;
            case R.id.btn_share_video:
                shareVideo();
                break;
        }
    }

    private void shareVideo() {
        ShareHelper.createVideoBuilder(this)
                .setSharePlatform(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setTitle("给中国军人点赞!26分钟内在天险长江上搭建一座1100米浮桥!“我们日复—日")
                .setDescription("给中国军人点赞!26分钟内在天险长江上搭建一座1100米浮桥!“我们日复—日的训练，就是要在任何时刻确保万无一失!\"")
                .setThumbnailImage("https://pp.myapp.com/ma_pic2/0/shot_9025_4_1596700826/550")
                .setVideoUrl("https://js2.a.yximgs.com/upic/2019/02/21/08/BMjAxOTAyMjEwODQzNDFfOTc0ODg5OTY4XzEwOTIwMDYyMDY3XzFfMw==_b_B10022158709b7d2a644308d079edbd90.mp4")
                .share();

    }

    private void shareImage() {

        ShareHelper.createImageBuilder(this)
                .setImage("https://pp.myapp.com/ma_pic2/0/shot_9025_1_1596700826/550")
                .setThumbnailImage("https://pp.myapp.com/ma_pic2/0/shot_9025_4_1596700826/550")
                .setSharePlatform(SHARE_MEDIA.WEIXIN)
                .share();
    }

    private String orderInfo = "app_id=2021001159638664&biz_content=%7b%22out_trade_no%22%3a%22202008262697942%22%2c%22product_code%22%3a%22FAST_INSTANT_TRADE_PAY%22%2c%22subject%22%3a%22%e5%95%86%e5%93%81%e8%b4%ad%e4%b9%b0%22%2c%22timeout_express%22%3a%2230m%22%2c%22total_amount%22%3a%220.01%22%7d&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=https%3a%2f%2fapp2.kamijie.cn%2fpay%2fapp_alipay_notify_url&sign_type=RSA2&timestamp=2020-08-26+14%3a23%3a43&version=1.0&sign=Y2WjVEANlvXBEWiZ4f0VM57dhAk%2b55c6kGVr2BakIsZpcYGPmji28JPazsSOqrkrMz697rmq3MTLLVn8JHUZu7qUf64ootaUlqPue9A9CVJ6VPf%2f5DVKu78vRYkpVoNckaQPmVlxD8T2lX1N0KBDA8TZ2xNEHya5bR8h4sWk0hYONdQta8Oo93EGUooPF1PaCsfcXRz6g7BQe0eqqgZNGYoW%2fDkxR0YrCA4zkTjVaVsrjBFOSh6FB17o7Z%2fRo0rjO5pSfZvqIypIccVQPbnCLmhMJn%2bw4scorw9cJyLuFSbPT7CuecMHDDP2CMVZEuncmYnlYFXcudCUlTxGCtNeiw%3d%3d";

    private void payAli() {

        PayHelper.createAliPayBuilder(this)
                .setOrderInfo(orderInfo)
                .setPayResultListener(new PayResultListener() {
                    @Override
                    public void onCancel() {
                        mTvHello.setText("ali pay cancel");
                    }

                    @Override
                    public void onSuccess() {
                        mTvHello.setText("ali pay Success");
                    }

                    @Override
                    public void onFailed(String error) {
                        mTvHello.setText("ali pay Failed ： " + error);
                    }
                }).pay();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
}
