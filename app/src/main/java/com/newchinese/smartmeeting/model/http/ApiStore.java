package com.newchinese.smartmeeting.model.http;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.net.LogInterceptor;
import com.newchinese.smartmeeting.net.NetUrl;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017-08-25.
 */

public class ApiStore {

    private final OkHttpClient.Builder mBuilder;
    private final Retrofit.Builder mReBuilder;
    private final Retrofit mRetrofit;

    private ApiStore() {

        mBuilder = new OkHttpClient.Builder();
        mBuilder.addInterceptor(new LogInterceptor())
                .addInterceptor(generateHeader());

        mReBuilder = new Retrofit.Builder()
                .client(mBuilder.build())
                .baseUrl(NetUrl.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        mRetrofit = mReBuilder.build();
    }

    interface ApiHolder {
        ApiStore APISTORE = new ApiStore();
    }

    public static ApiStore newInstance() {
        return ApiHolder.APISTORE;
    }

    private Interceptor generateHeader() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                PackageManager pm = App.getAppliction().getPackageManager();
                String pn = App.getAppliction().getPackageName();
                String version = "1.0";
                try {
                    PackageInfo pi = pm.getPackageInfo(pn, 0);
                    version = pi.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                request = request.newBuilder()
                        .header("User-Agent", "smartmeeting/" + version)
                        .build();
                return chain.proceed(request);
            }
        };
    }

    public <T> T getServices(Class<T> cls) {
        return mRetrofit.create(cls);
    }
}
