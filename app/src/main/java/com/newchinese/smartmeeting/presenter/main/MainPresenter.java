package com.newchinese.smartmeeting.presenter.main;

import android.content.ComponentName;
import android.util.Log;

import com.newchinese.coolpensdk.constants.PointType;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.MainContract;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteRecordDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;
import com.newchinese.smartmeeting.manager.NotePageManager;
import com.newchinese.smartmeeting.manager.NotePointManager;
import com.newchinese.smartmeeting.manager.NoteRecordManager;
import com.newchinese.smartmeeting.manager.NoteStrokeManager;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NoteRecord;
import com.newchinese.smartmeeting.model.bean.NoteStroke;
import com.newchinese.smartmeeting.ui.main.BleListener;
import com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.PointCacheUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * author         xulei
 * Date           2017/8/19
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {
    private NoteRecordDao noteRecordDao;
    private NotePageDao notePageDao;
    private NoteStrokeDao noteStrokeDao;
    private NotePointDao notePointDao;
    private NoteRecordManager noteRecordManager;
    private NotePageManager notePageManager;
    private NoteStrokeManager noteStrokeManager;
    private NotePointManager notePointManager;
    private NoteRecord activeNoteRecord;
    private NotePage activeNotePage;
    private NoteStroke activeNoteStroke;
    private DataCacheUtil dataCacheUtil;
    private ExecutorService singleThreadExecutor;
    private PointCacheUtil pointCacheUtil;

    /**
     * 获取当前线程池对象
     */
    public ExecutorService getSingleThreadExecutor() {
        return singleThreadExecutor;
    }

    @Override
    public void onPresenterCreated() {
        noteRecordDao = GreenDaoUtil.getInstance().getNoteRecordDao();
        notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
        noteStrokeDao = GreenDaoUtil.getInstance().getNoteStrokeDao();
        notePointDao = GreenDaoUtil.getInstance().getNotePointDao();
        noteRecordManager = NoteRecordManager.getInstance();
        notePageManager = NotePageManager.getInstance();
        noteStrokeManager = NoteStrokeManager.getInstance();
        notePointManager = NotePointManager.getInstance();
        dataCacheUtil = DataCacheUtil.getInstance();
        //获取缓存的当前活动页与本
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
        activeNotePage = dataCacheUtil.getActiveNotePage();
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        //初始化第一笔缓存工具类
        pointCacheUtil = PointCacheUtil.getInstance();
        pointCacheUtil.setCanAddFlag(true);
    }

    @Override
    public void onPresenterDestroy() {
        singleThreadExecutor.shutdown();
    }

    @Override
    public void initListener() {
//        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView));
    }

    @Override
    public boolean isBluetoothOpen() {
        return BluetoothLe.getDefault().isBluetoothOpen();
    }

    @Override
    public void openBle() {
//        BluetoothLe.getDefault().enableBluetooth();
    }

    @Override
    public void scanBlueDevice() {
//        BluetoothLe.getDefault().setScanPeriod(5000).startScan();
    }

    @Override
    public void checkjumpDrawingBoard(com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        if (pointCacheUtil.isCanAddFlag()) { //DrawingBoardActivity未初始化完之前的点都缓存起来
            pointCacheUtil.putInQueue(notePoint);
        }
        ComponentName topActivity = App.getAppliction().getTopActivity();
        if (!DrawingBoardActivity.class.getName().equals(topActivity.getClassName())) {
            mView.jumpDrawingBoard();
        }
    }

    /**
     * 初始化7张分类记录表
     * 默认设置其他分类为活动记录表
     */
    @Override
    public void initNoteRecord() {
        Runnable insertRecordRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO: 2017/8/21  需从服务器获取是否过12点标记, 过12点则收藏并清空之前所有记录表内数据
                if (noteRecordManager.getNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_WORK) == null) {
                    noteRecordManager.insertNoteRecord(noteRecordDao, "", "", "", "", "", Constant.CLASSIFY_NAME_WORK);
                    noteRecordManager.insertNoteRecord(noteRecordDao, "", "", "", "", "", Constant.CLASSIFY_NAME_PROJECT);
                    noteRecordManager.insertNoteRecord(noteRecordDao, "", "", "", "", "", Constant.CLASSIFY_NAME_STUDY);
                    noteRecordManager.insertNoteRecord(noteRecordDao, "", "", "", "", "", Constant.CLASSIFY_NAME_EXPLORE);
                    noteRecordManager.insertNoteRecord(noteRecordDao, "", "", "", "", "", Constant.CLASSIFY_NAME_REPORT);
                    noteRecordManager.insertNoteRecord(noteRecordDao, "", "", "", "", "", Constant.CLASSIFY_NAME_REVIEW);
                    noteRecordManager.insertNoteRecord(noteRecordDao, "", "", "", "", "", Constant.CLASSIFY_NAME_OTHER);
                }
                NoteRecord otherRecord = noteRecordManager.getNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_OTHER);
                dataCacheUtil.setActiveNoteRecord(otherRecord);
            }
        };
        singleThreadExecutor.execute(insertRecordRunnable);
    }

    /**
     * 从服务器获取是否过零点的状态
     */
    private void requestZeroStatus() {
        //RxJava2+Retrofit2请求
        if (true) {
            collectAndClearAllRecord();
        }
    }

    /**
     * 过了则收藏所有记录表内缩略图，清空7张记录表内数据。
     */
    private void collectAndClearAllRecord() {

    }

    /**
     * 存页
     */
    @Override
    public void savePage(final com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
        Runnable savePageRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("test_point", "savePage");
                activeNotePage = notePageManager.getPageByIndex(notePageDao, activeNoteRecord.getId(), notePoint.getPageIndex());
                if (activeNotePage == null) {
                    activeNotePage = notePageManager.insertNotePage(notePageDao, activeNoteRecord.getId(), notePoint.getPageIndex(),
                            System.currentTimeMillis(), "", "", new ArrayList<String>()); //截图与录屏文件path都置空待手动设置更新
                }
                dataCacheUtil.setActiveNotePage(activeNotePage); //缓存当前活动页
            }
        };
        singleThreadExecutor.execute(savePageRunnable);
    }

    /**
     * 存线点
     */
    @Override
    public void saveStrokeAndPoint(final com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        Runnable saveStrokePointRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("test_point", "saveStrokeAndPoint");
                //down点时往数据库存线
                if (notePoint.getPointType() == PointType.TYPE_DOWN) {
                    activeNoteStroke = noteStrokeManager.insertNoteStroke(noteStrokeDao,
                            dataCacheUtil.getActiveNotePage().getId(), dataCacheUtil.getCurrentColor(),
                            dataCacheUtil.getStrokeWidth());
                }
                //防止currentNoteStroke为空
                if (activeNoteStroke == null) {
                    Log.e("greendao_test", "saveStrokeAndPoint：noteStrokeData为空，向数据库添加");
                    activeNoteStroke = noteStrokeManager.insertNoteStroke(noteStrokeDao,
                            dataCacheUtil.getActiveNotePage().getId(), dataCacheUtil.getCurrentColor(),
                            dataCacheUtil.getStrokeWidth());
                }
                //往数据库存点
                notePointManager.insertPoint(notePointDao, activeNoteStroke.getId(),
                        notePoint.getPX(), notePoint.getPY(), notePoint.getTestTime(), notePoint.getFirstPress(),
                        notePoint.getPress(), notePoint.getPageIndex(), notePoint.getPointType());
            }
        };
        singleThreadExecutor.execute(saveStrokePointRunnable);
    }
}
