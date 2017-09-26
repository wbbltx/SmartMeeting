package com.newchinese.smartmeeting.model;

import android.util.Log;

import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.ApiSubscriber;
import com.newchinese.smartmeeting.entity.http.NetError;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;

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
    public void login(final LoginData data) {
        Flowable<BaseResult<LoginData>> observable;
        observable = mServices.login(data);
        invokeRequest(NetUrl.LOGIN_NORMAL, true, Constant.LOGIN, observable.map(new Function<BaseResult<LoginData>, BaseResult<LoginData>>() {
            @Override
            public BaseResult<LoginData> apply(@NonNull BaseResult<LoginData> result) throws Exception {
                result.data.setTel(data.getTel());
                return result;
            }
        }));
    }

    @Override
    public void loginQQ(LoginData data) {
        invokeRequest(NetUrl.LOGIN_QQ, true, Constant.LOGIN_QQ, mServices.loginQQ(data));
    }

    @Override
    public void loginWeChat(LoginData data) {

    }


    @Override
    public void regist(LoginData data) {
        invokeRequest(NetUrl.REGIST, true, "regist", mServices.regist(data));
    }

    @Override
    public void forget(LoginData data) {
        invokeRequest(NetUrl.FORGET_PASS, true, "forget", mServices.forget(data));
    }

    @Override
    public void verify(LoginData data) {
        invokeRequest(NetUrl.VERIFY_CODE, false, "verify", mServices.verify(data));
    }

    @Override
    public void dynamic(LoginData data) {
        invokeRequest(NetUrl.DYNAMIC_PASS, false, Constant.LOGIN_DYNAMIC, mServices.dynamic(data));
    }

    @Override
    public void updateNick(LoginData data) {
        invokeRequest(NetUrl.UPDATE_NICK, true, "updateNick", mServices.updateNick(data));
    }

    @Override
    public void updateIcon(LoginData data) {
        invokeRequest(NetUrl.UPDATE_ICON, true, "updateIcon", mServices.updateIcon(data)
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

    private <T> void invokeRequest(final String url, final boolean update, final String type, Flowable<BaseResult<T>> observable) {
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
                        Log.e("test_login", "onFail:" + error.getMessage());
                        mPresenter.onResult(false, type, result);
                    }

                    @Override
                    public void onNext(BaseResult<T> loginDataBaseResult) {
                        if (mPresenter != null) {
                            loginDataBaseResult.url = url;
                            loginDataBaseResult.update = update;
                            Log.e("test_login", "onNext:" + loginDataBaseResult.toString());
                            mPresenter.onResult(true, type, loginDataBaseResult);
                        }
                    }
                });
    }
}
