package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;

import java.util.List;

/**
 * Description:   收藏页列表Activity的Contract
 * author         xulei
 * Date           2017/8/25
 */

public interface CollectPageListActContract {
    interface View<E> extends BaseView<E> {
        void getAllCollectPageData(List<CollectPage> collectPages);
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void loadAllCollectPageData();
    }
}
