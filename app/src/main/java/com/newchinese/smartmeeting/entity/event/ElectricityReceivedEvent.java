package com.newchinese.smartmeeting.entity.event;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class ElectricityReceivedEvent {

    private String value;

    public ElectricityReceivedEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
