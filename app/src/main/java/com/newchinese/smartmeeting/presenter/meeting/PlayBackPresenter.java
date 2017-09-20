package com.newchinese.smartmeeting.presenter.meeting;

import android.util.Log;

import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.PlayBackContract;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NotePoint;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.entity.bean.NoteStroke;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class PlayBackPresenter extends BasePresenter<PlayBackContract.View> implements PlayBackContract.Presenter {

    private DataCacheUtil dataCacheUtil;
    private NoteRecord activeNoteRecord;
    private NotePageDao notePageDao;
    private NoteStrokeDao noteStrokeDao;
    private NotePointDao notePointDao;
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private NotePage currentSelectPage;
    private int progressMax;
    private ArrayList<com.newchinese.coolpensdk.entity.NotePoint> playBackList;
    @Override
    public void onPresenterCreated() {
        dataCacheUtil = DataCacheUtil.getInstance();
        notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
        noteStrokeDao = GreenDaoUtil.getInstance().getNoteStrokeDao();
        notePointDao = GreenDaoUtil.getInstance().getNotePointDao();
        //获取缓存的当前活动页与本
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPresenterDestroy() {
        dataCacheUtil.setPlayBackList(null);
        dataCacheUtil.setProgressMax(0);
    }

    /**
     * 根据当前页查询出该页面的点，并保存到集合中 同时得到progressMax
     * @param drawingBoardView
     * @param pageIndex
     */
    @Override
    public void readData(final DrawingBoardView drawingBoardView, final int pageIndex) {
        progressMax = 0;
        playBackList = new ArrayList<>();
        Runnable playBackRunnable = new Runnable() {
            @Override
            public void run() {
                currentSelectPage = notePageDao.queryBuilder()
                        .where(NotePageDao.Properties.BookId.eq(activeNoteRecord.getId()),
                                NotePageDao.Properties.PageIndex.eq(pageIndex)).unique();
                if (currentSelectPage != null) { //防止当前页为空
                    List<NoteStroke> noteStrokeListData = noteStrokeDao.queryBuilder()
                            .where(NoteStrokeDao.Properties.PageId.eq(currentSelectPage.getId())).list();
                    if (noteStrokeListData != null && noteStrokeListData.size() > 0) { //线集合不为空
                        for (NoteStroke noteStroke : noteStrokeListData) {
                            List<NotePoint> notePointListData = notePointDao.queryBuilder()
                                    .where(NotePointDao.Properties.StrokeId.eq(noteStroke.getId())).list();
                            if (notePointListData != null && notePointListData.size() > 0) { //点集合不为空
                                for (NotePoint notePoint : notePointListData) {
                                    //将所有点存入集合，
                                    com.newchinese.coolpensdk.entity.NotePoint sdkPoint =
                                            new com.newchinese.coolpensdk.entity.NotePoint(notePoint.getPX(),
                                                    notePoint.getPY(), notePoint.getTestTime(), notePoint.getFirstPress(),
                                                    notePoint.getPress(), notePoint.getPageIndex(), notePoint.getPointType(), notePoint.getStrokeId());
                                    progressMax++;
                                    playBackList.add(sdkPoint);
                                }
                            } else Log.e("test_greendao", "currentNotePointListData当前点集合为空");
                        }
                        dataCacheUtil.setProgressMax(progressMax);
                    } else Log.e("test_greendao", "currentNoteStrokeListData当前线集合为空");
                } else Log.e("test_greendao", "activeNotePage当前页为空");
                dataCacheUtil.setPlayBackList(playBackList);
            }
        };
        singleThreadExecutor.execute(playBackRunnable);
    }
}
