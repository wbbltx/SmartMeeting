package com.newchinese.smartmeeting.presenter.record;

import android.util.Log;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.RecordsFragContract;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.model.bean.CollectPage;
import com.newchinese.smartmeeting.model.bean.CollectRecord;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

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

    @Override
    public void onPresenterCreated() {
        //初始化数据库工具类
        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPresenterDestroy() {
        singleThreadExecutor.shutdownNow();
    }

    @Override
    public void loadAllCollectRecordData() {
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                List<CollectRecord> collectRecords = collectRecordDao.queryBuilder()
                        .orderDesc(CollectRecordDao.Properties.CollectDate).list();
                Log.i("test_greendao", collectRecords.size() + "," + collectRecords.toString());
                mView.getAllCollectRecordData(collectRecords);
                for (CollectRecord record : collectRecords) {
                    List<CollectPage> collectPages = GreenDaoUtil.getInstance().getCollectPageDao().queryBuilder()
                            .where(CollectPageDao.Properties.BookId.eq(record.getId())).list();
                    Log.i("test_greendao", collectPages.size() + "," + collectPages.toString());
                }
            }
        };
        singleThreadExecutor.execute(loadRunnable);
    }
}
