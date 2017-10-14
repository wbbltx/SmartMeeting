package com.newchinese.smartmeeting.entity.listener;

import android.bluetooth.BluetoothDevice;

import com.newchinese.coolpensdk.listener.OnBleScanListener;
import com.newchinese.coolpensdk.listener.OnConnectListener;
import com.newchinese.coolpensdk.listener.OnElectricityRequestListener;
import com.newchinese.coolpensdk.listener.OnKeyListener;
import com.newchinese.coolpensdk.listener.OnLeNotificationListener;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.ui.meeting.activity.DraftBoxActivity;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2017/8/19 0019.
 */

public class BleListener implements OnBleScanListener, OnConnectListener, OnKeyListener, OnLeNotificationListener, OnElectricityRequestListener, PopWindowListener {

    private DraftBoxActivity mView;
    private boolean isInited;
    private static String TAG = "BleListener";

    @Override
    public void onConfirm(int tag) {//确认读取存储数据
        XLog.d(TAG, TAG + " onConfirm");
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_STORAGE_CHANNEL);
        if (mView != null){
            mView.showAnim();
        }
        Flowable.timer(600, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.READ_STORAGE_INFO);
                    }
                });
    }

    @Override
    public void onCancel(int i) {//删除存储数据
        XLog.d(TAG, TAG + " onCancel");
        if (mView != null){
            mView.showAnim();
        }
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.EMPTY_STORAGE_DATA);
    }

    private interface BleListenerHolder {
        BleListener BLE_LISTENER = new BleListener();
    }

    ;

    public static BleListener getDefault() {
        return BleListenerHolder.BLE_LISTENER;
    }

    public BleListener init(DraftBoxActivity iView) {
        if (mView == null) {
            mView = iView;
//            isInited = true;
        }
        return this;
    }

    public void unInit(){
        mView = null;
    }

    @Override
    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        if (mView != null)
            mView.showResult(bluetoothDevice);
    }

    @Override
    public void onScanCompleted() {
        if (mView != null)
            mView.onScanComplete();
    }

    @Override
    public void onConnected() {
        XLog.d(TAG, TAG + " 已连接");
        //友盟统计
        MobclickAgent.onEvent(App.getAppliction(),"connect_success");
        //连接成功将临时变量中的地址和名称放入sp中
        SharedPreUtils.setString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS, DataCacheUtil.getInstance().getDevice().getAddress());
        SharedPreUtils.setString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_NAME, DataCacheUtil.getInstance().getDevice().getName());
        //设置已经连接成功过
        SharedPreUtils.setBoolean(App.getAppliction(), BluCommonUtils.IS_FIRST_LAUNCH, false);
        //设置当前蓝牙的连接状态
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_CONNECTED);
//        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
        mView.onSuccess();
    }

    @Override
    public void onDisconnected() {
        XLog.d(TAG, TAG + " 连接断开");
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_DISCONNECTED);
//        DataCacheUtil.getInstance().setFirstTime(true);
        if (mView != null)
            mView.onDisconnected();
    }

    @Override
    public void onFailed(int i) {
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_FAILED);
        mView.onFailed();
        XLog.d(TAG, "连接失败"+i);
    }

    @Override
    public void isConnecting() {
        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_CONNECTING);
        mView.onConnecting();
        XLog.d(TAG, TAG + " 连接中...");
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

    /**
     * 存储数据读取完毕
     */
    @Override
    public void onReadHistroyInfo() {
        XLog.d(TAG, TAG + " onReadHistroyInfo");
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.EMPTY_STORAGE_DATA);
        if (mView != null){
            mView.dismissAnim();
        }
        Flowable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
                    }
                });
    }

    @Override
    public void onHistroyInfoDetected() {
        XLog.d(TAG, TAG + " onHistroyInfoDetected");
        mView.onHistoryDetected(this);
    }

    /**
     * 存储数据删除完成
     */
    @Override
    public void onHistroyInfoDeleted() {
        BluetoothLe.getDefault().sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
        if (mView != null){
            mView.dismissAnim();
        }
    }

    @Override
    public void onElectricityDetected(String s) {
        String str = s.substring(s.length() - 2, s.length());
        str = Integer.valueOf(str, 16).toString();
        if (mView != null) {
            mView.onElecReceived(str);
        }
    }
}
    

