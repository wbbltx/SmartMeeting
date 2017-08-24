package com.newchinese.smartmeeting.base;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.SharedPreUtils;


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
    public Animation animation;

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        //初始化Presenter
        mPresenter = initPresenter();

        animation = AnimationUtils.loadAnimation(this, R.anim.pen_loading);
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

    @Override
    public void onSuccess() {//设置图标的状态为连接
        XLog.d(TAG,TAG+" onSuccess");
//        SharedPreUtils.setString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS, BluCommonUtils.getDeviceAddress());
    }

    @Override
    public void onFailed() {//设置图标的状态为断开
        XLog.d(TAG,TAG+" onFailed");
    }

    @Override
    public void onConnecting() {//设置图标的状态为正在连接
        XLog.d(TAG,TAG+" onConnecting");
    }

    @Override
    public void onDisconnected() {//设置图标的状态为断开
        XLog.d(TAG,TAG+" onDisconnected");
    }

    @Override
    public void onElecReceived(String ele) {
        XLog.d(TAG,TAG+" onElecReceived");
    }

    @Override
    public void onHistoryDetected(String msg, final PopWindowListener listener) {
        //应该弹出询问框 读取或者删除存储数据
        showDialog(msg, listener,1);
    }

    public void showDialog(String msg, final PopWindowListener listener, final int tag) {
        if (!mAlertDialog.isShowing()) {
            return;
        }
        mBuilder = mBuilder == null ? new AlertDialog.Builder(this) : mBuilder;
        mAlertDialog = mAlertDialog == null ? mBuilder.setTitle("提示：")
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onConfirm(tag);
                        }
                    }
                })
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                })
                .create() : mAlertDialog;
    }


}
