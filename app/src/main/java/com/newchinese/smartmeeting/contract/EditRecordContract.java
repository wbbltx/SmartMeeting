package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;

import java.util.List;

/**
 * Created by Administrator on 2017/11/23 0023.
 */

public interface EditRecordContract {
    interface View<E> extends BaseView<E>{
        void showQueryResult(List<CollectRecord> collectRecordList);

        void showToast(String info);

    }

    interface Presenter extends BaseSimplePresenter<View> {
        void queryCollectRecords(String typeName);

        void deleteCollectRecords(List<CollectRecord> collectRecords,List<Boolean> isSelecteds);

        void reNameCollectRecords(List<CollectRecord> collectRecords,List<Boolean> isSelecteds,String newName);
    }
}
