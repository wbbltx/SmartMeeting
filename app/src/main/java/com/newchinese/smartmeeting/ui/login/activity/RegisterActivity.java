package com.newchinese.smartmeeting.ui.login.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.model.bean.BaseResult;
import com.newchinese.smartmeeting.model.bean.LoginData;
import com.newchinese.smartmeeting.net.NetUrl;
import com.newchinese.smartmeeting.presenter.login.LoginPresenterImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.widget.EditView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import static android.os.Build.VERSION_CODES.M;

public class RegisterActivity extends AppCompatActivity implements LoginContract.LoginIView<BaseResult<LoginData>>, EditView.OnEditViewListener, View.OnClickListener {

    public static final int UI_TYPE_REG = 0;//注册界面
    public static final int UI_TYPE_FOR = 1;//忘记密码
    public static final int UI_TYPE_UPD = 2;//完善资料
    private static final int CAMERA_CODE = 1001;
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
    private File mFile;
    private ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    private ProgressDialog mPd;

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
                        CustomizedToast.showShort(this,"请填写正确资料");
                    }
                }
                break;
            case R.id.iv_regist_icon:
                new AlertDialog.Builder(this)
                        .setItems(getResources().getStringArray(R.array.selectIcon), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectIcon(which);
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }

    private void selectIcon(int which) {
        Intent intent = new Intent();
        switch (which) {
            case 0:
                if (Build.VERSION.SDK_INT >= M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
                } else {
                    mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile(), "icon.jpg");
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                }
                break;
            case 1:
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                break;
        }
        startActivityForResult(intent, which);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Glide.with(this)
                    .load(mFile)
                    .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().override(300))
                    .into(mIvIcon);
            Observable.just(mFile)
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) throws Exception {
                            mBaos = file2Byte(file);
                        }
                    });
        } else if (requestCode == 1 && data != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(data.getData())
                    .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().override(300))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            resource.compress(Bitmap.CompressFormat.PNG, 100, mBaos);
                            mIvIcon.setImageBitmap(resource);
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

    private String byte2String (byte[] src) {
        return new String(Base64.encode(src, Base64.DEFAULT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_CODE) {
            Intent intent = new Intent();
            mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile(), "icon.jpg");
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void showLoading(String msg) {
        mPd = mPd == null ? new ProgressDialog(this) : mPd;
        if (!TextUtils.isEmpty(msg)) {
            mPd.setMessage(msg);
            mPd.show();
        } else {
            if (mPd.isShowing()) {
                mPd.hide();
            }
        }
    }
}
