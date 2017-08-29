package com.newchinese.smartmeeting.model.event;

import android.bluetooth.BluetoothDevice;

import com.newchinese.coolpensdk.manager.BluetoothLe;

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
