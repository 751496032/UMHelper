package com.hzw.umeng.result;

import com.hzw.umeng.entity.UMAuthResult;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * author:HZWei
 * date:  2020/8/21
 * desc:
 */
public interface UMAuthResultListener {

    void onResult(UMAuthResult result, SHARE_MEDIA media, Map<String, String> map);
}
