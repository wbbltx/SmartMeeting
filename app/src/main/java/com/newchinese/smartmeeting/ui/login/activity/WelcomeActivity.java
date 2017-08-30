package com.newchinese.smartmeeting.ui.login.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.WelcomeActContract;
import com.newchinese.smartmeeting.database.LoginDataDao;
import com.newchinese.smartmeeting.model.bean.LoginData;
import com.newchinese.smartmeeting.presenter.login.WelcomePresenter;
import com.newchinese.smartmeeting.ui.main.activity.MainActivity;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

/**
 * Description:   欢迎页
 * author         xulei
 * Date           2017/8/18 13:22
 */
public class WelcomeActivity extends BaseActivity<WelcomePresenter, View> implements WelcomeActContract.View<View> {
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
        Intent intent;
        LoginDataDao loginDataDao = GreenDaoUtil.getInstance().getLoginDataDao();
        LoginData loginData = loginDataDao.queryBuilder().unique();
        //判断是否登录过
        if (loginData != null && loginData.getCode() != null && !loginData.getCode().isEmpty()) {
            intent = new Intent(WelcomeActivity.this, MainActivity.class);
        } else {
            intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
