package com.newchinese.smartmeeting.model;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.contract.AboutContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.RequestVersion;
import com.newchinese.smartmeeting.entity.bean.VersionInfo;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.ApiSubscriber;
import com.newchinese.smartmeeting.entity.http.NetError;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;
import com.newchinese.smartmeeting.ui.mine.service.UpdateService;
import com.newchinese.smartmeeting.util.DeviceUtils;
import com.newchinese.smartmeeting.util.log.XLog;

import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class AboutModelImp implements AboutContract.AboutIModel {

    private final AboutContract.AboutIPresenter mPresenter;
    private final ApiService mServices;
    private final Context context;
    private String interiorAppUrl;

    public AboutModelImp(AboutContract.AboutIPresenter aboutIPresenter, Context context) {
        this.mPresenter = aboutIPresenter;
        this.context = context;
        XApi.registerProvider(new NetProviderImpl());
        mServices = XApi.get(NetUrl.THOST, ApiService.class);
    }

    @Override
    public void checkVersion() {
        RequestVersion requestVersion = new RequestVersion().setPlatform("1").setVersion("1.0");

        Flowable<BaseResult<VersionInfo>> baseResultFlowable = mServices.checkVersion(requestVersion);

        baseResultFlowable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        if (mPresenter != null) {
                            mPresenter.loading();
                        }
                    }
                })
                .compose(XApi.<BaseResult<VersionInfo>>getApiTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber<BaseResult<VersionInfo>>() {
                    @Override
                    protected void onFail(NetError error) {
                        XLog.d("hahehe", " onFail " + error.getMessage());
                    }

                    @Override
                    public void onNext(BaseResult<VersionInfo> versionInfoBaseResult) {
                        VersionInfo data = versionInfoBaseResult.data;
                        XLog.d("hahehe", versionInfoBaseResult.msg + " ++ " + data);
                        String versionCode = DeviceUtils.getVersionCode(App.getAppliction());
                        if (Integer.parseInt(versionCode) < Integer.parseInt(data.getVersion())) {
                            interiorAppUrl = data.getInteriorAppUrl();
                            mPresenter.showDialog();
                        }
                    }
                });
    }

    @Override
    public void downLoad() {
        context.startService(new Intent(context,UpdateService.class));
    }
}
