package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.listener.PopWindowListener;

/**
 * Description:   弹出是否开启蓝牙的框
 * Date           2017/8/18
 */

public class MenuPopUpWindow extends PopupWindow implements View.OnClickListener {

    private final View mView;
    private RelativeLayout rlGuiDang;
    private RelativeLayout rlDelete;
    private OnMenuClicked onMenuClicked;

    public MenuPopUpWindow(Context context) {
        super(context);
        mView =  View.inflate(context,R.layout.layout_menu,null);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mView = inflater.inflate(R.layout.ble_popup_layout,null);

        rlGuiDang = (RelativeLayout) mView.findViewById(R.id.rl_menu_guidang);
        rlDelete = (RelativeLayout) mView.findViewById(R.id.rl_menu_delete);
//        tvGuiDang = (TextView) mView.findViewById(R.id.tv_menu_guidang);
//        tvDelete = (TextView) mView.findViewById(R.id.tv_menu_delete);
        rlDelete.setOnClickListener(this);
        rlGuiDang.setOnClickListener(this);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(178);
        //设置PopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(191);
        //设置PopupWindow弹出窗体可点击
//        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.popup_anim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x3fffff);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.setOutsideTouchable(true);
    }

    public interface OnMenuClicked{
        void onMenuClicked(int flag);
    }

    public void setOnMenuClicked(OnMenuClicked onMenuClicked){
        this.onMenuClicked = onMenuClicked;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_menu_guidang:
                onMenuClicked.onMenuClicked(1);
                dismiss();
                break;

            case R.id.rl_menu_delete:
                onMenuClicked.onMenuClicked(2);
                dismiss();
                break;
        }
    }
}
