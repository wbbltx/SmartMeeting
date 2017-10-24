package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;

import java.util.List;

/**
 * Created by Administrator on 2017/10/23 0023.
 */

public interface CollectToAddListContract {
    interface View<E> extends BaseView<E>{

        void showActivePage(List<NotePage> notePageList);

        void showToast(String string);

    }

    interface Presenter extends BaseSimplePresenter<CollectToAddListContract.View>{

        void loadLeftPage(NoteRecord noteRecord);

        void transferPage(List<NotePage> notePageList, List<Boolean> isSelectedList);

    }
}
