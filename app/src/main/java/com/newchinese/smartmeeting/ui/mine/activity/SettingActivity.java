package com.newchinese.smartmeeting.ui.mine.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.database.LoginDataDao;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;
import com.newchinese.smartmeeting.ui.login.activity.LoginActivity;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.widget.TakePhotoPopWin;

import org.reactivestreams.Publisher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 设置Activity
 */
public class SettingActivity extends BaseSimpleActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.rl_nick_name)
    RelativeLayout rlNickName;
    @BindView(R.id.rl_change_pwd)
    RelativeLayout rlChangePwd;
    private TakePhotoPopWin takePhotoPopWin;
    private Bitmap headerBitmap;
    private LoginData loginData;
    private LoginDataDao loginDataDao;
    private File headerFile;
    private ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    private ApiService mServices;
    private Flowable<BaseResult<LoginData>> observable;
    private ProgressDialog mPd;
    private static String path = "/sdcard/myHead/";//sd路径

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        takePhotoPopWin = new TakePhotoPopWin(this, "SettingActivity");
        mPd = new ProgressDialog(this);
        mPd.setMessage("正在请求...");
    }

    @Override
    protected void initStateAndData() {
        ivPen.setVisibility(View.GONE);
        tvTitle.setText("设置");
        loginDataDao = GreenDaoUtil.getInstance().getLoginDataDao();
        loginData = loginDataDao.queryBuilder().unique();
        XApi.registerProvider(new NetProviderImpl());
        mServices = XApi.get(NetUrl.HOST, ApiService.class);
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.iv_back, R.id.rl_header, R.id.rl_nick_name, R.id.rl_change_pwd, R.id.btn_exit_login})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_back: //返回
                finish();
                break;
            case R.id.rl_header: //修改头像
                if (loginData.getCode() != null && !loginData.getCode().isEmpty()) { //排除三方登录与快捷登录
                    takePhotoPopWin.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.rl_nick_name: //修改昵称
                if (loginData.getCode() != null && !loginData.getCode().isEmpty()) { //排除三方登录与快捷登录
                    intent = new Intent(SettingActivity.this, ChangeNickNameActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_change_pwd: //修改密码
                if (loginData.getCode() != null && !loginData.getCode().isEmpty()) { //排除三方登录与快捷登录
                    intent = new Intent(SettingActivity.this, UpdateActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                }
                break;
            case R.id.btn_exit_login: //退出登录
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //清空用户LoginData表
                        GreenDaoUtil.getInstance().getLoginDataDao().deleteAll();
                    }
                }).start();
                SharedPreUtils.setBoolean(Constant.IS_LOGIN, false);
                intent = new Intent(SettingActivity.this, LoginActivity.class);
                //清空所有栈中Activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginData = loginDataDao.queryBuilder().unique();
        if (loginData != null) {
            Log.e("test_greendao", "" + loginData.toString());
            tvNickName.setText(loginData.getNickname() + "");
            Glide.with(this)
                    .load(loginData.getIcon())
                    .into(ivHeader);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.SELECT_PIC_KITKAT: //选择照片返回
                    cropPhoto(data.getData());//裁切图片
                    break;
                case Constant.TAKEPHOTO_SAVE_MYPATH: //拍照返回
                    File file = new File(Environment.getExternalStorageDirectory() + "/image.jpg");
                    cropPhoto(Uri.fromFile(file));//裁剪图片
                    break;
                case Constant.CROP_HEADER: //裁剪返回
                    if (headerBitmap != null && !headerBitmap.isRecycled()) {
                        headerBitmap.recycle();
                        headerBitmap = null;
                        System.gc();
                    }
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        headerBitmap = extras.getParcelable("data");
                        if (headerBitmap != null) {
                            String fileName = setPicToView(headerBitmap);//保存到SD卡中
                            if (!TextUtils.isEmpty(fileName)) {
                                headerFile = new File(fileName);
                                chansformHeaderFile();
                                Glide.with(this)
                                        .load(headerBitmap)
                                        .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().override(300))
                                        .into(ivHeader);
                                ivHeader.setImageBitmap(headerBitmap);
                                takePhotoPopWin.dismiss();
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 调用系统的裁剪
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 头像存SD卡
     */
    private String setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return "";
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "header.png";//图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 80, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /**
     * 头像文件转换为Byte数组再转换为String
     */
    private void chansformHeaderFile() {
        if (headerFile != null) {
            Observable.just(headerFile)
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) throws Exception {
                            mBaos = file2Byte(file);
                            chansformBaos();
                        }
                    });
        }
    }

    /**
     * 转换流
     */
    private void chansformBaos() {
        if (mBaos.size() != 0) {
            Observable.fromArray(mBaos.toByteArray())
                    .subscribeOn(Schedulers.io())
                    .map(new Function<byte[], String>() {
                        @Override
                        public String apply(@io.reactivex.annotations.NonNull byte[] bytes) throws Exception {
                            return byte2String(mBaos.toByteArray());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String icon) throws Exception {
                            mBaos.close();
                            updateHeader(icon);
                        }
                    });
        }
    }

    /**
     * 上传图片
     */
    private void updateHeader(String icon) {
        mPd.setMessage("正在请求...");
        mPd.show();
        loginData.setIcon(icon).setIcon_format("png");
        observable = mServices.updateIcon(loginData)
                .concatMap(new Function<BaseResult<String>, Publisher<BaseResult<LoginData>>>() {
                    @Override
                    public Publisher<BaseResult<LoginData>> apply(@NonNull BaseResult<String> data) throws Exception {
                        BaseResult<LoginData> result = new BaseResult<>();
                        result.no = data.no;
                        result.msg = data.msg;
                        result.data = new LoginData().setIcon(data.data);
                        return Flowable.just(result);
                    }
                });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<LoginData>>() {
                    @Override
                    public void accept(BaseResult<LoginData> loginDataBaseResult) throws Exception {
                        Log.e("test_http", "" + loginDataBaseResult.toString());
                        Toast.makeText(SettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        loginData.setIcon(loginDataBaseResult.data.getIcon());
                        loginDataDao.update(loginData);
                        Glide.with(SettingActivity.this)
                                .load(loginData.getIcon())
                                .transition(new DrawableTransitionOptions().crossFade(1000)) //淡入淡出1s
                                .into(ivHeader);
                        mPd.dismiss();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("test_http", "上传图片结束：" + System.currentTimeMillis());
                        Log.e("test_http", "请求错误：" + throwable.getMessage());
                        Toast.makeText(SettingActivity.this, "请求错误", Toast.LENGTH_SHORT).show();
                        mPd.dismiss();
                    }
                });
    }

    private ByteArrayOutputStream file2Byte(File file) {
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((len = fis.read(bytes)) != -1) {
                    baos.write(bytes, 0, len);
                }
                fis.close();
                return baos;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String byte2String(byte[] src) {
        return new String(Base64.encode(src, Base64.DEFAULT));
    }

    @Override
    protected void onDestroy() {
        if (mPd.isShowing()) {
            mPd.dismiss();
        }
        if (headerBitmap != null && !headerBitmap.isRecycled()) {
            headerBitmap.recycle();
            headerBitmap = null;
        }
        if (mBaos != null) {
            try {
                mBaos.flush();
                mBaos.close();
                System.gc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.gc();
        super.onDestroy();
    }
}
