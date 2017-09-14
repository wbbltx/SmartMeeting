package com.newchinese.smartmeeting.base;

import com.newchinese.smartmeeting.entity.listener.PopWindowListener;

/**
 * Description:   基于MVP的基本Fragment
 * author         xulei
 * Date           2017/8/17
 */

public abstract class BaseFragment<T extends BasePresenter> extends BaseSimpleFragment implements BaseView {
    protected T mPresenter;

    @Override
    protected void onFragViewCreated() {
        mPresenter = initPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
            mPresenter.onPresenterCreated();
        }
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter.onPresenterDestroy();
        }
        super.onDestroyView();
    }

    protected abstract T initPresenter();

}
