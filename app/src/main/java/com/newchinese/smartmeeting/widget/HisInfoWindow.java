package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.listener.PopWindowListener;

/**
 * Description:   弹出是否开启蓝牙的框
 * Date           2017/8/18
 */

public class HisInfoWindow extends PopupWindow implements View.OnClickListener {

    private final View mView;
    private final PopWindowListener popWindowListener;
    private TextView stay_close;
    private TextView open_bluetooth;
    private TextView title;

    public HisInfoWindow(Context context, PopWindowListener popWindowListener) {
        super(context);
        this.popWindowListener = popWindowListener;
        mView =  View.inflate(context,R.layout.layout_hisinfo_dialog,null);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mView = inflater.inflate(R.layout.ble_popup_layout,null);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();

        stay_close = (TextView) mView.findViewById(R.id.negativeButton);
        open_bluetooth = (TextView) mView.findViewById(R.id.positiveButton);
        title = (TextView) mView.findViewById(R.id.title);
        open_bluetooth.setOnClickListener(this);
        stay_close.setOnClickListener(this);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        this.setHeight(height/4);
        //设置PopupWindow弹出窗体可点击
//        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popup_anim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x3fffff);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.setOutsideTouchable(false);
    }

    public void setHintText(String confirmText,String cancelText,String title){
        open_bluetooth.setText(confirmText);
        stay_close.setText(cancelText);
        this.title.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.negativeButton:
                popWindowListener.onCancel(0);
                dismiss();
                break;

            case R.id.positiveButton:
                popWindowListener.onConfirm(0);
                dismiss();
                break;
        }
    }
}
