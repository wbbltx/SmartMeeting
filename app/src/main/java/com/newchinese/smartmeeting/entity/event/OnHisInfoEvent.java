package com.newchinese.smartmeeting.entity.event;

/**
 * Created by Administrator on 2017/10/27 0027.
 */

public class OnHisInfoEvent {

    private String flag;

    public OnHisInfoEvent(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
