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

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity;

import java.io.File;

/**
 * Description:   插入图片底部弹出PopupWindow
 * author         xulei
 * Date           2017/8/28 11:13
 */
public class TakePhotoPopWin extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private View view;

    public TakePhotoPopWin(Context mContext) {
        this.mContext = mContext;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.take_photo_pop, null);
        Button btn_take_photo = (Button) view.findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = (Button) view.findViewById(R.id.btn_pick_photo);
        Button btn_edit_photo = (Button) view.findViewById(R.id.btn_cancel_photo);
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
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
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
                ((DrawingBoardActivity) mContext).startActivityForResult(intent, Constant.TAKEPHOTO_SAVE_MYPATH);
                break;
            case R.id.btn_pick_photo:
                Intent albmIntent = new Intent(Intent.ACTION_PICK);
                albmIntent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                ((DrawingBoardActivity) mContext).startActivityForResult(albmIntent, Constant.SELECT_PIC_KITKAT);
                break;
            case R.id.btn_cancel_photo:
                this.dismiss();
                break;
        }
    }
}
