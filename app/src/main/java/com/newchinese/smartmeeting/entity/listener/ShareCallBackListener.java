package com.newchinese.smartmeeting.entity.listener;

import android.content.Context;
import android.widget.Toast;

import com.newchinese.smartmeeting.ui.record.activity.CollectPageDetailActivity;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by Administrator on 2017/8/30 0030.
 */

public class ShareCallBackListener implements UMShareListener {

    private final Context context;

    public ShareCallBackListener(Context context) {
        this.context = context;
    }

    /**
     * @descrption 分享开始的回调
     * @param platform 平台类型
     */
    @Override
    public void onStart(SHARE_MEDIA platform) {
//        Toast.makeText(context, "分享的平台：" + platform.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * @descrption 分享成功的回调
     * @param platform 平台类型
     */
    @Override
    public void onResult(SHARE_MEDIA platform) {
        Toast.makeText(context, "分享成功", Toast.LENGTH_LONG).show();
    }

    /**
     * @descrption 分享失败的回调
     * @param platform 平台类型
     * @param t 错误原因
     */
    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
        Toast.makeText(context, "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * @descrption 分享取消的回调
     * @param platform 平台类型
     */
    @Override
    public void onCancel(SHARE_MEDIA platform) {
        Toast.makeText(context, "取消分享", Toast.LENGTH_LONG).show();

    }
}
