package com.newchinese.coolpensdk.listener;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public interface OnConnectListener {

    public abstract void onConnected();

    public abstract void onDisconnected();

    public abstract void onFailed(int i);

    public abstract void isConnecting();
}
