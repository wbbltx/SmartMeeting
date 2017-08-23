package com.newchinese.smartmeeting.presenter.meeting;

import android.view.View;
import android.widget.TextView;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxContract;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NoteRecord;
import com.newchinese.smartmeeting.ui.main.BleListener;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;

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

public class DraftBoxPresenter extends BasePresenter<DraftBoxContract.View> implements DraftBoxContract.Presenter {
    private static final String TAG = "DraftBoxPresenter";
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void onPresenterCreated() {
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
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
        timerTask= new TimerTask() {
            @Override
            public void run() {
                requestElectricity();
            }
        };
        timer.schedule(timerTask,500,20000);
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
                NoteRecord activeRecord = DataCacheUtil.getInstance().getActiveNoteRecord();
                NotePageDao notePageDao = GreenDaoUtil.getInstance().getNotePageDao();
                List<NotePage> notePageList = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(activeRecord.getId()))
                        .orderDesc(NotePageDao.Properties.Date).list();
                mView.getActivePageList(notePageList);
            }
        };
        singleThreadExecutor.execute(loadPageRunnable);
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
        XLog.d(TAG,"设置图标状态 "+state);
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
