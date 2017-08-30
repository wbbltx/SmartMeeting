package com.newchinese.smartmeeting.model.model;

import com.newchinese.smartmeeting.contract.MineContract;
import com.newchinese.smartmeeting.model.bean.BaseResult;
import com.newchinese.smartmeeting.model.bean.FeedBack;
import com.newchinese.smartmeeting.model.bean.LoginData;
import com.newchinese.smartmeeting.net.ApiService;
import com.newchinese.smartmeeting.net.ApiSubscriber;
import com.newchinese.smartmeeting.net.NetError;
import com.newchinese.smartmeeting.net.NetProviderImpl;
import com.newchinese.smartmeeting.net.NetUrl;
import com.newchinese.smartmeeting.net.XApi;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017-08-30.
 */

public class UpdateModelImpl implements MineContract.UpdateIModel {

    private final ApiService mServices;
    private MineContract.UpdateIPresenter mPresenter;

    public UpdateModelImpl(MineContract.UpdateIPresenter presenter) {

        this.mPresenter = presenter;
        XApi.registerProvider(new NetProviderImpl());
        mServices = XApi.get(NetUrl.HOST, ApiService.class);
    }

    @Override
    public void updateNick(LoginData data) {
        invokeRequest(NetUrl.UPDATE_NICK, true, mServices.updateNick(data));
    }

    @Override
    public void updatePass(LoginData data) {
        invokeRequest(NetUrl.UPDATE_PASS, false, mServices.updatePass(data));
    }

    @Override
    public void feedBack(FeedBack data) {
        invokeRequest(NetUrl.FEED_BACK, false, mServices.feedBack(data));
    }

    protected <T> void invokeRequest(final String url, final boolean update, Flowable<BaseResult<T>> observable) {
        observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        if (mPresenter != null) {
                            mPresenter.loading();
                        }
                    }
                })
                .compose(XApi.<BaseResult<T>>getApiTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber<BaseResult<T>>() {
                    @Override
                    protected void onFail(NetError error) {
                        BaseResult<String> result = new BaseResult<>();
                        result.msg = error.getMessage();
                        mPresenter.onResult(false, result);
                    }

                    @Override
                    public void onNext(BaseResult<T> loginDataBaseResult) {
                        if (mPresenter != null) {
                            loginDataBaseResult.url = url;
                            loginDataBaseResult.update = update;
                            mPresenter.onResult(true, loginDataBaseResult);
                        }
                    }
                });
    }
}
