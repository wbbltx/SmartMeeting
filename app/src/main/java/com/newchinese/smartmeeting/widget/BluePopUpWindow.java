package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.log.XLog;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public class BluePopUpWindow extends PopupWindow implements View.OnClickListener {

    private final View mView;
    private final PopWindowListener popWindowListener;
    private TextView stay_close;
    private TextView open_bluetooth;
    private TextView title;

    public BluePopUpWindow(Context context, PopWindowListener popWindowListener) {
        super(context);
        this.popWindowListener = popWindowListener;
        mView =  View.inflate(context,R.layout.ble_popup_layout,null);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mView = inflater.inflate(R.layout.ble_popup_layout,null);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();

        stay_close = (TextView) mView.findViewById(R.id.stay_close);
        open_bluetooth = (TextView) mView.findViewById(R.id.open_bluetooth);
        title = (TextView) mView.findViewById(R.id.title);
        open_bluetooth.setOnClickListener(this);
        stay_close.setOnClickListener(this);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(height/4);
        //设置PopupWindow弹出窗体可点击
//        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popup_anim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x3fffff);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.setOutsideTouchable(true);
    }

    public void setHintText(String confirmText,String cancelText,String title){
        open_bluetooth.setText(confirmText);
        stay_close.setText(cancelText);
        this.title.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stay_close:
                dismiss();
                break;

            case R.id.open_bluetooth:
                popWindowListener.onConfirm(0);
                dismiss();
                break;
        }
    }
}
