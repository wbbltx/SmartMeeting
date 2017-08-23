package com.newchinese.smartmeeting.model.event;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class ScanResultEvent {
    private int flag;  //1是连接状态 0是断开状态

    public ScanResultEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
