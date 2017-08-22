package com.newchinese.smartmeeting.presenter.meeting;

import android.util.Log;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxContract;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NoteRecord;
import com.newchinese.smartmeeting.ui.main.BleListener;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:   草稿箱Presenter
 * author         xulei
 * Date           2017/8/18
 */

public class DraftBoxPresenter extends BasePresenter<DraftBoxContract.View> implements DraftBoxContract.Presenter {
    private ExecutorService singleThreadExecutor; //单核心线程线程池

    @Override
    public void onPresenterCreated() {
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPresenterDestroy() {

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
        BluetoothLe.getDefault().connectBleDevice(add);
    }

    @Override
    public void initListener() {
        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnConnectListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnKeyListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnElectricityRequestListener(BleListener.getDefault().init(mView));
    }


}
