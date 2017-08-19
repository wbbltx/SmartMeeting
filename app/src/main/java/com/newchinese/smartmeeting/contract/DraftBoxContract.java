package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface DraftBoxContract {
    interface View<E> extends BaseView<E> {

    }

    interface Presenter extends BaseSimplePresenter<View> {

    }
}
