package com.newchinese.smartmeeting.presenter.record;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.EditRecordContract;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/11/23 0023.
 */

public class EditRecordsPresenter extends BasePresenter<EditRecordContract.View> implements EditRecordContract.Presenter {
    private CollectRecordDao collectRecordDao;
    private CollectPageDao collectPageDao;
    private List<Boolean> isSelectedList;
    private List<CollectRecord> notePageList;
    private String typeName;
    private ExecutorService singleThreadExecutor; //单核心线程线程池

    @Override
    public void onPresenterCreated() {
        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
        collectPageDao = GreenDaoUtil.getInstance().getCollectPageDao();
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        isSelectedList = new ArrayList<>();
        notePageList = new ArrayList<>();
    }

    @Override
    public void onPresenterDestroy() {

    }

    @Override
    public void queryCollectRecords(String typeName) {
        List<CollectRecord> collectRecords = null;
        this.typeName = typeName;
        if (typeName != null) {
            if (typeName.equals("全部")) {
                collectRecords = collectRecordDao.queryBuilder().orderDesc(CollectRecordDao.Properties.CollectDate).list();
            } else {
                collectRecords = collectRecordDao.queryBuilder().where(CollectRecordDao.Properties.ClassifyName.eq(typeName)).list();
            }
            if (mView != null) {
                mView.showQueryResult(collectRecords);
            }
        }
    }

    @Override
    public void deleteCollectRecords(List<CollectRecord> collectRecords, List<Boolean> isSelecteds) {
        isSelectedList.clear();
        isSelectedList.addAll(isSelecteds);
        notePageList.clear();
        notePageList.addAll(collectRecords);
        for (int i = 0; i < isSelecteds.size(); i++) {
            if (isSelecteds.get(i)) {
                CollectRecord collectRecord = collectRecords.get(i);
                List<CollectPage> pageList = collectRecord.getPageList();
                for (CollectPage collectPage : pageList) {
                    collectPageDao.delete(collectPage);
                }
                collectRecordDao.delete(collectRecord);
            }
        }
        queryCollectRecords(typeName);
        mView.showToast("删除成功");
    }

    /**
     * 重命名收藏表
     *
     * @param collectRecords
     * @param isSelecteds
     * @param newName
     */
    @Override
    public void reNameCollectRecords(List<CollectRecord> collectRecords, List<Boolean> isSelecteds, String newName) {
        isSelectedList.clear();
        isSelectedList.addAll(isSelecteds);
        notePageList.clear();
        notePageList.addAll(collectRecords);
        for (int i = 0; i < isSelecteds.size(); i++) {
            if (isSelecteds.get(i)) {
                CollectRecord collectRecord = collectRecords.get(i);
                collectRecord.setCollectRecordName(newName);
                collectRecordDao.update(collectRecord);
            }
        }
        queryCollectRecords(typeName);
        mView.showToast("修改成功");
    }

}
