package com.hzw.umeng;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hzw.umeng.result.UMShareResultImpl;
import com.hzw.umeng.utils.Utils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;

import java.lang.ref.WeakReference;

/**
 * author:HZWei
 * date:  2020/8/20
 * desc:
 */
public class ShareHelper {


    private <T extends Builder> ShareHelper(T t) {
        if (!isConditionsOK(t)) return;
        if (t instanceof WebBuilder) {
            WebBuilder builder = (WebBuilder) t;
            UMWeb umWeb = new UMWeb(builder.mWebUrl);
            umWeb.setTitle(builder.mTitle);
            umWeb.setDescription(builder.mDescription);
            umWeb.setThumb(new UMImage(builder.mActivity, builder.mThumbnailImage));

            share(new ShareAction(builder.mActivity).withMedia(umWeb), t);
        } else if (t instanceof VideoBuilder) {
            VideoBuilder builder = (VideoBuilder) t;
            UMVideo umVideo = new UMVideo(builder.mVideoUrl);
            umVideo.setTitle(builder.mTitle);
            umVideo.setDescription(builder.mDescription);
            umVideo.setThumb(new UMImage(builder.mActivity, builder.mThumbnailImage));

            share(new ShareAction(builder.mActivity).withMedia(umVideo), t);
        } else if (t instanceof ImageBuilder) {
            ImageBuilder builder = (ImageBuilder) t;
            UMImage umImage = new UMImage(builder.mActivity, builder.mImage);
            umImage.setThumb(new UMImage(builder.mActivity, builder.mThumbnailImage));

            share(new ShareAction(builder.mActivity).withMedia(umImage), t);
        }
    }



    private <T extends Builder> boolean isConditionsOK(T t) {
        if (Utils.isContextNull(t.mActivity)) return false;
        if (TextUtils.isEmpty(t.mThumbnailImage)) {
            Utils.showShort(t.mActivity, t.mActivity.getString(R.string.share_thumbnail_image_empty));
            return false;
        } else if (t.mSharePlatform == null || t.mSharePlatform.length == 0) {
            Utils.showShort(t.mActivity, t.mActivity.getString(R.string.share_platform_empty));
            return false;
        }

        if (t instanceof WebBuilder) {
            WebBuilder builder = (WebBuilder) t;
            if (TextUtils.isEmpty(builder.mWebUrl)) {
                Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.share_link_empty));
                return false;
            } else if (TextUtils.isEmpty(builder.mTitle)) {
                Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.share_title_empty));
                return false;
            } else if (TextUtils.isEmpty(builder.mDescription)) {
                Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.share_description_empty));
                return false;
            }
        } else if (t instanceof VideoBuilder) {
            VideoBuilder builder = (VideoBuilder) t;
            if (TextUtils.isEmpty(builder.mVideoUrl)) {
                Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.share_link_empty));
                return false;
            } else if (TextUtils.isEmpty(builder.mTitle)) {
                Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.share_title_empty));
                return false;
            } else if (TextUtils.isEmpty(builder.mDescription)) {
                Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.share_description_empty));
                return false;
            }
        } else if (t instanceof ImageBuilder) {
            ImageBuilder builder = (ImageBuilder) t;
            if (TextUtils.isEmpty(builder.mImage)) {
                Utils.showShort(builder.mActivity, builder.mActivity.getString(R.string.share_image_empty));
                return false;
            }
        }

        return true;
    }


    private void share(ShareAction action, Builder builder) {
        if (builder.mSharePlatform.length > 1) {
            //调用友盟自带的分享模版
            action.setDisplayList(builder.mSharePlatform)
                    .setCallback(builder.mUMShareListener != null ? builder.mUMShareListener : new UMShareResultImpl(builder.mActivity))
                    .open();
        } else {
            action.setPlatform(builder.mSharePlatform[0])
                    .setCallback(builder.mUMShareListener != null ? builder.mUMShareListener : new UMShareResultImpl(builder.mActivity))
                    .share();
        }

    }


    public static WebBuilder createWebBuilder(Activity activity) {
        return new WebBuilder(activity);
    }

    public static ImageBuilder createImageBuilder(Activity activity) {
        return new ImageBuilder(activity);
    }

    public static VideoBuilder createVideoBuilder(Activity activity) {
        return new VideoBuilder(activity);
    }


    public static  class WebBuilder extends Builder<WebBuilder> {
        private String mWebUrl;
        private String mTitle;
        private String mDescription;

        WebBuilder(Activity activity) {
            super(activity);
        }


        public WebBuilder setWebUrl(String url) {
            this.mWebUrl = url;
            return this;
        }

        public WebBuilder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public WebBuilder setDescription(String description) {
            this.mDescription = description;
            return this;
        }

        @Override
        public ShareHelper share() {
            return super.share();
        }
    }

    public static  class ImageBuilder extends Builder<ImageBuilder> {
        private String mImage;

        ImageBuilder(Activity activity) {
            super(activity);
        }

        public ImageBuilder setImage(String image) {
            this.mImage = image;
            return this;
        }


        @Override
        public ShareHelper share() {
            return super.share();
        }
    }

    public static  class VideoBuilder extends Builder<VideoBuilder> {
        private String mVideoUrl;
        private String mTitle;
        private String mDescription;

        VideoBuilder( Activity activity) {
            super(activity);
        }

        public VideoBuilder setVideoUrl(String videoUrl) {
            mVideoUrl = videoUrl;
            return this;
        }

        public VideoBuilder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public VideoBuilder setDescription(String description) {
            mDescription = description;
            return this;
        }

        @Override
        public ShareHelper share() {
            return super.share();
        }
    }


    @SuppressWarnings("all")
    static class Builder<T extends Builder> {
        Activity mActivity;
        SHARE_MEDIA[] mSharePlatform;
        UMShareResultImpl mUMShareListener;
        String mThumbnailImage;

        Builder(@NonNull Activity activity) {
            mActivity = new WeakReference<>(activity).get();
        }

        public T setSharePlatform(SHARE_MEDIA... sharePlatform) {
            mSharePlatform = sharePlatform;
            return (T) this;
        }

        public T setUMShareListener(UMShareResultImpl UMShareListener) {
            mUMShareListener = UMShareListener;
            return (T) this;
        }

        public T setThumbnailImage(String thumbnailImage) {
            mThumbnailImage = thumbnailImage;
            return (T) this;
        }

        public ShareHelper share() {
            return new ShareHelper((T) this);
        }
    }


}
