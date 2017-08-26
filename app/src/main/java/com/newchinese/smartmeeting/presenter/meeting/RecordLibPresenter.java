package com.newchinese.smartmeeting.presenter.meeting;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.RecordLibContract;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NoteRecord;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/26 0026.
 */

public class RecordLibPresenter extends BasePresenter<RecordLibContract.View> implements RecordLibContract.Presenter {
    private DataCacheUtil dataCacheUtil;
    private NotePageDao notePageDao;
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private NoteRecord activeNoteRecord;

    @Override
    public void onPresenterCreated() {
        dataCacheUtil = DataCacheUtil.getInstance();
        notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
    }

    @Override
    public void onPresenterDestroy() {

    }

    @Override
    public void deleteRecord(final List<String> pathList, final List<Boolean> isSelectedList, final int pageIndex) {

        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                NotePage unique = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(activeNoteRecord.getId()), NotePageDao.Properties.PageIndex.eq(pageIndex)).unique();
                List<String> screenPathList = unique.getScreenPathList();
                for (int i = 0; i < isSelectedList.size(); i++) {
                    if (isSelectedList.get(i)) {
                        String s = pathList.get(i);
                        screenPathList.remove(s);
                    }
                }
                mView.refreshRecord(screenPathList);
                unique.setScreenPathList(screenPathList);
                notePageDao.update(unique);
            }
        };
        singleThreadExecutor.execute(deleteRunnable);
    }
}
