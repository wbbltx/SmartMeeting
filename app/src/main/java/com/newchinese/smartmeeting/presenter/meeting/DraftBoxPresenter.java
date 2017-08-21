package com.newchinese.smartmeeting.presenter.meeting;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxContract;

/**
 * Description:   草稿箱Presenter
 * author         xulei
 * Date           2017/8/18
 */

public class DraftBoxPresenter extends BasePresenter<DraftBoxContract.View> implements DraftBoxContract.Presenter {
    @Override
    public boolean isBluetoothOpen() {

        return false;
    }


    @Override
    public void openBle() {
        BluetoothLe.getDefault().enableBluetooth();
    }

    @Override
    public void scanBlueDevice() {

    }
}
