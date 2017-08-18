package com.newchinese.coolpensdk.listener;

/**
 * @anthor wubinbin
 * @time 2017/4/24 17:20
 */

public interface OnLeNotificationListener {

    /**
     * 历史笔迹信息
     */
    public abstract void onReadHistroyInfo();

    /**
     * 检测到有历史信息
     */
    public abstract void onHistroyInfoDetected();

    /**
     * 历史信息删除完成
     */
    public abstract void onHistroyInfoDeleted();



}
