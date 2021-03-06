package com.newchinese.smartmeeting.util;

/**
 * 所有蓝牙公共信息
 */
public class BluCommonUtils {
    public static final String SAVE_WRITE_PEN_KEY = "writePenKey";//保存写入蓝牙的KEY
    public static final String SAVE_CONNECT_BLU_INFO_NAME = "connectBluInfo_name";//保存连接蓝牙信息-名称
    public static final String SAVE_CONNECT_BLU_INFO_ADDRESS = "connectBluInfo_Address";//保存连接蓝牙信息-mac地址
    public static final String IS_FIRST_LAUNCH = "isFirstLaunch";//是否曾经连接成功过
    public static final String IS_FIRST_INSTALL = "isFirstInstall";//是否首次安装
    public static final String CLASSIFY_NAME = "classify_name";
    public static final String VERSION_PATH = "version_url";   //版本更新地址
    public static final String DATA = "data";                    //版本更新地址

    public static final String DELETE_MODE = "delete_mode";    //书写列表页状态 删除状态
    public static final String EDIT_MODE = "edit_mode";        //编辑归档状态
    public static final String NORMAL_MODE = "normal_mode";   //普通状态

    public static final int PEN_CONNECTED = 1;
    public static final int PEN_DISCONNECTED = 0;
    public static final int PEN_FAILED = -1;
    public static final int PEN_CONNECTING = 2;


}
