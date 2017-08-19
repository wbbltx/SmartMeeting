package com.newchinese.smartmeeting.presenter.record;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.RecordsContract;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public class RecordsFragPresenter extends BasePresenter<RecordsContract.View> implements RecordsContract.Presenter {

    @Override
    public boolean isBluetoothOpen() {
        return false;
    }

    @Override
    public void openBle() {

    }

    @Override
    public void scanBlueDevice() {

    }
}
