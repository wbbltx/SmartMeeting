package com.newchinese.smartmeeting.entity.listener;

import android.content.Context;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.ui.record.activity.CollectPageDetailActivity;
import com.umeng.analytics.MobclickAgent;
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
     * @param platform 平台类型
     * @descrption 分享开始的回调
     */
    @Override
    public void onStart(SHARE_MEDIA platform) {
//        Toast.makeText(context, "分享的平台：" + platform.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * @param platform 平台类型
     * @descrption 分享成功的回调
     */
    @Override
    public void onResult(SHARE_MEDIA platform) {
        MobclickAgent.onEvent(context, "share", platform.toString());
        Toast.makeText(context, context.getString(R.string.share_success), Toast.LENGTH_LONG).show();
    }

    /**
     * @param platform 平台类型
     * @param t        错误原因
     * @descrption 分享失败的回调
     */
    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
        Toast.makeText(context, context.getString(R.string.share_failed) + t.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * @param platform 平台类型
     * @descrption 分享取消的回调
     */
    @Override
    public void onCancel(SHARE_MEDIA platform) {
        Toast.makeText(context, context.getString(R.string.share_cancel), Toast.LENGTH_LONG).show();

    }
}
