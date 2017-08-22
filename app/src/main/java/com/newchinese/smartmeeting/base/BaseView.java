package com.newchinese.smartmeeting.base;

import com.newchinese.smartmeeting.listener.PopWindowListener;

/**
 * Description:   MVP中的基本view接口类
 * author         xulei
 * Date           2017/8/17
 */

public interface BaseView<T> {

    void onScanComplete();

    void showResult(T t);

    void onSuccess();

    void onFailed();

    void onConnecting();

    void onDisconnected();

    void onHistoryDetected(String str, PopWindowListener popWindowListener);

    void onElecReceived(String ele);
}
