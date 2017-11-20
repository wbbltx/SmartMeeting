package com.newchinese.smartmeeting.model;

import android.content.Context;
import android.content.Intent;

import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.contract.AboutContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.RequestVersion;
import com.newchinese.smartmeeting.entity.bean.VersionInfo;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.ApiSubscriber;
import com.newchinese.smartmeeting.entity.http.Kits;
import com.newchinese.smartmeeting.entity.http.NetError;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;
import com.newchinese.smartmeeting.ui.mine.service.UpdateService;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.log.XLog;

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
    private static final String TAG = "AboutModelImp";
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
                        XLog.d(TAG, " onFail " + error.getMessage());
                    }

                    @Override
                    public void onNext(BaseResult<VersionInfo> versionInfoBaseResult) {
                        VersionInfo data = versionInfoBaseResult.data;
                        XLog.d(TAG, versionInfoBaseResult.msg + " ++ " + data);
                        int versionCode = Kits.Package.getVersionCode(App.getAppliction());
                        if ((versionCode) < data.getVersion()) {
                            interiorAppUrl = data.getInteriorAppUrl();
                            mPresenter.showDialog();
                        }else {
                            XLog.d(TAG, " 已经是最新版本！ ");
//                            提示已经是最新版本
//                            CustomizedToast.showLong(context, context.getString(R.string.already_new));
                        }
                    }
                });
    }

    @Override
    public void downLoad() {
        XLog.d(TAG, " 开启服务下载apk");
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(BluCommonUtils.VERSION_PATH,interiorAppUrl);
        context.startService(intent);
    }
}
