package com.newchinese.smartmeeting.base;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.entity.listener.PopWindowListener;
import com.newchinese.smartmeeting.ui.meeting.activity.DraftBoxActivity;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.entity.event.ConnectEvent;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.widget.HisInfoWindow;
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
    protected ScanResultDialog scanResultDialog;
    protected HisInfoWindow hisInfoWindow;

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        scanResultDialog = new ScanResultDialog(this);
        //初始化Presenter
        mPresenter = initPresenter();
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

    protected void onComplete(Activity context) {
        int count = scanResultDialog.getCount();
        XLog.d(TAG, TAG + " onComplete "+count);
        List<BluetoothDevice> devices = scanResultDialog.getDevices();
        String address = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS);
        if (count == 0) {//如果没有搜索到笔，提示
            XLog.d(TAG, TAG + " 没有搜索到笔 ");
            CustomizedToast.showShort(context, getString(R.string.please_open_pen));
        } else {
            XLog.d(TAG, TAG + " 搜索到笔 ");
            for (BluetoothDevice device : devices) {
                if (device.getAddress().equals(address)) {
                    if (DataCacheUtil.getInstance().getPenState() != BluCommonUtils.PEN_CONNECTED) {
                        EventBus.getDefault().post(new ConnectEvent(device, 0));
                    }
                    return;
                }
            }
            if (scanResultDialog != null && !isFinishing()){
                scanResultDialog.setContent(address,"0");
                scanResultDialog.show();
            }
        }
    }

    public void showDialog(final PopWindowListener listener,View view) {
//        if (){
            hisInfoWindow = new HisInfoWindow(this, listener);
            hisInfoWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//        }
    }
}
