package com.newchinese.smartmeeting.util;

/**
 * 所有蓝牙公共信息
 */
public class BluCommonUtils {
    public static final String SAVE_WRITE_PEN_KEY = "writePenKey";//保存写入蓝牙的KEY
    public static final String SAVE_CONNECT_BLU_INFO_NAME = "connectBluInfo_name";//保存连接蓝牙信息-名称
    public static final String SAVE_CONNECT_BLU_INFO_ADDRESS = "connectBluInfo_Address";//保存连接蓝牙信息-mac地址

    public static final int PEN_CONNECTED = 1;
    public static final int PEN_DISCONNECTED = 0;
    public static final int PEN_FAILED = -1;

    /**
     * 保存蓝牙地址的临时变量
     */
    public static String DEVICE_ADDRESS;

    public static String getDeviceAddress() {
        return DEVICE_ADDRESS;
    }

    public static void setDeviceAddress(String deviceAddress) {
        DEVICE_ADDRESS = deviceAddress;
    }
}
