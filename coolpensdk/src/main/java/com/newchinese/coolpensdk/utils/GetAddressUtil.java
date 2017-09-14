package com.newchinese.coolpensdk.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Description:   获取设备识别码工具类
 * author         xulei
 * Date           2017/9/13
 */

public class GetAddressUtil {
    /**
     * 获取IMEI码
     * 仅仅只对Android手机有效
     * 需要android.permission.READ_PHONE_STATE权限
     */
    public static String getIMEIAddress(Context context) {
        String szImei;
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.READ_PHONE_STATE", "com.newchinese.smartmeeting"));
        if (permission) {
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            szImei = TelephonyMgr.getDeviceId();
        } else {
            szImei = getAndroidIdAddress(context);
            if (szImei == null) szImei = "";
            Log.e("coolPenError", "11008:无READ_PHONE_STATE权限");
        }
        return szImei;
    }

//    /**
//     * 获取设备Mac地址
//     */
//    public static String getMacAddress(Context context) {
//        Log.e("test_address", "IMEIAddress:" + getIMEIAddress(context));
//        Log.e("test_address", "BlueAddress:" + getBlueAddress());
//        Log.e("test_address", "AndroidIdAddress:" + getAndroidIdAddress(context));
//
//        String macAddress = null;
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
//        if (!wifiManager.isWifiEnabled()) {
//            //必须先打开，才能获取到MAC地址
//            wifiManager.setWifiEnabled(true);
//            wifiManager.setWifiEnabled(false);
//        }
//        if (null != info) {
//            macAddress = info.getMacAddress();
//        }
//        if ("02:00:00:00:00:00".equals(macAddress)) {
//            return get6MacAddress();
//        }
//        return macAddress;
//    }
//
//    /**
//     * 获取6.0及以上设备Mac地址
//     */
//    private static String get6MacAddress() {
//        try {
//            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface nif : all) {
//                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
//                byte[] macBytes = nif.getHardwareAddress();
//                if (macBytes == null) {
//                    return "";
//                }
//                StringBuilder res1 = new StringBuilder();
//                for (byte b : macBytes) {
//                    res1.append(String.format("%02X:", b));
//                }
//                if (res1.length() > 0) {
//                    res1.deleteCharAt(res1.length() - 1);
//                }
//                return res1.toString();
//            }
//        } catch (Exception e) {
//            Log.e("coolPenError", "11008:获取Mac地址失败");
//        }
//        return "02:00:00:00:00:00";
//    }

//    /**
//     * 获取蓝牙设备地址
//     */
//    public static String getBlueAddress() {
//        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter      
//        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        String m_szBTMAC = m_BluetoothAdapter.getAddress();
//        return m_szBTMAC;
//    }

    /**
     * 获取Android ID
     * 它有时为null
     */
    public static String getAndroidIdAddress(Context context) {
        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return m_szAndroidID;
    }
}
