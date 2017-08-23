package com.newchinese.smartmeeting.contract;

import android.app.Activity;
import android.content.Context;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Description:   欢迎Activity的Contract
 * author         xulei
 * Date           2017/8/19
 */

public interface WelcomeContract {
    interface View<E> extends BaseView<E> {
        void jumpActivity();
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void startTimer();
    }
}
