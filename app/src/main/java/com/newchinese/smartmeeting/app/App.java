package com.newchinese.smartmeeting.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.entity.listener.ActivityLife;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Description:   自定义Application
 * author         xulei
 * Date           2017/8/18
 */
public class App extends Application {
    public static final String APPKEY = "1308e911d0841bf20922d075dfaab229";
    public static App appliction;

    public static App getAppliction() {
        return appliction;
    }

    public static Context getContext() {
        return appliction.getApplicationContext();
    }

    {
        PlatformConfig.setWeixin("wx79a3fe537606f1e5", "973ae992268dc9d31ec6aeb53128ed57");
        PlatformConfig.setQQZone("1106336859", "WtanYiSsdzvTci9y");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appliction = this;
        UMShareAPI.get(this);
        //在Application中初始化酷神笔API
        DrawingboardAPI.getInstance().init(getApplicationContext(), APPKEY);
        //初始化GreenDao
        GreenDaoUtil.getInstance().initDataBase(getApplicationContext());
        registerActivityLifecycleCallbacks(new ActivityLife());
        getLastColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    //设置书写页色板最后一个颜色
    private void getLastColor() {
        int lastcolor = SharedPreUtils.getInteger(this, "lastcolor", -1);
        if (lastcolor != -1) Constant.colors[5] = lastcolor;
    }
}
