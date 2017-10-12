package com.newchinese.smartmeeting.presenter.record;

import android.util.Log;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.RecordsFragContract;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public class RecordsFragPresenter extends BasePresenter<RecordsFragContract.View> implements RecordsFragContract.Presenter {
    private CollectRecordDao collectRecordDao;
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private List<CollectRecord> searchCollectRecordList;

    @Override
    public void onPresenterCreated() {
        searchCollectRecordList = new ArrayList<>();
        //初始化数据库工具类
        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPresenterDestroy() {
        singleThreadExecutor.shutdownNow();
    }

    /**
     * 查询出所有收藏记录
     */
    @Override
    public void loadAllCollectRecordData() {
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                List<CollectRecord> collectRecords = collectRecordDao.queryBuilder()
                        .orderDesc(CollectRecordDao.Properties.CollectDate).list();
                mView.getAllCollectRecordData(collectRecords);
            }
        };
        singleThreadExecutor.execute(loadRunnable);
    }

    /**
     * 根据关键字name匹配查询
     */
    @Override
    public void searchCollectRecordByName(final String name) {
        searchCollectRecordList.clear();
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                List<CollectRecord> collectRecords = collectRecordDao.queryBuilder()
                        .orderDesc(CollectRecordDao.Properties.CollectDate).list();
                for (CollectRecord collectRecord : collectRecords) {
                    if (collectRecord.getCollectRecordName().contains(name)) {
                        searchCollectRecordList.add(collectRecord);
                    }
                }
                mView.getAllCollectRecordData(searchCollectRecordList);
            }
        };
        singleThreadExecutor.execute(loadRunnable);
    }


}
