package com.newchinese.smartmeeting.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.newchinese.smartmeeting.listener.PopWindowListener;

/**
 * Description:   基于MVP的基本Fragment
 * author         xulei
 * Date           2017/8/17
 */

public abstract class BaseFragment<T extends BasePresenter> extends BaseSimpleFragment implements BaseView {
    protected T mPresenter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = initPresenter();
        if (mPresenter != null) mPresenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) mPresenter.detachView();
        super.onDestroyView();
    }

    protected abstract T initPresenter();

    @Override
    public void onScanComplete() {

    }

    @Override
    public void showResult(Object o) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onHistoryDetected(String str, PopWindowListener popWindowListener) {

    }

    @Override
    public void onElecReceived(String ele) {

    }
}
