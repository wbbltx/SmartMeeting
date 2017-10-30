package com.newchinese.smartmeeting.presenter.record;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.CollectToAddListContract;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.manager.CollectPageManager;
import com.newchinese.smartmeeting.manager.CollectRecordManager;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/23 0023.
 */

public class CollectToAddListPresenter extends BasePresenter<CollectToAddListContract.View> implements CollectToAddListContract.Presenter {
    private NotePageDao notePageDao;
    private CollectPageDao collectPageDao;
    private CollectRecord activeCollectRecord;
    private CollectPageManager collectPageManager;
    private List<Boolean> isSelectedList;
    private List<NotePage> notePageList;
    private ExecutorService singleThreadExecutor;
    private NoteRecord activeNoteRecord;

    @Override
    public void onPresenterCreated() {
        notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
        collectPageDao = GreenDaoUtil.getInstance().getCollectPageDao();
        collectPageManager = CollectPageManager.getInstance();

        isSelectedList = new ArrayList<>();
        notePageList = new ArrayList<>();

        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPresenterDestroy() {
        singleThreadExecutor.shutdownNow();
    }

    @Override
    public void loadLeftPage(final NoteRecord activeNoteRecord) {
        this.activeNoteRecord = activeNoteRecord;

        Observable.create(new ObservableOnSubscribe<List<NotePage>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<NotePage>> e) throws Exception {
                List<NotePage> notePageLists = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(activeNoteRecord.getId()))
                        .orderDesc(NotePageDao.Properties.Date).list();
                e.onNext(notePageLists);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NotePage>>() {
                    @Override
                    public void accept(List<NotePage> notePageList) throws Exception {
                        if (mView != null) {
                            mView.showActivePage(notePageList);
                        }
                    }
                });
    }

    @Override
    public void transferPage(List<NotePage> notePages, List<Boolean> isSelecteds) {

        isSelectedList.clear();
        isSelectedList.addAll(isSelecteds);
        notePageList.clear();
        notePageList.addAll(notePages);

        activeCollectRecord = DataCacheUtil.getInstance().getActiveCollectRecord();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < isSelectedList.size(); i++) {
                    if (isSelectedList.get(i)) {
                        //存收藏页
                        NotePage selectPage = notePageList.get(i);
                        collectPageManager.insertCollectPage(collectPageDao, activeCollectRecord.getId(),
                                selectPage.getPageIndex(), selectPage.getDate(),
                                selectPage.getThumbnailPath(), selectPage.getScreenPathList());
                        //删记录页
                        notePageDao.delete(selectPage);
                    }
                }
                loadLeftPage(activeNoteRecord);
            }
        };

        if (!singleThreadExecutor.isShutdown()) {
            singleThreadExecutor.execute(runnable);
        }

        if (mView != null) {
            mView.showToast("添加成功！");
        }

    }


}
