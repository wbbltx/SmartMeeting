package com.newchinese.smartmeeting.contract;

import android.view.View;
import android.widget.TextView;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.model.bean.NotePage;

import java.util.List;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface DraftBoxContract {
    interface View<E> extends BaseView<E> {
        void getActivePageList(List<NotePage> pageList);

        void setState(int state);

//        void onScanComplete();
//
//        void showResult(E e);
//
//        void onSuccess();
//
//        void onFailed();
//
//        void onConnecting();
//
//        void onDisconnected();
//
//        void onHistoryDetected(String str, PopWindowListener popWindowListener);
//
//        void onElecReceived(String ele);
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void initListener();

        boolean isBluetoothOpen();

        void openBle();

        void scanBlueDevice();

        void stopScan();

        boolean isConnected();

        void disConnect();

        void connectDevice(String add);

        void loadActivePageList();

        void requestElectricity();

        void startTimer();

        void stopTimer();

        void updatePenState(int state);
    }
}
