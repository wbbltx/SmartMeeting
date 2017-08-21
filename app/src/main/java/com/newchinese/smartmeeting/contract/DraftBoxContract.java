package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.model.bean.NotePage;

import java.util.List;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface DraftBoxContract {
    interface View<E> extends BaseView<E> {

        void onScanComplete();

        void showResult(E e);

        void getActivePageList(List<NotePage> pageList);
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void initListener();

        void stopScan();

        boolean isConnected();

        void loadActivePageList();
    }
}
