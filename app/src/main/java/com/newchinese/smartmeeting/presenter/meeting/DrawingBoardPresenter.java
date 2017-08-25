package com.newchinese.smartmeeting.presenter.meeting;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.newchinese.coolpensdk.constants.PointType;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DrawingBoardActContract;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NotePoint;
import com.newchinese.smartmeeting.model.bean.NoteRecord;
import com.newchinese.smartmeeting.model.bean.NoteStroke;
import com.newchinese.smartmeeting.ui.meeting.service.RecordService;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.PlayBackUtil;
import com.newchinese.smartmeeting.util.PointCacheUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Description:   画板Presenter
 * author         xulei
 * Date           2017/8/18
 */

public class DrawingBoardPresenter extends BasePresenter<DrawingBoardActContract.View> implements DrawingBoardActContract.Presenter {
    private static final java.lang.String TAG = "DrawingBoardPresenter";
    private DataCacheUtil dataCacheUtil;
    private NoteRecord activeNoteRecord;
    private NotePage currentSelectPage;
    private NotePageDao notePageDao;
    private NoteStrokeDao noteStrokeDao;
    private NotePointDao notePointDao;
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private boolean isFirstLoad = true; //初次加载第一笔缓存标记
    private int pageIndex;
    private RecordService recordService;
    private List<String> strings = new ArrayList<>();
    private long duration;
    private Disposable subscribe;
    private ArrayList<com.newchinese.coolpensdk.entity.NotePoint> playBackList;
    private int progressMax;

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
        return BluetoothLe.getDefault().isBluetoothOpen();
    }

    //将录屏路径存入数据库
    @Override
    public void saveRecord(final String path) {
        Runnable saveRecordRunnable = new Runnable() {
            @Override
            public void run() {
                Set<Integer> pages = dataCacheUtil.getPages();
                for (Integer page : pages) {
                    NotePage unique = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(activeNoteRecord.getId()), NotePageDao.Properties.PageIndex.eq(page)).unique();
                    List<String> screenPathList = unique.getScreenPathList();
                    if (screenPathList == null) {
                        screenPathList = new ArrayList<>();
                    }
                    screenPathList.add(path);
                    XLog.d(TAG, "页数" + screenPathList.size());
                    unique.setScreenPathList(screenPathList);
                    notePageDao.update(unique);
                }
            }
        };
        singleThreadExecutor.execute(saveRecordRunnable);
    }

    @Override
    public boolean isConnected() {
        return BluetoothLe.getDefault().getConnected();
    }

    @Override
    public void initRecord(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        context.bindService(intent, connection, context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean isRecording() {
        return recordService.isRunning();
    }

    /**
     * 停止录屏计时，并将时间初始化
     */
    @Override
    public void stopRecordTimer() {
        duration = 0;
        subscribe.dispose();
    }

    /**
     * 开始录屏计时
     */
    @Override
    public void startRecordTimer() {
        subscribe = Flowable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mView.setRecordTime(timeParse(++duration) + "");
//
                    }
                })
                .subscribe();
        addSubscribe(subscribe);

    }

    @Override
    public void extra(MediaProjection mediaProjection) {
//        recordService.setMediaProject(mediaProjection);
//        recordService.startRecord();
    }

    /**
     * 查询当前页的录屏个数
     *
     * @param pageIndex
     */
    @Override
    public void queryRecordCount(final int pageIndex) {
        Runnable queryRecordCountRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentSelectPage != null) {
                    NotePage notePage = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(activeNoteRecord.getId()), NotePageDao.Properties.PageIndex.eq(pageIndex)).unique();
                    List<String> screenPathList = notePage.getScreenPathList();
                    if (screenPathList == null) {
                        mView.setRecordCount(0);
                    } else {
                        mView.setRecordCount(screenPathList.size());
                    }
                }
            }
        };
        singleThreadExecutor.execute(queryRecordCountRunnable);
    }

    /**
     * 笔记回放
     */
    @Override
    public void playBack(final DrawingBoardView drawingBoardView) {
//        mView.clearCanvars(); //清屏防止有上一页缓存
        progressMax = 0;
        playBackList = new ArrayList<>();
        Runnable playBackRunnable = new Runnable() {
            @Override
            public void run() {
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
                                                    notePoint.getPress(), notePoint.getPageIndex(), notePoint.getPointType());
                                    progressMax++;
                                    playBackList.add(sdkPoint);
                                }
                            } else Log.e("test_greendao", "currentNotePointListData当前点集合为空");
                        }
                        dataCacheUtil.setProgressMax(progressMax);
                    } else Log.e("test_greendao", "currentNoteStrokeListData当前线集合为空");
                } else Log.e("test_greendao", "activeNotePage当前页为空");
                PlayBackUtil.getInstance().addAllNewsBrief(null, playBackList, drawingBoardView);
            }
        };
        singleThreadExecutor.execute(playBackRunnable);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

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

    public String timeParse(long duration) {
        String time = "";
        long minute = duration / 60;
        long seconds = duration % 60;
        long second = Math.round((float) seconds);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }
}
