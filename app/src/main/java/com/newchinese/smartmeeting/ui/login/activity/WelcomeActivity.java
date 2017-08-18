package com.newchinese.smartmeeting.ui.login.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.ui.main.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:   欢迎页
 * author         xulei
 * Date           2017/8/18 13:22
 */
public class WelcomeActivity extends AppCompatActivity {
    // 要申请的权限
    private String[] permissions = {Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.WAKE_LOCK,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        requestPermissing();
        jumpActivity();
    }

    /**
     * 欢迎页2秒后跳登录页
     */
    private void jumpActivity() {
        Timer timer;
        TimerTask task;
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(task, 2000);
    }

    /**
     * 6.0及以上主动请求权限
     */
    private void requestPermissing() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int permissionStatus = ContextCompat.checkSelfPermission(this, permission);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    ActivityCompat.requestPermissions(this, permissions, 321);
                    break;
                }
            }
        }
    }
}
