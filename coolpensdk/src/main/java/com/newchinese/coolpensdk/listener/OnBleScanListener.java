package com.newchinese.coolpensdk.listener;

import android.bluetooth.BluetoothDevice;

public interface OnBleScanListener {
    public abstract void onScanResult(BluetoothDevice bluetoothDevice, int rssi,byte[] scanRecord);

    public abstract void onScanCompleted();

}
