package com.newchinese.smartmeeting.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;

import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.List;

/**
 * Description:   自定义Application
 * author         xulei
 * Date           2017/8/18
 */
public class App extends Application {
    public static final String APPKEY = "1308e911d0841bf20922d075dfaab229";
    public static App appliction;
    private ComponentName topActivity; //栈顶Activity名字

    public static App getAppliction() {
        return appliction;
    }

    public ComponentName getTopActivity() {
        return topActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appliction = this;
        //获取栈顶Activity
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        topActivity = runningTaskInfo.topActivity;
        //在Application中初始化酷神笔API
        DrawingboardAPI.getInstance().init(getApplicationContext(), APPKEY);
        //初始化GreenDao
        GreenDaoUtil.getInstance().initDataBase(getApplicationContext());
    }
}
