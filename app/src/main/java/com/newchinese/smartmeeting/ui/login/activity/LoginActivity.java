package com.newchinese.smartmeeting.ui.login.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.presenter.login.LoginPresenterImpl;
import com.newchinese.smartmeeting.ui.login.adapter.LoginPageAdapter;
import com.newchinese.smartmeeting.ui.main.activity.MainActivity;
import com.newchinese.smartmeeting.widget.EditView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginIView<BaseResult<LoginData>>, LoginPageAdapter.OnPageInnerClickListener, View.OnClickListener {

    private TabLayout mTab;
    private ViewPager mVp;
    private LoginContract.LoginIPresenter mPresenter;
    private ImageButton mIbQQ;
    private ImageButton mIbWX;
    private Disposable mDisposable;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
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

        mVp.setAdapter(new LoginPageAdapter().setOnPageInnerClickListener(this).setPresenter(mPresenter));
        mTab.setupWithViewPager(mVp);
    }

    @Override
    public void skipWhat() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void updateView(BaseResult<LoginData> data) {
        if (!NetUrl.DYNAMIC_PASS.equals(data.url)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onInnerClick(final View v, int position) {
        if (position == 0) {//获取动态密码
            mDisposable = Flowable.intervalRange(0, 60, 0, 1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            ((EditView) v).setEnd("重新获取(" + (60 - aLong) + ")", false);
                        }
                    })
                    .doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            ((EditView) v).setEnd("获取动态密码", true);
                        }
                    })
                    .subscribe();
            mPresenter.dynamicPass((String) v.getTag(R.id.ev_regist_1));
        } else {//忘记密码
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("ui", RegisterActivity.UI_TYPE_FOR);
            startActivity(intent);
        }
    }

    @Override
    public void onLogin(String phone, String pass, int position) {
        if (mPresenter != null) {
            if (position == 0) {
                mPresenter.loginQuick(phone, pass);
            } else {
                mPresenter.loginPhone(phone, pass);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_login_qq:

                break;
            case R.id.ib_login_wx:

                break;
        }
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
}
