package com.newchinese.smartmeeting.presenter.main;

import android.content.ComponentName;

import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.MainContract;
import com.newchinese.smartmeeting.ui.main.BleListener;
import com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity;

/**
 * Description:
 * author         xulei
 * Date           2017/8/19
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    @Override
    public void checkjumpDrawingBoard() {
        ComponentName topActivity = App.getAppliction().getTopActivity();
        if (!DrawingBoardActivity.class.getName().equals(topActivity.getClassName())) {
            mView.jumpDrawingBoard();
        }
    }

    /**
     * 存记录
     */
    @Override
    public void saveRecord() {
        
    }

    /**
     * 存页
     */
    @Override
    public void savePage(NotePoint notePoint) {

    }

    /**
     * 存线点
     */
    @Override
    public void saveStrokeAndPoint(NotePoint notePoint) {

    }
    @Override
    public void initListener() {
        BluetoothLe.getDefault().setOnBleScanListener(BleListener.getDefault().init(mView));
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
}
