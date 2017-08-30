package com.newchinese.smartmeeting.base;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.model.event.ConnectEvent;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.widget.ScanResultDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Description:   基于MVP的基本Activity
 * author         xulei
 * Date           2017/8/17
 */

public abstract class BaseActivity<T extends BasePresenter, E> extends BaseSimpleActivity implements BaseView<E> {
    private static final String TAG = "BaseActivity";
    protected T mPresenter;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    protected ScanResultDialog scanResultDialog;
//    public Animation animation;

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        scanResultDialog = new ScanResultDialog(this);
        //初始化Presenter
        mPresenter = initPresenter();
//        animation = AnimationUtils.loadAnimation(this, R.anim.pen_loading);
        //给Presenter绑定View
        if (mPresenter != null) {
            mPresenter.attachView(this);
            mPresenter.onPresenterCreated();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter.onPresenterDestroy();
        }
    }

    protected abstract T initPresenter();

    @Override
    public void onScanComplete() {
    }

    @Override
    public void showResult(E e) {
    }

    protected void showDialog(final BluetoothDevice bluetoothDevice) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("是否连接新笔" + bluetoothDevice.getAddress())
                .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new ConnectEvent(bluetoothDevice, 0));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    protected void onComplete() {
        XLog.d(TAG, TAG + " onComplete");
        int count = scanResultDialog.getCount();
        List<BluetoothDevice> devices = scanResultDialog.getDevices();
        String address = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS);
        if (count == 0) {//如果没有搜索到笔，提示
            CustomizedToast.showShort(App.getAppliction(), "请开启酷神笔！");
        } else {
            for (BluetoothDevice device : devices) {
                XLog.d(TAG, "扫描到的所有设备：" + device.getAddress());
                if (device.getAddress().equals(address)) {
                    XLog.d(TAG, "搜索结果列表中报验上次连接的笔："+address);
                    EventBus.getDefault().post(new ConnectEvent(device, 0));
                    return;
                }
            }
            if (count == 1) {
                showDialog(devices.get(0));
            } else {
                scanResultDialog.show();
            }
        }
    }

    @Override
    public void onSuccess() {//设置图标的状态为连接
        XLog.d(TAG, TAG + " onSuccess");
    }

    @Override
    public void onFailed() {//设置图标的状态为断开
        XLog.d(TAG, TAG + " onFailed");
    }

    @Override
    public void onConnecting() {//设置图标的状态为正在连接
        XLog.d(TAG, TAG + " onConnecting");
    }

    @Override
    public void onDisconnected() {//设置图标的状态为断开
        XLog.d(TAG, TAG + " onDisconnected");
    }

    @Override
    public void onElecReceived(String ele) {
        XLog.d(TAG, TAG + " onElecReceived");
    }



    @Override
    public void onHistoryDetected(String msg, final PopWindowListener listener) {
        //应该弹出询问框 读取或者删除存储数据
        XLog.d(TAG, TAG + " onHistoryDetected");
        new AlertDialog.Builder(this)
                .setTitle("是否读取历史数据")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onConfirm(1);
                        }
                    }
                })
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onCancel(1);
                        }
                    }
                })
                .create().show();
    }

}
