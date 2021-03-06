package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;

import java.util.List;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface RecordsFragContract {
    interface View extends BaseView {
        void getAllCollectRecordData(List<CollectRecord> collectRecordList);
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void loadAllCollectRecordData();
        
        void searchCollectRecordByName(String name);
    }
}
