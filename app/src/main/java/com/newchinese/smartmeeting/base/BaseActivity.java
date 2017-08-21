package com.newchinese.smartmeeting.base;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;


/**
 * Description:   基于MVP的基本Activity
 * author         xulei
 * Date           2017/8/17
 */

public abstract class BaseActivity<T extends BasePresenter, E> extends BaseSimpleActivity implements BaseView<E> {
    protected T mPresenter;

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        //初始化Presenter
        mPresenter = initPresenter();
        //给Presenter绑定View
        if (mPresenter != null) {
            mPresenter.attachView(this);
            mPresenter.onPresenterCreated();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter.onPresenterDestroy();
        }
    }

    protected abstract T initPresenter();
}
