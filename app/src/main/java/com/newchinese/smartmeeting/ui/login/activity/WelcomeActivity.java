package com.newchinese.smartmeeting.ui.login.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.WelcomeActContract;
import com.newchinese.smartmeeting.database.LoginDataDao;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.presenter.login.WelcomePresenter;
import com.newchinese.smartmeeting.ui.main.activity.MainActivity;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;

/**
 * Description:   欢迎页
 * author         xulei
 * Date           2017/8/18 13:22
 */
public class WelcomeActivity extends BaseActivity<WelcomePresenter, View> implements WelcomeActContract.View<View> {
    // 要申请的权限
    private String[] permissions = {Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.SET_DEBUG_APP,
            Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WRITE_APN_SETTINGS
    };

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
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        boolean isThrough = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int permissionStatus = ContextCompat.checkSelfPermission(this, permission);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    ActivityCompat.requestPermissions(this, permissions, 321);
                    isThrough = false;
                    break;
                }
            }
            if (isThrough) {
                mPresenter.startTimer();
            }
        } else {
            mPresenter.startTimer();
        }
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
        if (SharedPreUtils.getBoolean("isFirstInstall", true)) { //首次安装则跳引导页
            intent = new Intent(WelcomeActivity.this, GuideActivity.class);
        } else {
            LoginDataDao loginDataDao = GreenDaoUtil.getInstance().getLoginDataDao();
            LoginData loginData = loginDataDao.queryBuilder().unique();
            //判断是否登录过
            boolean isLogin = SharedPreUtils.getBoolean(Constant.IS_LOGIN, false);
            if (loginData != null && isLogin) {
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            } else {
                intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            mPresenter.startTimer();
        }
    }
}
