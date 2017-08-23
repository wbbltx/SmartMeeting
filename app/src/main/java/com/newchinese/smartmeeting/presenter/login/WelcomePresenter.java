package com.newchinese.smartmeeting.presenter.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.MainContract;
import com.newchinese.smartmeeting.contract.WelcomeContract;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Description:   欢迎Activity的Presenter
 * author         xulei
 * Date           2017/8/19
 */

public class WelcomePresenter extends BasePresenter<WelcomeContract.View> implements WelcomeContract.Presenter {
    private static final int TIME_COUNT = 2000;

    /**
     * 倒计时
     */
    @Override
    public void startTimer() {
        addSubscribe(Flowable.timer(TIME_COUNT, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mView.jumpActivity();
                    }
                }));
    }

    @Override
    public void onPresenterCreated() {

    }

    @Override
    public void onPresenterDestroy() {

    }

}
