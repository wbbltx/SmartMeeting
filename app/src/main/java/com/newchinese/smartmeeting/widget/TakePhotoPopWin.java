package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.ui.login.activity.RegisterActivity;
import com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity;
import com.newchinese.smartmeeting.ui.mine.activity.SettingActivity;

import java.io.File;

/**
 * Description:   插入图片底部弹出PopupWindow
 * author         xulei
 * Date           2017/8/28 11:13
 */
public class TakePhotoPopWin extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private View view;
    private String type;

    public TakePhotoPopWin(Context mContext, String type) {
        this.mContext = mContext;
        this.type = type;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.take_photo_pop, null);
        TextView btn_take_photo = (TextView) view.findViewById(R.id.btn_take_photo);
        TextView btn_pick_photo = (TextView) view.findViewById(R.id.btn_pick_photo);
        TextView btn_edit_photo = (TextView) view.findViewById(R.id.btn_cancel_photo);
        // 设置按钮监听
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
        btn_edit_photo.setOnClickListener(this);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x333333);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.popup_anim);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用照相机
                Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//命令相机返回的uri指定到此处
                if ("SettingActivity".equals(type))
                    ((SettingActivity) mContext).startActivityForResult(intent, Constant.TAKEPHOTO_SAVE_MYPATH);
                else if ("DrawingBoardActivity".equals(type))
                    ((DrawingBoardActivity) mContext).startActivityForResult(intent, Constant.TAKEPHOTO_SAVE_MYPATH);
                else if ("RegisterActivity".equals(type))
                    ((RegisterActivity) mContext).startActivityForResult(intent, Constant.TAKEPHOTO_SAVE_MYPATH);
                break;
            case R.id.btn_pick_photo:
                Intent albmIntent = new Intent();
                albmIntent.setType("image/*");
                albmIntent.setAction(Intent.ACTION_GET_CONTENT);
//                Intent albmIntent = new Intent(Intent.ACTION_PICK);
//                albmIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                if ("SettingActivity".equals(type)){
                    albmIntent =new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    ((SettingActivity) mContext).startActivityForResult(albmIntent, Constant.SELECT_PIC_KITKAT);
                }
                else if ("DrawingBoardActivity".equals(type))
                    ((DrawingBoardActivity) mContext).startActivityForResult(albmIntent, Constant.SELECT_PIC_KITKAT);
                else if ("RegisterActivity".equals(type))
                    ((RegisterActivity) mContext).startActivityForResult(albmIntent, Constant.SELECT_PIC_KITKAT);
                break;
            case R.id.btn_cancel_photo:
                this.dismiss();
                break;
        }
    }
}
