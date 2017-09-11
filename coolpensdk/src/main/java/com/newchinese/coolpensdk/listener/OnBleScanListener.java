package com.newchinese.coolpensdk.listener;

import android.bluetooth.BluetoothDevice;

public interface OnBleScanListener {
    void onScanResult(BluetoothDevice bluetoothDevice, int rssi,byte[] scanRecord);

    void onScanCompleted();

}
