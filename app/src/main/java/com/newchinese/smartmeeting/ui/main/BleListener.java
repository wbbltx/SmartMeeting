package com.newchinese.smartmeeting.ui.main;

import android.bluetooth.BluetoothDevice;

import com.newchinese.coolpensdk.listener.OnBleScanListener;
import com.newchinese.coolpensdk.listener.OnConnectListener;
import com.newchinese.coolpensdk.listener.OnElectricityRequestListener;
import com.newchinese.coolpensdk.listener.OnKeyListener;
import com.newchinese.coolpensdk.listener.OnLeNotificationListener;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Created by Administrator on 2017/8/19 0019.
 */

public class BleListener implements OnBleScanListener,OnConnectListener,OnKeyListener,OnLeNotificationListener,OnElectricityRequestListener{

    private BaseView mView;

    private interface BleListenerHolder{
        BleListener BLE_LISTENER = new BleListener();
    };

    public static BleListener getDefault(){
        return BleListenerHolder.BLE_LISTENER;
    }

    public BleListener init(BaseView iView) {
        mView = iView;
        return this;
    }

    @Override
    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        mView.showResult(bluetoothDevice);
    }

    @Override
    public void onScanCompleted() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onFailed(int i) {

    }

    @Override
    public void isConnecting() {

    }

    @Override
    public void onKeyGenerated(String key) {

    }

    @Override
    public void onSetLocalKey() {

    }

    @Override
    public void onReadHistroyInfo() {

    }

    @Override
    public void onHistroyInfoDetected() {

    }

    @Override
    public void onHistroyInfoDeleted() {

    }

    @Override
    public void onElectricityDetected(String electricity) {

    }
}
    

