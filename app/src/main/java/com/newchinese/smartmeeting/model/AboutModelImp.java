package com.newchinese.smartmeeting.model;

import com.newchinese.smartmeeting.contract.AboutContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.VersionInfo;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.ApiSubscriber;
import com.newchinese.smartmeeting.entity.http.NetError;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;

import org.reactivestreams.Subscription;

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

    public AboutModelImp(AboutContract.AboutIPresenter aboutIPresenter) {
        this.mPresenter = aboutIPresenter;
        XApi.registerProvider(new NetProviderImpl());
        mServices = XApi.get(NetUrl.HOST, ApiService.class);
    }

    @Override
    public void checkVersion() {
        Flowable<BaseResult<VersionInfo>> baseResultFlowable = mServices.checkVersion();
        baseResultFlowable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        mPresenter.loading();
                    }
                })
                .compose(XApi.<BaseResult<VersionInfo>>getApiTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber<BaseResult<VersionInfo>>() {
                    @Override
                    protected void onFail(NetError error) {

                    }

                    @Override
                    public void onNext(BaseResult<VersionInfo> versionInfoBaseResult) {

                    }
                });
    }
}
