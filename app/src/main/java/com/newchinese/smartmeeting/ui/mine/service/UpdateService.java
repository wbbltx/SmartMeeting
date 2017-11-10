package com.newchinese.smartmeeting.ui.mine.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;


import com.newchinese.smartmeeting.R;

import java.io.File;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class UpdateService extends Service {

    private static final int NOTIFY_ID = 1000;
    private Context mContext;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int preProgress;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mContext = this;
        return null;
    }

    private void downLoad(){
        initNotification();
    }

    /**
     * 自动更新
     *
     * @param file
     */
    private void installApk(File file) {
        Uri uri = Uri.fromFile(file);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(install);
    }

    private void initNotification() {
        builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentText("0%")
                .setContentTitle("正在更新")
                .setProgress(100, 0, false);
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 更新通知
     * @param progress
     */
    private void updateNotification(long progress){
        int curProgress = (int) progress;
        if (preProgress < curProgress){
            builder.setContentText(progress+"%");
            builder.setProgress(100,(int)progress,false);
            notificationManager.notify(NOTIFY_ID, builder.build());
        }
        preProgress = (int) progress;
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        notificationManager.cancel(NOTIFY_ID);
    }
}
