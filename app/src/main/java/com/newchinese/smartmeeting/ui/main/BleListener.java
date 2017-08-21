package com.newchinese.smartmeeting.ui.main;

import android.bluetooth.BluetoothDevice;

import com.newchinese.coolpensdk.listener.OnBleScanListener;
import com.newchinese.coolpensdk.listener.OnConnectListener;
import com.newchinese.coolpensdk.listener.OnElectricityRequestListener;
import com.newchinese.coolpensdk.listener.OnKeyListener;
import com.newchinese.coolpensdk.listener.OnLeNotificationListener;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.contract.DraftBoxContract;
import com.newchinese.smartmeeting.contract.MainContract;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.SharedPreUtils;

/**
 * Created by Administrator on 2017/8/19 0019.
 */

public class BleListener implements OnBleScanListener,OnConnectListener,OnKeyListener,OnLeNotificationListener,OnElectricityRequestListener{

    private DraftBoxContract.View mView;
    private boolean isInited;
    private static String TAG = "BleListener";

    private interface BleListenerHolder{
        BleListener BLE_LISTENER = new BleListener();
    };

    public static BleListener getDefault(){
        return BleListenerHolder.BLE_LISTENER;
    }

    public BleListener init(DraftBoxContract.View iView) {
        if (!isInited) {
            mView = iView;
            isInited = true;
        }
        return this;
    }

    @Override
    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        mView.showResult(bluetoothDevice);
    }

    @Override
    public void onScanCompleted() {
        mView.onScanComplete();
    }

    @Override
    public void onConnected() {
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
    }

    @Override
    public void onDisconnected() {
        XLog.d(TAG,"连接断开");
    }

    @Override
    public void onFailed(int i) {
        XLog.d(TAG,"连接失败");
    }

    @Override
    public void isConnecting() {
        XLog.d(TAG,"连接中...");
    }

    @Override
    public void onKeyGenerated(String key) {
        SharedPreUtils.setString(App.getAppliction(), BluCommonUtils.SAVE_WRITE_PEN_KEY, key);
    }

    @Override
    public void onSetLocalKey() {
        String cacheKeyMessage = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_WRITE_PEN_KEY);
        BluetoothLe.getDefault().setKey(cacheKeyMessage);
    }

    @Override
    public void onReadHistroyInfo() {

    }

    @Override
    public void onHistroyInfoDetected() {

        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.EMPTY_STORAGE_DATA);
    }

    @Override
    public void onHistroyInfoDeleted() {
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
    }

    @Override
    public void onElectricityDetected(String electricity) {

    }
}
    

