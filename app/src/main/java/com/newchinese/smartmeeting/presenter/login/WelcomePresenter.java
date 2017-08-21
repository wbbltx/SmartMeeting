package com.newchinese.smartmeeting.presenter.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.MainContract;
import com.newchinese.smartmeeting.contract.WelcomeContract;

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
    // 要申请的权限
    private String[] permissions = {Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.WAKE_LOCK,
    };

    /**
     * 6.0及以上主动请求权限
     */
    @Override
    public void requestPermissing(Activity activity) {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int permissionStatus = ContextCompat.checkSelfPermission(activity, permission);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    ActivityCompat.requestPermissions(activity, permissions, 321);
                    break;
                }
            }
        }
    }

    /**
     * 倒计时
     */
    @Override
    public void startTimer() {
        addSubscribe(Flowable.timer(TIME_COUNT, TimeUnit.MILLISECONDS)
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

    @Override
    public boolean isBluetoothOpen() {
        return false;
    }

    @Override
    public void openBle() {

    }

    @Override
    public void scanBlueDevice() {

    }
}
