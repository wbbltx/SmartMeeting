package com.newchinese.smartmeeting.presenter.record;

import android.util.Log;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.CollectPageListActContract;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.model.bean.CollectPage;
import com.newchinese.smartmeeting.model.bean.CollectRecord;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:   收藏页列表Activity的Presenter
 * author         xulei
 * Date           2017/8/25
 */

public class CollectPageListActPresenter extends BasePresenter<CollectPageListActContract.View> implements CollectPageListActContract.Presenter {
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private CollectPageDao collectRecordDao;
    private CollectRecord activeCollectRecord;

    @Override
    public void onPresenterCreated() {
        //初始化数据库工具类
        collectRecordDao = GreenDaoUtil.getInstance().getCollectPageDao();
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        //当前活动收藏记录
        activeCollectRecord = DataCacheUtil.getInstance().getActiveCollectRecord();
    }

    @Override
    public void onPresenterDestroy() {
        singleThreadExecutor.shutdownNow();
    }

    /**
     * 数据库读取当前收藏记录表中所有收藏页
     */
    @Override
    public void loadAllCollectPageData() {
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                List<CollectPage> collectPages = collectRecordDao.queryBuilder()
                        .where(CollectPageDao.Properties.BookId.eq(activeCollectRecord.getId())).list();
                Log.e("test_greendao", collectPages.size() + "," + collectPages.toString());
                mView.getAllCollectPageData(collectPages);
            }
        };
        singleThreadExecutor.execute(loadRunnable);
    }
}
