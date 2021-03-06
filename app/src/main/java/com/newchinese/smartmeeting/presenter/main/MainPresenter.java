package com.newchinese.smartmeeting.presenter.main;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.newchinese.coolpensdk.constants.PointType;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.MainActContract;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteRecordDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.entity.bean.NoteStroke;
import com.newchinese.smartmeeting.entity.bean.RequestVersion;
import com.newchinese.smartmeeting.entity.bean.VersionInfo;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.ApiSubscriber;
import com.newchinese.smartmeeting.entity.http.Kits;
import com.newchinese.smartmeeting.entity.http.NetError;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;
import com.newchinese.smartmeeting.manager.CollectPageManager;
import com.newchinese.smartmeeting.manager.CollectRecordManager;
import com.newchinese.smartmeeting.manager.NotePageManager;
import com.newchinese.smartmeeting.manager.NotePointManager;
import com.newchinese.smartmeeting.manager.NoteRecordManager;
import com.newchinese.smartmeeting.manager.NoteStrokeManager;
import com.newchinese.smartmeeting.model.AboutModelImp;
import com.newchinese.smartmeeting.ui.mine.service.UpdateService;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.PointCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.util.log.XLog;

import org.reactivestreams.Subscription;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:
 * author         xulei
 * Date           2017/8/19
 */

public class MainPresenter extends BasePresenter<MainActContract.View> implements MainActContract.Presenter {
    private static final java.lang.String TAG = "MainPresenter";
    private NoteRecordDao noteRecordDao;
    private NotePageDao notePageDao;
    private NoteStrokeDao noteStrokeDao;
    private NotePointDao notePointDao;
    private CollectRecordDao collectRecordDao;
    private CollectPageDao collectPageDao;
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
    private ApiService mServices;
    private String interiorAppUrl;

    /**
     * 获取当前线程池对象
     */
    public ExecutorService getSingleThreadExecutor() {
        return singleThreadExecutor;
    }

    @Override
    public void onPresenterCreated() {
        GreenDaoUtil greenDaoUtil = GreenDaoUtil.getInstance();
        noteRecordDao = greenDaoUtil.getNoteRecordDao();
        notePageDao = greenDaoUtil.getNotePageDao();
        noteStrokeDao = greenDaoUtil.getNoteStrokeDao();
        notePointDao = greenDaoUtil.getNotePointDao();
        collectRecordDao = greenDaoUtil.getCollectRecordDao();
        collectPageDao = greenDaoUtil.getCollectPageDao();
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

        XApi.registerProvider(new NetProviderImpl());
        mServices = XApi.get(NetUrl.THOST, ApiService.class);
    }

    @Override
    public void onPresenterDestroy() {
        singleThreadExecutor.shutdown();
        DataCacheUtil.getInstance().setFirstTime(true);
        disconnect();
    }

    @Override
    public void initListener() {
//        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView));
    }

    @Override
    public void saveCache(com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        if (pointCacheUtil.isCanAddFlag()) { //DrawingBoardActivity未初始化完之前的点都缓存起来
            pointCacheUtil.putInQueue(notePoint);
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
                //默认放其他分类
                NoteRecord otherRecord = noteRecordManager.getNoteRecord(noteRecordDao, Constant.CLASSIFY_NAME_OTHER);
                dataCacheUtil.setActiveNoteRecord(otherRecord);
                //判断是否过0点了，收藏与删除，去除此功能
//                collectAndClearAllRecord();
            }
        };
        singleThreadExecutor.execute(insertRecordRunnable);

    }

    /**
     * 获取当前时间与缓存时间对比不是一天
     * 过了则收藏所有记录表内缩略图，清空7张记录表内数据。
     */
    private void collectAndClearAllRecord() {
        String cacheDayNum = SharedPreUtils.getString(Constant.DAY_NUM);
        String currentDayNum = DateUtils.formatLongDate4(System.currentTimeMillis());
        if (!"".equals(cacheDayNum) && !cacheDayNum.equals(currentDayNum)) {
            //收藏现有的并清空页线点
            Runnable deleteRunnable = new Runnable() {
                @Override
                public void run() {
                    //收藏现有的所有记录页
                    List<NoteRecord> noteRecords = noteRecordDao.queryBuilder().list();
                    //遍历所有记录表
                    for (NoteRecord noteRecord : noteRecords) {
                        List<NotePage> notePages = notePageDao.queryBuilder()
                                .where(NotePageDao.Properties.BookId.eq(noteRecord.getId())).list();
                        if (notePages.size() > 0) {
                            CollectRecord collectRecord = CollectRecordManager.getInstance()
                                    .insertCollectRecord(collectRecordDao, noteRecord.getClassifyName(),
                                            noteRecord.getClassifyName() + DateUtils.formatLongDate3(System.currentTimeMillis()));
                            //遍历所有页
                            for (NotePage notePage : notePages) {
                                //存收藏页
                                CollectPageManager.getInstance().insertCollectPage(collectPageDao,
                                        collectRecord.getId(), notePage.getPageIndex(), notePage.getDate(),
                                        notePage.getThumbnailPath(), notePage.getScreenPathList());
                            }
                        }
                    }
                    //删除现有的所有点线页
                    notePageDao.deleteAll();
                    noteStrokeDao.deleteAll();
                    notePointDao.deleteAll();
                }
            };
            singleThreadExecutor.execute(deleteRunnable);
        }
        //存当前日期
        SharedPreUtils.setString(Constant.DAY_NUM, currentDayNum);
    }

    /**
     * 判断选择的会议类型与上次书写的是不是同一个
     */
    public void checkRecord() {
        if (dataCacheUtil.getActiveWriteRecord() != null && dataCacheUtil.getActiveNoteRecord() != null){
            if (!dataCacheUtil.getActiveWriteRecord().equals(dataCacheUtil.getActiveNoteRecord())){
                dataCacheUtil.setActiveNoteRecord(dataCacheUtil.getActiveWriteRecord());
            }
        }
    }

    public void setWriteRecord(){
        dataCacheUtil.setActiveWriteRecord(dataCacheUtil.getActiveNoteRecord());
    }

    /**
     * 存页
     * 在此将新添加的页插入到缓存的活动记录页集合中
     */
    @Override
    public void savePage(final com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        activeNotePageList = dataCacheUtil.getActiveNotePageList();
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
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
                    Log.e("test_greendao", "saveStrokeAndPoint：noteStrokeData为空，向数据库添加");
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
            dataCacheUtil.setPicSDCardDirectory(picPath);
            //创建三级记录目录
            String[] recordDirectoryList = Constant.SD_DIRECTORY_RECORD_LIST;
            for (String recordDirectory : recordDirectoryList) {
                String recordPath = picPath + "/" + recordDirectory;
                File recordFile = new File(recordPath);
                if (!recordFile.exists()) {
                    recordFile.mkdir();
                }
            }
            //创建插入图片目录
            String insertPath = picPath + "/" + Constant.SD_DIRECTORY_INSERT;
            File insertFile = new File(insertPath);
            if (!insertFile.exists()) {
                insertFile.mkdir();
            }
        } else {
            mView.showToast(App.getContext().getString(R.string.please_install_sd));
        }
    }

    @Override
    public void saveRecordPage(int i) {
        if (DataCacheUtil.getInstance().isRecording()) {
            DataCacheUtil.getInstance().addPages(i);
        }
    }

    @Override
    public void disconnect() {
//        退出应用 蓝牙断开 状态初始化为断开
        if (dataCacheUtil.getPenState() == BluCommonUtils.PEN_CONNECTED)
            dataCacheUtil.setPenState(BluCommonUtils.PEN_DISCONNECTED);
            BluetoothLe.getDefault().disconnectBleDevice();
    }

    @Override
    public void checkVersion() {
        RequestVersion requestVersion = new RequestVersion().setPlatform("1").setVersion("1.0");
        mServices.checkVersion(requestVersion).delay(500,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {

                    }
                })
                .compose(XApi.<BaseResult<VersionInfo>>getApiTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber<BaseResult<VersionInfo>>() {
                    @Override
                    protected void onFail(NetError error) {
                        XLog.d(TAG, " onFail " + error.getMessage());
                    }

                    @Override
                    public void onNext(BaseResult<VersionInfo> versionInfoBaseResult) {
                        VersionInfo data = versionInfoBaseResult.data;
                        XLog.d(TAG, versionInfoBaseResult.msg + " ++ " + data);
                        int versionCode = Kits.Package.getVersionCode(App.getAppliction());
                        if ((versionCode) < data.getVersion()) {
                            interiorAppUrl = data.getInteriorAppUrl();
                            mView.showDialog();
                        }else {
                            mView.initMaskView();
                        }
                    }
                });
    }

    @Override
    public void downLoadApk(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(BluCommonUtils.VERSION_PATH,interiorAppUrl);
        context.startService(intent);
    }
}
