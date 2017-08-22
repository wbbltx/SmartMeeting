package com.newchinese.smartmeeting.base;

import android.view.View;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.log.XLog;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Description:   基于RxJava的Presenter封装,控制订阅的生命周期
 * author         xulei
 * Date           2017/8/17 16:04
 */
public abstract class BasePresenter<T extends BaseView> implements BaseSimplePresenter<T> {
    private static final String TAG = "BasePresenter";
    protected T mView;
    protected CompositeDisposable mCompositeDisposable;
    public static final int BSTATE_DISCONNECT = 0;
    public static final int BSTATE_CONNECTED = 1;
    public static final int BSTATE_CONNECTING = 2;
//    protected static String BSTATE_DISCONNECT;

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    /**
     * 解绑Presenter
     */
    public void detachView() {
        this.mView = null;
        unSubscribe();
    }

    /**
     * 添加RxJava订阅
     */
    protected void addSubscribe(Disposable subscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    /**
     * 取消RxJava订阅
     */
    protected void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    public void updatePenState(int state, View view) {
        int i = 0;
        XLog.d(TAG,"设置图标状态 "+state);
        switch (state) {
            case BSTATE_CONNECTED:
                i = R.mipmap.pen_succes;
//                view.clearAnimation();
                break;

            case BSTATE_CONNECTING:
                i = R.mipmap.pen_loading;
                break;

            case BSTATE_DISCONNECT:
                i = R.mipmap.pen_break;
//                view.clearAnimation();
                break;
        }
        view.setBackgroundResource(i);
    }
}
