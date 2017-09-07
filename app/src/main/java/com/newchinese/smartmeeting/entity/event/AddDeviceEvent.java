package com.newchinese.smartmeeting.entity.event;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public class AddDeviceEvent {

    private BluetoothDevice bluetoothDevice;

    public AddDeviceEvent(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
}
