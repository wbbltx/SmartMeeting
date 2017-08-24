package com.newchinese.smartmeeting.presenter.main;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private PointCacheUtil pointCacheUtil;
    private ExecutorService singleThreadExecutor;
    private List<NotePage> activeNotePageList;
    // 要申请的权限
    private String[] permissions = {Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

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
        //获取缓存的当前活动记录与页,活动记录下所有页
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
        activeNotePage = dataCacheUtil.getActiveNotePage();
        //初始化第一笔缓存工具类
        pointCacheUtil = PointCacheUtil.getInstance();
        pointCacheUtil.setCanAddFlag(true);
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPresenterDestroy() {
        singleThreadExecutor.shutdown();
    }

    /**
     * 6.0及以上主动请求权限
     */
    @Override
    public void requestPermissing(Activity activity) {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int permissionStatus = ContextCompat.checkSelfPermission(activity, permission);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    ActivityCompat.requestPermissions(activity, permissions, 321);
                    break;
                }
            }
        }
    }

    @Override
    public void initListener() {
//        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView));
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
                if (noteRecordManager.getNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_WORK) == null) {
                    noteRecordManager.insertNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_WORK,
                            Constant.SD_DIRECTORY_WORK);
                    noteRecordManager.insertNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_PROJECT,
                            Constant.SD_DIRECTORY_PROJECT);
                    noteRecordManager.insertNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_STUDY,
                            Constant.SD_DIRECTORY_STUDY);
                    noteRecordManager.insertNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_EXPLORE,
                            Constant.SD_DIRECTORY_EXPLORE);
                    noteRecordManager.insertNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_REPORT,
                            Constant.SD_DIRECTORY_REPORT);
                    noteRecordManager.insertNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_REVIEW,
                            Constant.SD_DIRECTORY_REVIEW);
                    noteRecordManager.insertNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_OTHER,
                            Constant.SD_DIRECTORY_OTHER);
                }
                NoteRecord otherRecord = noteRecordManager.getNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_OTHER);
                dataCacheUtil.setActiveNoteRecord(otherRecord);
            }
        };
        singleThreadExecutor.execute(insertRecordRunnable);
    }

    /**
     * 获取当前时间与缓存时间对比不是一天
     */
    private void requestZeroStatus() {
        if (true) {
            collectAndClearAllRecord();
        }
    }

    /**
     * 过了则收藏所有记录表内缩略图，清空7张记录表内数据。
     */
    private void collectAndClearAllRecord() {
        // TODO: 2017/8/21 收藏
        //清空页线点
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                notePageDao.deleteAll();
                noteStrokeDao.deleteAll();
                notePointDao.deleteAll();
            }
        };
        singleThreadExecutor.execute(deleteRunnable);
    }

    /**
     * 存页
     * 在此将新添加的页插入到缓存的活动记录页集合中
     */
    @Override
    public void savePage(final com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        activeNotePageList = dataCacheUtil.getActiveNotePageList();
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
        Log.i("test_active", "savePage：activeNoteRecord：" + activeNoteRecord.toString());
        Runnable savePageRunnable = new Runnable() {
            @Override
            public void run() {
                activeNotePage = notePageManager.getPageByIndex(notePageDao, activeNoteRecord.getId(), notePoint.getPageIndex());
                if (activeNotePage == null) {
                    activeNotePage = notePageManager.insertNotePage(notePageDao, activeNoteRecord.getId(), notePoint.getPageIndex(),
                            System.currentTimeMillis(), "", "", null); //截图与录屏文件path都置空待手动设置更新
                    activeNotePageList.add(0, activeNotePage);
                    dataCacheUtil.setActiveNotePageList(activeNotePageList);
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
                //down点时往数据库存线
                if (notePoint.getPointType() == PointType.TYPE_DOWN) {
                    activeNoteStroke = noteStrokeManager.insertNoteStroke(noteStrokeDao,
                            dataCacheUtil.getActiveNotePage().getId(), dataCacheUtil.getCurrentColor(),
                            dataCacheUtil.getStrokeWidth());
                }
                //防止currentNoteStroke为空
                if (activeNoteStroke == null) {
                    Log.i("test_greendao", "saveStrokeAndPoint：noteStrokeData为空，向数据库添加");
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

    /**
     * SD卡创建应用所需所有目录
     */
    @Override
    public void createSDCardDirectory() {
        //判断SD卡状态
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            String basePath = Environment.getExternalStorageDirectory() + "/" + Constant.SD_DIRECTORY_BASE;
            //创建一级根目录
            File baseFile = new File(basePath);
            if (!baseFile.exists()) {
                baseFile.mkdir();
            }
            //创建二级图片目录
            String picPath = basePath + "/" + Constant.SD_DIRECTORY_IMAGE;
            File picFile = new File(picPath);
            if (!picFile.exists()) {
                picFile.mkdir();
            }
            //创建三级记录目录
            String[] recordDirectoryList = Constant.SD_DIRECTORY_RECORD_LIST;
            for (String recordDirectory : recordDirectoryList) {
                String recordPath = picPath + "/" + recordDirectory;
                File recordFile = new File(recordPath);
                if (!recordFile.exists()) {
                    recordFile.mkdir();
                }
            }
            dataCacheUtil.setPicSDCardDirectory(picPath);
        } else {
            mView.showToast("请安装SD卡");
        }
    }

    @Override
    public void saveRecordPage(int i) {
        if (DataCacheUtil.getInstance().isRecording()) {
            DataCacheUtil.getInstance().addPages(i);
        }
    }
}
