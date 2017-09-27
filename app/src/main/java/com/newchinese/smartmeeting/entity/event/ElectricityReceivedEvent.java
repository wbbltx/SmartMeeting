package com.newchinese.smartmeeting.entity.event;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class ElectricityReceivedEvent {

    private String value;
    private boolean isLowPower;

    public boolean isLowPower() {
        return isLowPower;
    }

    public void setLowPower(boolean lowPower) {
        isLowPower = lowPower;
    }

    public ElectricityReceivedEvent(String value, boolean isLowPower) {

        this.value = value;
        this.isLowPower = isLowPower;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
