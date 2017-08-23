package com.newchinese.smartmeeting.presenter.meeting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.newchinese.coolpensdk.constants.PointType;
import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DrawingBoardContract;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NotePoint;
import com.newchinese.smartmeeting.model.bean.NoteRecord;
import com.newchinese.smartmeeting.model.bean.NoteStroke;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.PointCacheUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Description:   画板Presenter
 * author         xulei
 * Date           2017/8/18
 */

public class DrawingBoardPresenter extends BasePresenter<DrawingBoardContract.View> implements DrawingBoardContract.Presenter {
    private DataCacheUtil dataCacheUtil;
    private NoteRecord activeNoteRecord;
    private NotePage currentSelectPage;
    private NotePageDao notePageDao;
    private NoteStrokeDao noteStrokeDao;
    private NotePointDao notePointDao;
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private boolean isFirstLoad = true; //初次加载第一笔缓存标记
    private int pageIndex;

    @Override
    public void onPresenterCreated() {
        dataCacheUtil = DataCacheUtil.getInstance();
        notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
        noteStrokeDao = GreenDaoUtil.getInstance().getNoteStrokeDao();
        notePointDao = GreenDaoUtil.getInstance().getNotePointDao();
        //获取缓存的当前活动页与本
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPresenterDestroy() {
        //重置第一笔缓存标志，初始笔色，初始线宽
        PointCacheUtil.getInstance().setCanAddFlag(true);
        DataCacheUtil.getInstance().setCurrentColor(Constant.colors[0]);
        DataCacheUtil.getInstance().setStrokeWidth(0);
        //关闭线程池
        shutDownExecutor();
        //清除SDK数据缓存
        DrawingboardAPI.getInstance().clearCache();
    }

    @Override
    public boolean isBluetoothOpen() {
        return false;
    }

    @Override
    public void openBle() {

    }

    @Override
    public void scanBlueDevice() {

    }

    /**
     * 加载第一笔缓存
     */
    @Override
    public void loadFirstStokeCache() {
        PointCacheUtil pointCacheUtil = PointCacheUtil.getInstance();
        com.newchinese.coolpensdk.entity.NotePoint[] all = pointCacheUtil.getArrayAll();
        if (all != null && all.length > 0) {
            pageIndex = all[0].getPageIndex();
            for (com.newchinese.coolpensdk.entity.NotePoint notePoint : all) {
                if (notePoint.getPointType() == PointType.TYPE_DOWN && isFirstLoad) {
                    //延时加载数据库，否则view未初始化完毕会丢点
                    Flowable.timer(500, TimeUnit.MILLISECONDS)
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    readDataBasePoint(pageIndex);
                                }
                            });
                    isFirstLoad = false;
                    mView.setTitleText(pageIndex);
                }
                mView.getFirstStrokeCachePoint(notePoint);
            }
            pointCacheUtil.clearQueue();
        }
        pointCacheUtil.setCanAddFlag(false);
    }

    /**
     * 根据当前活动页读数据库的点
     */
    @Override
    public void readDataBasePoint(final int pageIndex) {
        mView.clearCanvars(); //清屏防止有上一页缓存
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord(); //当前活动的分类记录表
        Log.i("test_active", "readDataBasePoint：activeNoteRecord：" + activeNoteRecord.toString());
        Runnable readDataRunnable = new Runnable() {
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
                                    //转换点对象为SDK所需格式并绘制
                                    com.newchinese.coolpensdk.entity.NotePoint sdkPoint =
                                            new com.newchinese.coolpensdk.entity.NotePoint(notePoint.getPX(),
                                                    notePoint.getPY(), notePoint.getTestTime(), notePoint.getFirstPress(),
                                                    notePoint.getPress(), notePoint.getPageIndex(), notePoint.getPointType());
                                    mView.getDataBasePoint(sdkPoint, noteStroke.getStrokeColor(), noteStroke.getStrokeWidth());
                                }
                            } else Log.e("test_greendao", "currentNotePointListData当前点集合为空");
                        }
                    } else Log.e("test_greendao", "currentNoteStrokeListData当前线集合为空");
                } else Log.e("test_greendao", "activeNotePage当前页为空");
            }
        };
        singleThreadExecutor.execute(readDataRunnable);
    }

    /**
     * 获取当前NotePage在集合中的Pointion
     */
    public int getCurrentPosition(List<NotePage> activeNotePageList, int pageIndex) {
        Log.i("test_active", "size：" + activeNotePageList.size() + "," + activeNotePageList.toString());
        int position = 0;
        for (int i = 0; i < activeNotePageList.size(); i++) {
            if (activeNotePageList.get(i).getPageIndex() == pageIndex) {
                position = i;
            }
        }
        return position;
    }

    /**
     * 保存缩略图到SD卡，并更新数据库页的缩略图路径
     */
    @Override
    public void savePageThumbnail(Bitmap bitmap, final int pageIndex) {
        //判断SD卡状态
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            String classifyName = activeNoteRecord.getClassifyName(); //分类名称
            String picSDCardDirectory = dataCacheUtil.getPicSDCardDirectory(); //图片存储路径
            String recordDirectory = picSDCardDirectory + "/" + activeNoteRecord.getClassifyCode(); //记录缩略图存储路径
            //缩略图文件名称（分类记录名称+页码+时间戳），加时间戳防止重复
            final String thumbnailFilePath = recordDirectory + "/" + classifyName + "-" + pageIndex + "-" + System.currentTimeMillis() + ".jpg";
            Log.e("test_pic", "" + thumbnailFilePath);
            File thumbnailFile = new File(thumbnailFilePath);
            if (bitmap != null) {
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(thumbnailFile));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bitmap.recycle();
                    bitmap = null;
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //数据库存储缩略图路径，根据当前分类与页码查出当前NotePage，再更新缩略图路径
            Runnable saveRunnable = new Runnable() {
                @Override
                public void run() {
                    NotePage currentNotePage = notePageDao.queryBuilder()
                            .where(NotePageDao.Properties.BookId.eq(activeNoteRecord.getId()),
                                    NotePageDao.Properties.PageIndex.eq(pageIndex)).unique();
                    currentNotePage.setThumbnailPath(thumbnailFilePath);
                    notePageDao.update(currentNotePage);
                }
            };
            singleThreadExecutor.execute(saveRunnable);
        }
    }

    /**
     * 截取view的视图返回Bitmap
     */
    @Override
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = null;
        view.clearFocus();
        view.setPressed(false);
        boolean willNotCache = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);
        int color = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            view.destroyDrawingCache();
        }
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        //可能OOM位置1
        bitmap = Bitmap.createBitmap(cacheBitmap);
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCache);
        view.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    /**
     * 中断线程池
     */
    public void shutDownExecutor() {
        singleThreadExecutor.shutdownNow();
    }


}
