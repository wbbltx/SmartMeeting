package com.newchinese.smartmeeting.ui.main;

import android.bluetooth.BluetoothDevice;

import com.newchinese.coolpensdk.listener.OnBleScanListener;
import com.newchinese.coolpensdk.listener.OnConnectListener;
import com.newchinese.coolpensdk.listener.OnElectricityRequestListener;
import com.newchinese.coolpensdk.listener.OnKeyListener;
import com.newchinese.coolpensdk.listener.OnLeNotificationListener;

/**
 * Created by Administrator on 2017/8/19 0019.
 */

public interface BleListener extends OnBleScanListener,OnConnectListener,OnElectricityRequestListener,OnLeNotificationListener,OnKeyListener{


    @Override
    void onScanResult(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord);

    @Override
    void onScanCompleted();

    @Override
    void onConnected();

    @Override
    void onDisconnected();

    @Override
    void onFailed(int i);

    @Override
    void isConnecting();

    @Override
    void onKeyGenerated(String key);

    @Override
    void onSetLocalKey();

    @Override
    void onReadHistroyInfo();

    @Override
    void onHistroyInfoDetected();

    @Override
    void onHistroyInfoDeleted();

    @Override
    void onElectricityDetected(String electricity);
}
    

