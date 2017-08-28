package com.newchinese.smartmeeting.ui.main;

import android.bluetooth.BluetoothDevice;

import com.newchinese.coolpensdk.listener.OnBleScanListener;
import com.newchinese.coolpensdk.listener.OnConnectListener;
import com.newchinese.coolpensdk.listener.OnElectricityRequestListener;
import com.newchinese.coolpensdk.listener.OnKeyListener;
import com.newchinese.coolpensdk.listener.OnLeNotificationListener;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;

/**
 * Created by Administrator on 2017/8/19 0019.
 */

public class BleListener implements OnBleScanListener,OnConnectListener,OnKeyListener,OnLeNotificationListener,OnElectricityRequestListener, PopWindowListener {

    private BaseView mView;
    private boolean isInited;
    private static String TAG = "BleListener";

    @Override
    public void onConfirm(int tag) {//确认读取存储数据
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.READ_STORAGE_INFO);
    }

    @Override
    public void onCancel() {//删除存储数据
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.EMPTY_STORAGE_DATA);
    }

    private interface BleListenerHolder{
        BleListener BLE_LISTENER = new BleListener();
    };

    public static BleListener getDefault(){
        return BleListenerHolder.BLE_LISTENER;
    }

    public BleListener init(BaseView iView) {
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
        XLog.d(TAG,TAG+" 已连接");
        //连接成功将临时变量中的地址放入sp中 同时询问有没有存储数据
        SharedPreUtils.setString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS, BluCommonUtils.getDeviceAddress());
//        设置已经连接成功过
        SharedPreUtils.setBoolean(App.getAppliction(),BluCommonUtils.IS_FIRST_LAUNCH,false);
//        设置当前蓝牙的连接状态
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_CONNECTED);
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
        mView.onSuccess();
    }

    @Override
    public void onDisconnected() {
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_DISCONNECTED);
        mView.onDisconnected();
        XLog.d(TAG,TAG+" 连接断开");
    }

    @Override
    public void onFailed(int i) {
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_FAILED);
        mView.onFailed();
        XLog.d(TAG,"连接失败");
    }

    @Override
    public void isConnecting() {
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_CONNECTING);
        mView.onConnecting();
        XLog.d(TAG,TAG+" 连接中...");
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
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
    }

    @Override
    public void onHistroyInfoDetected() {
        mView.onHistoryDetected(App.getAppliction().getResources().getString(R.string.read_channel),this);
    }

    @Override
    public void onHistroyInfoDeleted() {
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
    }

    @Override
    public void onElectricityDetected(String s) {
        String str = s.substring(s.length() - 2, s.length());
        str = Integer.valueOf(str, 16).toString();
        if (mView != null){
            mView.onElecReceived(str);
        }
    }
}
    

