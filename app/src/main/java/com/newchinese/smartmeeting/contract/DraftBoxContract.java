package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface DraftBoxContract {
    interface View extends BaseView {

    }

    interface Presenter extends BaseSimplePresenter<View> {
        boolean isBleOpen();

        void openBle();

    }
}
