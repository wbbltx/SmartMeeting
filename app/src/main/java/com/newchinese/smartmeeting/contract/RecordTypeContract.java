package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;

import java.util.List;

/**
 * Created by Administrator on 2017/11/21 0021.
 */

public interface RecordTypeContract {
    interface View extends BaseView{

        void getRightCollectRecordData(List<CollectRecord> collectRecords);

    }

    interface Presenter extends BaseSimplePresenter<View>{

        void loadRecordsPages(String className);

    }
}
