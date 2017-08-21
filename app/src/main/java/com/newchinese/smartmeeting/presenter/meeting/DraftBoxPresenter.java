package com.newchinese.smartmeeting.presenter.meeting;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxContract;
import com.newchinese.smartmeeting.ui.main.BleListener;

/**
 * Description:   草稿箱Presenter
 * author         xulei
 * Date           2017/8/18
 */

public class DraftBoxPresenter extends BasePresenter<DraftBoxContract.View> implements DraftBoxContract.Presenter {
    @Override
    public void onPresenterCreated() {
        
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

    @Override
    public void initListener() {
        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnConnectListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnKeyListener(BleListener.getDefault().init(mView));
        BluetoothLe.getDefault().setOnElectricityRequestListener(BleListener.getDefault().init(mView));
    }
}
