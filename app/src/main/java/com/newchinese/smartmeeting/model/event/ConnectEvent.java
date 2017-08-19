package com.newchinese.smartmeeting.model.event;

/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class ConnectEvent {

    private String address;
    private int flag;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ConnectEvent(String address, int flag) {
        this.address = address;
        this.flag = flag;
    }
}
