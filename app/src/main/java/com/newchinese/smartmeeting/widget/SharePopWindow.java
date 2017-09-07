package com.newchinese.smartmeeting.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.listener.OnShareListener;

/**
 * Description:   弹出是否开启蓝牙的框
 * Date           2017/8/18
 */

public class SharePopWindow extends PopupWindow implements View.OnClickListener{

    private final View mView;
    private final Activity context;
    private final OnShareListener onShareListener;

    public SharePopWindow(Activity context, OnShareListener onShareListener) {
        super(context);
        this.context = context;
        this.onShareListener = onShareListener;
        mView =  View.inflate(context,R.layout.share_platform_layout,null);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mView = inflater.inflate(R.layout.ble_popup_layout,null);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();

        mView.findViewById(R.id.share_qq).setOnClickListener(this);
        mView.findViewById(R.id.share_qzone).setOnClickListener(this);
        mView.findViewById(R.id.share_weibo).setOnClickListener(this);
        mView.findViewById(R.id.share_moments).setOnClickListener(this);
        mView.findViewById(R.id.share_wechat).setOnClickListener(this);
        mView.findViewById(R.id.share_cancel).setOnClickListener(this);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(width*2/3);
        //设置PopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(height/2);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popup_anim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x333333);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.setOutsideTouchable(true);

        setFocusable(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_qzone:
                onShareListener.onShare("0");
                dismiss();
                break;

            case R.id.share_qq:
                onShareListener.onShare("1");
                dismiss();
                break;

            case R.id.share_moments:
                onShareListener.onShare("2");
                dismiss();
                break;

            case R.id.share_wechat:
                onShareListener.onShare("3");
                dismiss();
                break;

            case R.id.share_weibo:
                onShareListener.onShare("4");
                dismiss();
                break;
            case R.id.share_cancel:
                dismiss();
                break;
        }
    }
}
