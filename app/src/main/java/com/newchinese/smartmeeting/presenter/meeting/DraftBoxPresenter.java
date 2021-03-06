package com.newchinese.smartmeeting.presenter.meeting;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxActContract;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.manager.CollectPageManager;
import com.newchinese.smartmeeting.manager.CollectRecordManager;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.entity.listener.BleListener;
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
    public static final int BSTATE_DISCONNECT = 0;
    public static final int BSTATE_CONNECTED_LOW = 4;
    public static final int BSTATE_CONNECTED_NORMAL = 1;
    public static final int BSTATE_CONNECTING = 2;
    public static final int BSTATE_SCANNING = 3;

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
        if (activeNoteRecord != null)
            classifyName = activeNoteRecord.getClassifyName();
        isSelectedList = new ArrayList<>();
        notePageList = new ArrayList<>();
    }

    @Override
    public void onPresenterDestroy() {
        //清空活动记录所有页集合
        DataCacheUtil.getInstance().clearActiveNotePageList();
        BleListener.getDefault().unInit();
        singleThreadExecutor.shutdownNow();
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
        BluetoothLe
                .getDefault()
                .setScanByServiceUUID(null)
                .setScanPeriod(5000)
                .startScan();
    }

    @Override
    public void requestElectricity() {
        XLog.d(TAG, "发送请求电量命令");
        if (isConnected())
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
        timer.schedule(timerTask, 1000, 60000);
    }

    @Override
    public void stopTimer() {
        if (timer != null && timerTask != null) {
            timerTask.cancel();
            timer.cancel();
        }
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
                if (mView != null) {
                    mView.getActivePageList(notePageList);
                }
            }
        };
        if (!singleThreadExecutor.isShutdown()) {
            singleThreadExecutor.execute(loadPageRunnable);
        }
    }

    /**
     * 生成记录
     * 根据当前选择boolean列表，筛选出notePageList中被选择的NotePage
     * 存入收藏表，删除选中的页
     */
    @Override
    public void createSelectedRecords(final List<NotePage> notePages, List<Boolean> isSelecteds,
                                      final String recordName, final String pageMode) {
        isSelectedList.clear();
        isSelectedList.addAll(isSelecteds);
        notePageList.clear();
        notePageList.addAll(notePages);
        Runnable saveRecordsRunnable = new Runnable() {
            @Override
            public void run() {
                CollectRecord collectRecord = pageMode.equals(BluCommonUtils.DELETE_MODE) ? null : collectRecordManager.insertCollectRecord(collectRecordDao, classifyName, recordName);
                for (int i = 0; i < isSelectedList.size(); i++) {
                    if (isSelectedList.get(i)) {
                        //存收藏页
                        NotePage selectPage = notePageList.get(i);
                        if (pageMode.equals(BluCommonUtils.EDIT_MODE)) { //如果是归档模式，创建记录本，删除书写页  如果是删除模式，跳过该步骤，直接删除记录页
                            collectPageManager.insertCollectPage(collectPageDao, collectRecord.getId(),
                                    selectPage.getPageIndex(), selectPage.getDate(),
                                    selectPage.getThumbnailPath(), selectPage.getScreenPathList());
                        }
                        //删记录页
                        notePageDao.delete(selectPage);
                    }
                }
                if (mView != null) {
                    mView.showToast(pageMode.equals(BluCommonUtils.DELETE_MODE) ? "删除成功！" : App.getContext().getString(R.string.create_record_success));
                }
                //存完刷新页面
                loadActivePageList();
            }
        };
        if (!singleThreadExecutor.isShutdown()) {
            singleThreadExecutor.execute(saveRecordsRunnable);
        }
    }


    @Override
    public void disConnect() {
        BluetoothLe.getDefault().disconnectBleDevice();
    }

    @Override
    public void connectDevice(BluetoothDevice device) {
        //去连接设备时 将mac地址放在临时变量中
        DataCacheUtil.getInstance().setDevice(device);
        BluetoothLe.getDefault()
//                .setConnectTimeOut(8000)   //设置连接超时
                .connectBleDevice(device.getAddress());
    }

    @Override
    public void initListener() {
        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView.initBluListener()));
        BluetoothLe.getDefault().setOnConnectListener(BleListener.getDefault().init(mView.initBluListener()));
        BluetoothLe.getDefault().setOnKeyListener(BleListener.getDefault().init(mView.initBluListener()));
        BluetoothLe.getDefault().setOnElectricityRequestListener(BleListener.getDefault().init(mView.initBluListener()));
        BluetoothLe.getDefault().setOnLeNotificationListener(BleListener.getDefault().init(mView.initBluListener()));
    }

    @Override
    public void updatePenState(int state) {
        int i = R.mipmap.pen_disconnect;
        XLog.d(TAG, TAG + " 设置图标状态 " + state);
        switch (state) {
            case BSTATE_CONNECTED_LOW:
                i = R.mipmap.pen_low_power;
                break;
            case BSTATE_CONNECTED_NORMAL:
                i = R.mipmap.pen_normal_power;
                break;
            case BSTATE_CONNECTING:
            case BSTATE_SCANNING:
                i = R.mipmap.weilianjie;
                break;

            case BSTATE_DISCONNECT:
                i = R.mipmap.pen_disconnect;
                break;
        }
        if (mView != null) {
            mView.setState(i);
        }
    }

    @Override
    public boolean isScanning() {
        return BluetoothLe.getDefault().getScanning();
    }

    @Override
    public void openWrite() {
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
    }

}
