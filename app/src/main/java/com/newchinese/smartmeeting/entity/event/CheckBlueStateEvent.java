package com.newchinese.smartmeeting.entity.event;

/**
 * Created by Administrator on 2017/8/21 0021.
 */

public class CheckBlueStateEvent {
    private int flag;

    public CheckBlueStateEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
