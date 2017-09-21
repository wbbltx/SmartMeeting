package com.newchinese.smartmeeting.ui.login.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.presenter.login.LoginPresenterImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.widget.EditView;
import com.newchinese.smartmeeting.widget.TakePhotoPopWin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity implements LoginContract.LoginIView<BaseResult<LoginData>>, EditView.OnEditViewListener, View.OnClickListener {
    public static final int UI_TYPE_REG = 0;//注册界面
    public static final int UI_TYPE_FOR = 1;//忘记密码
    public static final int UI_TYPE_UPD = 2;//完善资料
    private String mBtnTitles[] = {"注 册", "确 定", "完 成"};
    private String mTitles[] = {"注册", "忘记密码", "完善个人资料"};
    private EditView mEvPhone, mEvCode, mEvPass;
    private LoginContract.LoginIPresenter mPresenter;
    private Button mBtnReg;
    private TextView mTvSkip;
    private Disposable mDisposable;
    private EditView mEvPass2;
    private int mUi;
    private ImageView mIvIcon;
    private RelativeLayout mRlIcon;
    private EditText mEtNick;
    private ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    private ProgressDialog mPd;
    private TakePhotoPopWin takePhotoPopWin;
    private Bitmap headerBitmap;
    private File headerFile;
    private static String path = "/sdcard/myHead/";//sd路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
        initIntent();
        super.onCreate(savedInstanceState);
        initPresenter();
        initView();
        updateBtn();
        initListener();
    }

    private void initIntent() {
        mUi = getIntent().getIntExtra("ui", 0);
        setTitle(mTitles[mUi]);
    }

    private void initPresenter() {
        mPresenter = new LoginPresenterImpl().attach(this);
    }

    private void initListener() {
        mEvPhone.setOnEditViewListener(this);
        mEvCode.setOnEditViewListener(this);
        mEvPass.setOnEditViewListener(this);
        mEvPass2.setOnEditViewListener(this);
        mBtnReg.setOnClickListener(this);
        if (mUi == UI_TYPE_UPD) {
            mIvIcon.setOnClickListener(this);
        }
    }

    private void initView() {
        mEvPhone = (EditView) findViewById(R.id.ev_regist_1);
        mEvCode = (EditView) findViewById(R.id.ev_regist_2);
        mEvPass = (EditView) findViewById(R.id.ev_regist_3);
        mEvPass2 = (EditView) findViewById(R.id.ev_regist_4);
        mBtnReg = (Button) findViewById(R.id.btn_regist_sub);
        mTvSkip = (TextView) findViewById(R.id.tv_regist_bottom);
        if (mUi == UI_TYPE_UPD) {
            mRlIcon = (RelativeLayout) findViewById(R.id.rl_regist_icon);
            mIvIcon = (ImageView) findViewById(R.id.iv_regist_icon);
            mEtNick = (EditText) findViewById(R.id.et_regist_nick);

            mRlIcon.setVisibility(mUi == UI_TYPE_UPD ? View.VISIBLE : View.GONE);

            mEtNick.setLinkTextColor(getResources().getColor(R.color.simple_blue));
            mEtNick.setHintTextColor(getResources().getColor(R.color.gray6));
            mEtNick.setGravity(Gravity.CENTER);
        }

        mEvPass2.setVisibility(mUi == UI_TYPE_FOR ? View.VISIBLE : View.GONE);
        mEvPhone.setVisibility(mUi == UI_TYPE_UPD ? View.INVISIBLE : View.VISIBLE);
        mEvCode.setVisibility(mUi == UI_TYPE_UPD ? View.INVISIBLE : View.VISIBLE);
        mEvPass.setVisibility(mUi == UI_TYPE_UPD ? View.INVISIBLE : View.VISIBLE);

        mEvPhone.setEditType(EditView.EDIT_TYPE_PHONE);
        mEvPass.setEditType(EditView.EDIT_TYPE_PASS);
        mEvPass2.setEditType(EditView.EDIT_TYPE_PASS);

        mEvPhone.configure("手机号码", "");
        mEvCode.configure("输入验证码", "获取验证码");
        mEvPass.configure("密码", "");
        mEvPass2.configure("再次输入密码", "");

        if (mPresenter != null) {
            mPresenter.getSpan(mTvSkip, mUi == UI_TYPE_REG ? "已有账号？去登录" : "");
        }

        mBtnReg.setText(mBtnTitles[mUi]);

        takePhotoPopWin = new TakePhotoPopWin(this, "RegisterActivity");
    }

    @Override
    public void skipWhat() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void updateView(BaseResult<LoginData> data) {
        if (data.update) {
            finish();
        }
        if (NetUrl.REGIST.equals(data.url)) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("ui", UI_TYPE_UPD);
            startActivity(intent);
        }
    }

    @Override
    public void onMatch(EditView view, boolean matching) {
        updateBtn();
    }

    private void updateBtn() {
        boolean enabled = mUi == UI_TYPE_UPD ? mEtNick != null && TextUtils.isEmpty(mEtNick.getText().toString()) : (mEvPhone.mMatching && mEvPass.mMatching && mEvCode.mMatching);
        if (mUi == UI_TYPE_FOR) {
            enabled &= mEvPass2.getText().equals(mEvPass.getText());
        }
        mBtnReg.setEnabled(enabled);
        ((GradientDrawable) mBtnReg.getBackground()).setColor(enabled ? getResources().getColor(R.color.simple_blue) : Color.parseColor("#999999"));
    }

    @Override
    public void onEndClick(View v) {
        switch (v.getId()) {
            case R.id.ev_regist_2:
                if (mEvPhone.mMatching) {
                    //获取验证码
                    mPresenter.verifyCode(mEvPhone.getText());
                    mDisposable = Flowable.intervalRange(0, 60, 0, 1, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    mEvCode.setEnd("重新获取(" + (60 - aLong) + ")", false);
                                }
                            })
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    mEvCode.setEnd("获取验证码", true);
                                }
                            })
                            .subscribe();
                } else {
                    CustomizedToast.showShort(this, "手机号不正确");
                }
                break;
        }
    }

    @Override
    public void onEditError(String err) {
        CustomizedToast.showShort(App.getAppliction(), err);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist_sub:
                if (mUi == UI_TYPE_REG) {
                    mPresenter.regist(mEvPhone.getText(), mEvPass.getText(), mEvCode.getText());
                } else if (mUi == UI_TYPE_FOR) {
                    mPresenter.forgetPass(mEvPhone.getText(), mEvCode.getText(), mEvPass.getText());
                } else if (mUi == UI_TYPE_UPD) {
                    final String nick = mEtNick.getText().toString().trim();
                    if (mBaos.size() != 0 && nick.length() < 9 && nick.length() > 0) {
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
                                    public void accept(String s) throws Exception {
                                        mPresenter.uploadInfo(nick, s);
                                    }
                                });
                    } else {
                        CustomizedToast.showShort(this, "请填写正确资料");
                    }
                }
                break;
            case R.id.iv_regist_icon:
                InputMethodManager imm = (InputMethodManager) findViewById(R.id.rl_draw_base).getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(findViewById(R.id.rl_draw_base).getApplicationWindowToken(), 0);
                }
                takePhotoPopWin.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
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
                                        .load(headerFile)
                                        .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().override(300))
                                        .into(mIvIcon);
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
                        }
                    });
        }
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
    public void showLoading(String msg) {
        mPd = mPd == null ? new ProgressDialog(this) : mPd;
        if (!TextUtils.isEmpty(msg) && !mPd.isShowing()) {
            mPd.setMessage(msg);
            mPd.show();
        } else {
            if (mPd.isShowing()) {
                mPd.hide();
            }
        }
    }

    @Override
    public void getDynamicMsg(BaseResult<LoginData> data) {
        
    }
}
