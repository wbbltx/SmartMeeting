package com.newchinese.smartmeeting.presenter.meeting;

import android.util.Log;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxActContract;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.manager.CollectPageManager;
import com.newchinese.smartmeeting.manager.CollectRecordManager;
import com.newchinese.smartmeeting.model.bean.CollectRecord;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NoteRecord;
import com.newchinese.smartmeeting.ui.main.BleListener;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:   草稿箱Presenter
 * author         xulei
 * Date           2017/8/18
 */

public class DraftBoxPresenter extends BasePresenter<DraftBoxActContract.View> implements DraftBoxActContract.Presenter {
    private static final String TAG = "DraftBoxPresenter";
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private Timer timer;
    private TimerTask timerTask;
    private NotePageDao notePageDao;
    private CollectRecordDao collectRecordDao;
    private CollectPageDao collectPageDao;
    private NoteRecord activeNoteRecord;
    private String classifyName;
    private CollectRecordManager collectRecordManager;
    private CollectPageManager collectPageManager;
    private List<Boolean> isSelectedList;
    private List<NotePage> notePageList;

    @Override
    public void onPresenterCreated() {
        notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
        collectPageDao = GreenDaoUtil.getInstance().getCollectPageDao();
        collectRecordManager = CollectRecordManager.getInstance();
        collectPageManager = CollectPageManager.getInstance();
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        //活动记录
        activeNoteRecord = DataCacheUtil.getInstance().getActiveNoteRecord();
        //当前分类
        classifyName = activeNoteRecord.getClassifyName();
        isSelectedList = new ArrayList<>();
        notePageList = new ArrayList<>();
    }

    @Override
    public void onPresenterDestroy() {
        //清空活动记录所有页集合
        DataCacheUtil.getInstance().clearActiveNotePageList();
    }

    @Override
    public boolean isBluetoothOpen() {
        return BluetoothLe.getDefault().isBluetoothOpen();
    }


    @Override
    public void openBle() {
        BluetoothLe.getDefault().enableBluetooth();
    }

    @Override
    public void scanBlueDevice() {
        BluetoothLe.getDefault().setScanPeriod(5000).startScan();
    }

    @Override
    public void requestElectricity() {
        XLog.d(TAG, "发送请求电量命令");
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OBTAIN_ELECTRICITY);
    }

    @Override
    public void startTimer() {
        timer = null;
        timerTask = null;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                requestElectricity();
            }
        };
        timer.schedule(timerTask, 500, 20000);
    }

    @Override
    public void stopTimer() {
//        timerTask.cancel();
//        timer.cancel();
    }

    @Override
    public void stopScan() {
        BluetoothLe.getDefault().stopScan();
    }

    @Override
    public boolean isConnected() {
        return BluetoothLe.getDefault().getConnected();
    }

    /**
     * 加载当前活动记录的所有页
     */
    @Override
    public void loadActivePageList() {
        Runnable loadPageRunnable = new Runnable() {
            @Override
            public void run() {
                NotePageDao notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
                List<NotePage> notePageList = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(activeNoteRecord.getId()))
                        .orderDesc(NotePageDao.Properties.Date).list();
                mView.getActivePageList(notePageList);
            }
        };
        singleThreadExecutor.execute(loadPageRunnable);
    }

    /**
     * 生成记录
     * 根据当前选择boolean列表，筛选出notePageList中被选择的NotePage
     * 存入收藏表，删除选中的页
     */
    @Override
    public void createSelectedRecords(final List<NotePage> notePages, List<Boolean> isSelecteds,
                                      final String recordName) {
        Log.i("test_select", notePages.size() + ",notePages：" + notePages);
        Log.i("test_select", "isSelecteds：" + isSelecteds);
        Log.i("test_select", "recordName：" + recordName);
        isSelectedList.clear();
        isSelectedList.addAll(isSelecteds);
        notePageList.clear();
        notePageList.addAll(notePages);
        Runnable saveRecordsRunnable = new Runnable() {
            @Override
            public void run() {
                CollectRecord collectRecord = collectRecordManager.insertCollectRecord(collectRecordDao, classifyName, recordName);
                for (int i = 0; i < isSelectedList.size(); i++) {
                    if (isSelectedList.get(i)) {
                        Log.i("test_select", "selectedPage：" + notePageList.get(i).getPageIndex());
                        //存收藏页
                        NotePage selectPage = notePageList.get(i);
                        collectPageManager.insertCollectPage(collectPageDao, collectRecord.getId(),
                                selectPage.getPageIndex(), selectPage.getDate(),
                                selectPage.getThumbnailPath(), selectPage.getScreenPathList());
                        //删记录页
                        notePageDao.delete(selectPage);
                    }
                }
                mView.showToast("生成记录成功");
                //存完刷新页面
                loadActivePageList();
            }
        };
        singleThreadExecutor.execute(saveRecordsRunnable);
    }


    @Override
    public void disConnect() {
        BluetoothLe.getDefault().disconnectBleDevice();
    }

    @Override
    public void connectDevice(String add) {
        //去连接设备时 将mac地址放在临时变量中
        BluCommonUtils.setDeviceAddress(add);
        BluetoothLe.getDefault().connectBleDevice(add);
    }

    @Override
    public void initListener() {
        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnConnectListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnKeyListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnElectricityRequestListener(BleListener.getDefault().init(mView));
    }

    @Override
    public void updatePenState(int state) {
        int i = R.mipmap.pen_break;
        XLog.d(TAG, "设置图标状态 " + state);
        switch (state) {
            case BSTATE_CONNECTED:
                i = R.mipmap.pen_succes;
                break;

            case BSTATE_CONNECTING:
            case BSTATE_SCANNING:
                i = R.mipmap.pen_loading;
                break;

            case BSTATE_DISCONNECT:
                i = R.mipmap.pen_break;
                break;
        }
        mView.setState(i);
    }

}
