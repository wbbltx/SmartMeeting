package com.newchinese.smartmeeting.presenter.meeting;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxContract;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public class DraftBoxPresenter extends BasePresenter<DraftBoxContract.View> implements DraftBoxContract.Presenter {

    @Override
    public boolean isBleOpen() {
        return BluetoothLe.getDefault().isBluetoothOpen();
    }

    @Override
    public void openBle() {
        BluetoothLe.getDefault().enableBluetooth();
    }
}
