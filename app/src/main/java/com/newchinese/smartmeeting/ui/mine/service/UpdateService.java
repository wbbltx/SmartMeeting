package com.newchinese.smartmeeting.ui.mine.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.Kits;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;
import com.newchinese.smartmeeting.entity.http.download.DownloadProgressHandler;
import com.newchinese.smartmeeting.entity.http.download.ProgressHelper;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.log.XLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class UpdateService extends Service {

    private static final int NOTIFY_ID = 1000;
    private Context mContext;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int preProgress;
    private String versionPath;
    private static final String TAG = "UpdateService";
    private Retrofit.Builder retrofit;
    //    interiorAppUrl='http://182.92.99.12:8080/images/M00/00/68/tlxjDFoFBLOAPtacAJc2K8rX4sk675.apk'
//                        http://182.92.99.12:8080/images/M00/00/69/tlxjDFoKsXeAfFkuAIANTG_1BTI289.apk

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        versionPath = intent.getStringExtra(BluCommonUtils.VERSION_PATH);
        XLog.d(TAG," onStartCommand "+ versionPath);
        downLoad();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mContext = this;
        versionPath = intent.getStringExtra(BluCommonUtils.VERSION_PATH);

        return null;
    }

    private void downLoad() {
        initNotification();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder();
        }
        Call<ResponseBody> responseBodyCall = retrofit.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://182.92.99.12:8080/images/M00/00/69/")
                .client(ProgressHelper.addProgress(null).build())
                .build()
                .create(ApiService.class)
                .loadApk();

        ProgressHelper.setProgressHandler(new DownloadProgressHandler() {
            @Override
            protected void onProgress(long progress, long total, boolean done) {
                updateNotification(progress*100/total);
                if (done){
                    cancelNotification();
                }
            }
        });

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    saveFile(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public File saveFile(retrofit2.Response<ResponseBody> response) throws Exception {
        InputStream in = null;
        FileOutputStream out = null;
        byte[] buf = new byte[2048*10];
        int len;
        try {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "SmartMeeting" + "/" + "apk" +"/");
            if (!dir.exists()) {// 如果文件不存在新建一个
                dir.mkdirs();
            }
            in = response.body().byteStream();
            File file = new File(dir,"smartingmeeting_V"+ Kits.Package.getVersionName(mContext)+".apk");
            out = new FileOutputStream(file);
            while ((len = in.read(buf)) != -1){
                out.write(buf,0,len);
            }
            // 回调成功的接口
            installApk(file);
            return file;
        }finally {
            in.close();
            out.close();
        }
    }

    /**
     * 初始化OkHttpClient
     *
     * @return
     */
    private OkHttpClient initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(100000, TimeUnit.SECONDS);
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse
                        .newBuilder()
//                        .body(new FileResponseBody(originalResponse))
                        .build();
            }
        });
        return builder.build();
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
                .setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP? R.mipmap.app_icon_alpha:R.mipmap.app_icon)
                .setContentText("0%")
                .setContentTitle("正在更新")
                .setProgress(100, 0, false);
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 更新通知
     *
     * @param progress
     */
    private void updateNotification(long progress) {
        int curProgress = (int) progress;
        if (preProgress < curProgress) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, (int) progress, false);
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
