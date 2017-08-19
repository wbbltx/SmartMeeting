package com.newchinese.smartmeeting.app;

import android.app.Application;

import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

/**
 * Description:   自定义Application
 * author         xulei
 * Date           2017/8/18
 */
public class App extends Application {
    public static final String APPKEY = "1308e911d0841bf20922d075dfaab229";

    @Override
    public void onCreate() {
        super.onCreate();
        //在Application中初始化酷神笔API
        DrawingboardAPI.getInstance().init(getApplicationContext(), APPKEY);
        //初始化GreenDao
        GreenDaoUtil.getInstance().initDataBase(getApplicationContext());
    }
}
