package com.newchinese.smartmeeting.model.event;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class ConnectEvent {

    private BluetoothDevice device;
    private int flag;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ConnectEvent(BluetoothDevice device, int flag) {

        this.device = device;
        this.flag = flag;
    }
}
