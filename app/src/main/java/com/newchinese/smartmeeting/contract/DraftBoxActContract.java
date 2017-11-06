package com.newchinese.smartmeeting.contract;

import android.bluetooth.BluetoothDevice;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.listener.BleListener;
import com.newchinese.smartmeeting.entity.listener.PopWindowListener;
import com.newchinese.smartmeeting.ui.meeting.activity.DraftBoxActivity;

import java.util.List;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface DraftBoxActContract {
    interface View<E> extends BaseView<E> {
        void getActivePageList(List<NotePage> pageList);

        void setState(int state);
        
        void showToast(String toastContent);
        
        void onScanComplete();
//
        void showResult(E e);
//
        void onSuccess();
//
        void onFailed(int i);
//
        void onConnecting();
//
        void onDisconnected();
//
        void onHistoryDetected(PopWindowListener popWindowListener);
//
        void onElecReceived(String ele);

        DraftBoxActivity initBluListener();

        void showAnim();

        void dismissAnim();
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void initListener();

        boolean isBluetoothOpen();

        void openBle();

        void scanBlueDevice();

        void stopScan();

        boolean isConnected();

        void disConnect();

        void connectDevice(BluetoothDevice device);

        void loadActivePageList();

        void createSelectedRecords(List<NotePage> notePageList, List<Boolean> isSelectedList, String recordName);

        void requestElectricity();

        void startTimer();

        void stopTimer();

        void updatePenState(int state);

        boolean isScanning();
    }
}
