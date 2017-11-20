package com.newchinese.coolpensdk.listener;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public interface OnConnectListener {

    void onConnected();

    void onDisconnected();

    /**
     * 0 超时 在一定时间内匹配失败连接失败
     * 1 没有正确设置关于key的监听
     * 2 有key 写入失败  可能是已经连接是没有清除之前的记录
     * 3 无key 写入失败  暂时不清楚可能出现的情况
     * 1 2 3会重新发生会导致onDisconnected()被调用
     * @param i
     */
    void onFailed(int i);

    void isConnecting();
}
