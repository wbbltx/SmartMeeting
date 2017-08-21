package com.newchinese.smartmeeting.ui.login.activity;

import android.content.Intent;
import android.view.View;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.WelcomeContract;
import com.newchinese.smartmeeting.presenter.login.WelcomePresenter;
import com.newchinese.smartmeeting.ui.main.activity.MainActivity;

/**
 * Description:   欢迎页
 * author         xulei
 * Date           2017/8/18 13:22
 */
public class WelcomeActivity extends BaseActivity<WelcomePresenter, View> implements WelcomeContract.View<View> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected WelcomePresenter initPresenter() {
        return new WelcomePresenter();
    }

    @Override
    protected void initStateAndData() {
        mPresenter.requestPermissing(this);
        mPresenter.startTimer();
    }

    @Override
    protected void initListener() {
    }

    /**
     * 欢迎页2秒后跳登录页
     */
    @Override
    public void jumpActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
