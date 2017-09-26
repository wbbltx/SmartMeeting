package com.newchinese.smartmeeting.ui.login.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.newchinese.coolpensdk.utils.GetAddressUtil;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.presenter.login.LoginPresenterImpl;
import com.newchinese.smartmeeting.ui.login.adapter.LoginPageAdapter;
import com.newchinese.smartmeeting.ui.main.activity.MainActivity;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.widget.EditView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginIView<BaseResult<LoginData>>, LoginPageAdapter.OnPageInnerClickListener, View.OnClickListener {
    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    private TabLayout mTab;
    private ViewPager mVp;
    private LoginContract.LoginIPresenter mPresenter;
    private ImageButton mIbQQ;
    private ImageButton mIbWX;
    private Disposable mDisposable;
    private ProgressDialog mPd;
    private UMShareAPI umShareAPI;
    private LoginData loginData;
    private String sms;
    private String telCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
        umShareAPI = UMShareAPI.get(this);
        initPresenter();
        initView();
        initListener();
    }

    private void initPresenter() {
        mPresenter = new LoginPresenterImpl().attach(this);
    }

    private void initListener() {
        mIbQQ.setOnClickListener(this);
        mIbWX.setOnClickListener(this);
    }

    private void initView() {
        mTab = (TabLayout) findViewById(R.id.tl_login_switch);
        mVp = (ViewPager) findViewById(R.id.vp_draw_main);
        mIbQQ = (ImageButton) findViewById(R.id.ib_login_qq);
        mIbWX = (ImageButton) findViewById(R.id.ib_login_wx);

        mTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.simple_blue));
        mTab.setTabTextColors(getResources().getColor(R.color.gray6), getResources().getColor(R.color.simple_blue));

        mVp.setAdapter(new LoginPageAdapter(this).setOnPageInnerClickListener(this).setPresenter(mPresenter));
        mTab.setupWithViewPager(mVp);
    }

    @Override
    public void skipWhat() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void updateView(BaseResult<LoginData> data) { //普通登录与三方登录成功回调
        startActivity(new Intent(this, MainActivity.class));
        SharedPreUtils.setBoolean(Constant.IS_LOGIN, true);
        finish();
    }

    @Override
    public void onInnerClick(final View v, int position) {
        if (position == 0) {//获取动态密码
            mDisposable = Flowable.intervalRange(0, 60, 0, 1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            ((EditView) v).setEnd(getString(R.string.cache_again) + "(" + (60 - aLong) + ")", false);
                        }
                    })
                    .doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            ((EditView) v).setEnd(getString(R.string.get_active_password), true);
                        }
                    })
                    .subscribe();
            telCache = (String) v.getTag(R.id.ev_regist_1);
            mPresenter.dynamicPass(telCache);
        } else {//忘记密码
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("ui", RegisterActivity.UI_TYPE_FOR);
            startActivity(intent);
        }
    }

    @Override
    public void onLogin(String phone, String pass, int position) {
        if (mPresenter != null) {
            if (position == 0) { //快捷登录
                // 判断动态密码是否一致,一致直接登录，不一致吐司
                if (loginData != null && !sms.isEmpty()) {
                    if (!phone.matches(REGEX_MOBILE) || !telCache.equals(phone)) {
                        Toast.makeText(this, getString(R.string.wrong_tel), Toast.LENGTH_SHORT).show();
                    } else if (!pass.equals(sms)) {
                        Toast.makeText(this, getString(R.string.wrong_code), Toast.LENGTH_SHORT).show();
                    } else {
                        GreenDaoUtil.getInstance().getLoginDataDao().deleteAll();
                        loginData.setTel(phone);
                        GreenDaoUtil.getInstance().getLoginDataDao().insert(loginData); //存登录数据
                        SharedPreUtils.setBoolean(Constant.IS_LOGIN, true); //设置登录状态
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        //存快捷登录类型
                        SharedPreUtils.setString(Constant.LOGIN_TYPE, "dynamic");
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.please_get_confirm_code), Toast.LENGTH_SHORT).show();
                }
            } else { //普通登录
                mPresenter.loginPhone(phone, pass);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_login_qq:
                umShareAPI.getPlatformInfo(this, SHARE_MEDIA.QQ, umAuthListener);
                break;
            case R.id.ib_login_wx:
                umShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);
                break;
        }
    }

    UMAuthListener umAuthListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Log.e("test_login", "" + data.toString());
            mPresenter.loginQQ(data.get("openid"), data.get("accessToken"));
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(LoginActivity.this, getString(R.string.login_cancle), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void showLoading(String msg) {
        mPd = mPd == null ? new ProgressDialog(this) : mPd;
        if (!TextUtils.isEmpty(msg)) {
            mPd.setMessage(msg);
            mPd.show();
        } else {
            if (mPd.isShowing()) {
                mPd.dismiss();
            }
        }
    }

    @Override
    public void getDynamicMsg(LoginData data) {
        loginData = data;
        sms = data.getSms();
        SharedPreUtils.setString(Constant.PASSWORD_FLAG, data.getFlag()); //设置是否有密码
    }
}
