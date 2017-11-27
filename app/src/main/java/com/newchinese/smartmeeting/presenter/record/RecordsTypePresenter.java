package com.newchinese.smartmeeting.presenter.record;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.contract.RecordTypeContract;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/21 0021.
 */

public class RecordsTypePresenter extends BasePresenter<RecordTypeContract.View> implements RecordTypeContract.Presenter {


    private static final String TAG = "RecordsTypePresenter";
    private CollectRecordDao collectRecordDao;

    @Override
    public void onPresenterCreated() {
//初始化数据库工具类
        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
    }

    @Override
    public void onPresenterDestroy() {

    }

    @Override
    public void loadRecordsPages(String className) {
        List<CollectRecord> collectRecords = null;
        if (className != null) {
            if (className.equals("全部")) {
                collectRecords = collectRecordDao.queryBuilder().orderDesc(CollectRecordDao.Properties.CollectDate).list();
            } else {
                collectRecords = collectRecordDao.queryBuilder().where(CollectRecordDao.Properties.ClassifyName.eq(className)).list();
            }
            if (mView != null) {
                mView.getRightCollectRecordData(collectRecords);
            }
        }
    }
}
