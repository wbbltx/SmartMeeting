package com.newchinese.smartmeeting.model.model;

import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.model.bean.BaseResult;
import com.newchinese.smartmeeting.model.bean.LoginData;
import com.newchinese.smartmeeting.net.ApiService;
import com.newchinese.smartmeeting.net.ApiSubscriber;
import com.newchinese.smartmeeting.net.NetError;
import com.newchinese.smartmeeting.net.NetProviderImpl;
import com.newchinese.smartmeeting.net.NetUrl;
import com.newchinese.smartmeeting.net.XApi;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017-08-25.
 */

public class LoginModelImpl implements LoginContract.LoginIModel {

    private final ApiService mServices;
    private LoginContract.LoginIPresenter mPresenter;

    public LoginModelImpl(LoginContract.LoginIPresenter presenter) {

        mPresenter = presenter;
        XApi.registerProvider(new NetProviderImpl());
        mServices = XApi.get(NetUrl.HOST, ApiService.class);
    }

    @Override
    public void login(final LoginData data, boolean quick) {
        Flowable<BaseResult<LoginData>> observable;
        if (quick) {
            observable = mServices.loginQuict(data);
        } else {
            observable = mServices.login(data);
        }
        invokeRequest(quick ? NetUrl.QUICK_LOGIN : NetUrl.LOGIN_NORMAL, true, observable.map(new Function<BaseResult<LoginData>, BaseResult<LoginData>>() {
            @Override
            public BaseResult<LoginData> apply(@NonNull BaseResult<LoginData> result) throws Exception {
                result.data.setTel(data.getTel());
                return result;
            }
        }));
    }


    @Override
    public void regist(LoginData data) {
        invokeRequest(NetUrl.REGIST, true, mServices.regist(data));
    }

    @Override
    public void forget(LoginData data) {
        invokeRequest(NetUrl.FORGET_PASS, true,  mServices.forget(data));
    }

    @Override
    public void verify(LoginData data) {
        invokeRequest(NetUrl.VERIFY_CODE, false, mServices.verify(data));
    }

    @Override
    public void dynamic(LoginData data) {
        invokeRequest(NetUrl.DYNAMIC_PASS, false, mServices.dynamic(data));
    }

    @Override
    public void updateNick(LoginData data) {
        invokeRequest(NetUrl.UPDATE_NICK, true, mServices.updateNick(data));
    }

    @Override
    public void updateIcon(LoginData data) {
        invokeRequest(NetUrl.UPDATE_ICON, true, mServices.updateIcon(data)
        .concatMap(new Function<BaseResult<String>, Publisher<BaseResult<LoginData>>>() {
            @Override
            public Publisher<BaseResult<LoginData>> apply(@NonNull BaseResult<String> data) throws Exception {
                BaseResult<LoginData> result = new BaseResult<>();
                result.no = data.no;
                result.msg = data.msg;
                result.data = new LoginData().setIcon(data.data);
                return Flowable.just(result);
            }
        }));
    }

    private <T> void invokeRequest(final String url, final boolean update, Flowable<BaseResult<T>> observable) {
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
