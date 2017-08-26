package com.newchinese.smartmeeting.presenter.record;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.CollectRecordFilterActContract;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.model.bean.CollectRecord;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:   收藏页列表Activity的Presenter
 * author         xulei
 * Date           2017/8/26
 */

public class CollectRecordFilterActPresenter extends BasePresenter<CollectRecordFilterActContract.View>
        implements CollectRecordFilterActContract.Presenter {
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private CollectRecordDao collectRecordDao;

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

    /**
     * 根据分类查询收藏记录
     */
    @Override
    public void loadCollectPageDataByClassify(final String classifyName) {
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                List<CollectRecord> collectRecords = collectRecordDao.queryBuilder()
                        .where(CollectRecordDao.Properties.ClassifyName.eq(classifyName)).list();
                mView.getFilterCollectRecordData(collectRecords);
            }
        };
        singleThreadExecutor.execute(loadRunnable);
    }
}
