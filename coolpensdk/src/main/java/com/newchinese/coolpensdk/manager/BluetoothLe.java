package com.newchinese.coolpensdk.manager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.newchinese.coolpensdk.listener.OnBleScanListener;
import com.newchinese.coolpensdk.listener.OnConnectListener;
import com.newchinese.coolpensdk.listener.OnElectricityRequestListener;
import com.newchinese.coolpensdk.listener.OnKeyListener;
import com.newchinese.coolpensdk.listener.OnLeNotificationListener;
import com.newchinese.coolpensdk.listener.OnReadRssiListener;

import java.util.UUID;

/**
 * @anthor wubinbin
 * @time 2017/4/20 17:50
 */
public class BluetoothLe {

    private static final String TAG = BluetoothLe.class.getName();
    private BleManager bleManager;
    private int scanPeriod = 30000;
    private int errorCount = 1;
    private UUID[] serviceUUID = {BluUUIDUtils.BtSmartUuid.UUID_SERVICE.getUuid()};
//    private OnBleScanListener onBleScanListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean is_Receive_No_Key_Write_Success_State;
    private boolean is_Receive_Have_Key_Write_Success_State;
    private boolean is_Receive_Key_State;
    private boolean isServiceDiscovered = false;
    private boolean isHistoricalData;

    private OnReadRssiListener onReadRssiListener;
    private OnLeNotificationListener onLeNotificationListener;
    private OnElectricityRequestListener onElectricityRequestListener;
    private OnConnectListener onConnectListener;
    private OnKeyListener onKeyListener;

    /**
     * 打开存储通道
     */
    public static final int OPEN_STORAGE_CHANNEL = 0;
    /**
     * 打开书写通道
     */
    public static final int OPEN_WRITE_CHANNEL = 1;
    /**
     * 读取存储信息
     */
    public static final int READ_STORAGE_INFO = 2;
    /**
     * 清空存储信息
     */
    public static final int EMPTY_STORAGE_DATA = 3;
    /**
     * 获取电量
     */
    public static final int OBTAIN_ELECTRICITY = 4;
    private String cacheKeyMessage;
    private Runnable runnableObtainKeyState;
    private Runnable runnableNoKeyStateWrite;
    private Runnable runnableHaveKeyWite;
    private Runnable runnableDiscoverService;
    private OnCharacterReadListener onCharacterReadListener;
    private boolean autoConnect = false;

    private static class SingletonHolder {
        private static final BluetoothLe INSTANCE = new BluetoothLe();
    }

    private BluetoothLe() {
    }

    /**
     * 获取Ble实例
     *
     * @return 返回Ble实例
     */
    public static BluetoothLe getDefault() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        if (bleManager == null) {
            bleManager = new BleManager(context.getApplicationContext(), mHandler);
        }
    }

    /**
     * 开启蓝牙 内部检测是否支持BLE
     */
    public boolean enableBluetooth() {
        boolean isOpen = false;
        if (bleManager.isBluetoothSupported() && bleManager.isBleSupported() && !bleManager.isBluetoothOpen()) {
            isOpen = bleManager.enableBluetooth();
        }
        return isOpen;
    }

    /**
     * 关闭蓝牙
     */
    public boolean disableBle() {
        return bleManager.disableBluetooth();
    }

    /**
     * @return true if bluetooth is open
     */
    public boolean isBluetoothOpen() {
        return bleManager.isBluetoothOpen();
    }

//    /**
//     * 扫描回调
//     */
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (onBleScanListener != null) {
//                        onBleScanListener.onScanResult(device, rssi, scanRecord);
//                    }
//                }
//            });
//        }
//    };

    /**
     * @param scanPeriod 设置扫描时长 不设置默认为10秒
     * @return
     */
    public BluetoothLe setScanPeriod(int scanPeriod) {
        this.scanPeriod = scanPeriod;
        return this;
    }

    /**
     * @param serviceUUID 设置扫描目标 不进行设置默认只搜索酷神笔 设置为null搜索不进行过滤 也可以根据场景需求设置需要搜索的服务
     * @return
     */
    public BluetoothLe setScanByServiceUUID(UUID[] serviceUUID) {
        this.serviceUUID = serviceUUID;
        return this;
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        if (isBluetoothOpen()) {
            bleManager.scanLeDevice(serviceUUID, scanPeriod);
        } else {
            Log.i(TAG, "please turn on bluetooth first");
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        bleManager.stopScanLeDevice();
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (onBleScanListener != null) {
//                    onBleScanListener.onScanCompleted();
//                }
//            }
//        });
    }

    /**
     * 连接远程设备
     *
     * @param remote 远程设备，支持mac地址和bluetoothdevice
     */
    public void connectBleDevice(Object remote) {
        bleManager.connect(remote, autoConnect, gattCallback, onConnectListener);
    }

    public void disconnectBleDevice() {
        closeResource();
        bleManager.disconnect();
    }

    private void closeResource() {
        is_Receive_Key_State = false;
        is_Receive_No_Key_Write_Success_State = false;
        is_Receive_Have_Key_Write_Success_State = false;
    }

    public void close() {
        closeResource();
        onReadRssiListener = null;
        onLeNotificationListener = null;
        onConnectListener = null;
        onKeyListener = null;
        runnableObtainKeyState = null;
        runnableNoKeyStateWrite = null;
        runnableHaveKeyWite = null;
        mHandler.removeCallbacksAndMessages(null);
        bleManager.close();
    }

    //    设置是否允许重连 默认不允许
//    public BluetoothLe setRetryConnectEnable(boolean b) {
//        bleManager.setRetryConnectEnable(b);
//        return this;
//    }

    //    设置连接次数 重连状态为FALSE 设置无效 默认一次
//    public BluetoothLe setRetryConnectCount(int i) {
//        bleManager.setRetryConnectCount(i);
//        return this;
//    }

    //    设置连接超时 重连状态为FALSE 设置无效 默认5秒
//    public BluetoothLe setConnectTimeOut(int i) {
//        bleManager.setConnectTimeOut(i);
//        return this;
//    }

    //    允许重连功能开启之后 重连参数有所改变，再次连接以后有可能导致133错误，需要复位 ??
    public void resetRetryConfig() {
        bleManager.resetRetryConfig();
    }

    //    写入命令 默认向UUID_CHAR_WRITE中写命令
    private void writeCharacteristic(byte[] bytes) {
        bleManager.writeCharacteristic(BluUUIDUtils.BtSmartUuid.UUID_CHAR_WRITE.getUuid(), bytes);
    }

    //    发送命令
    private void sendBleInstruct(String instructInfo, boolean isKey) {
        String message = "";
        if (isKey) {
            String local_key = BytesUtils.getBleKey();
            Log.i(TAG, "key generated " + local_key);
            if (onKeyListener != null) {
                onKeyListener.onKeyGenerated(local_key);
            }
            message = instructInfo + local_key;
        } else {
            message = instructInfo;
        }
        Log.i(TAG, "sendBleInstruct " + instructInfo);
        byte[] connKey = BytesUtils.HexString2Bytes(message);
        writeCharacteristic(connKey);
    }

    public void sendBleInstruct(int flag) {
        switch (flag) {
//            打开书写通道 此时读取到的是即时数据
            case OPEN_WRITE_CHANNEL:
                sendBleInstruct(BluUUIDUtils.BluInstruct.OPEN_WRITE_CHANNEL.getUuid(), false);
//                isHistoricalData = false;
                Log.i(TAG, "open channel and clear bluetooth cache");
                clearDeviceCache();
                break;
//            打开存储通道
            case OPEN_STORAGE_CHANNEL:
                sendBleInstruct(BluUUIDUtils.BluInstruct.OPEN_STORAGE_CHANNEL.getUuid(), false);
                break;
//            读取通道信息 此时读取到的是历史数据
            case READ_STORAGE_INFO:
                sendBleInstruct(BluUUIDUtils.BluInstruct.READ_STORAGE_INFO.getUuid(), false);
                isHistoricalData = true;
                break;
//            清空通道信息
            case EMPTY_STORAGE_DATA:
                sendBleInstruct(BluUUIDUtils.BluInstruct.EMPTY_STORAGE_DATA.getUuid(), false);
                break;
//            获取电量信息
            case OBTAIN_ELECTRICITY:
                sendBleInstruct(BluUUIDUtils.BluInstruct.OBTAIN_ELECTRICITY.getUuid(), false);
                break;
        }
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "onConnectionStateChange---" + status + "-------" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "connected 1 level");
//                bleManager.setIsConnected(true);
                errorCount = 1;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDING) {
                            Log.i(TAG, "pipixia lets go discoverService");
                            gatt.discoverServices();
                        }
                    }
                }, 100);

                runnableDiscoverService = new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "if Service is discovered" + isServiceDiscovered + getConnected());
                        if (!isServiceDiscovered) {
                            disconnectBleDevice();
                            if (onConnectListener != null) {
                                onConnectListener.onFailed(0);
                            }
                        }
                    }
                };
                mHandler.postDelayed(runnableDiscoverService, 10000);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange STATE_DISCONNECTED called");
                isServiceDiscovered = false;
                bleManager.setIsConnected(false);
                if (status == 133) {
                    Log.i(TAG, "onConnectionStateChange 133 " + errorCount);
                    final String address = gatt.getDevice().getAddress();
                    bleManager.resetRetryConfig();
                    if (errorCount != 0) {
                        connectBleDevice(address);
                        errorCount--;
                    } else {
                        errorCount = 1;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onConnectListener != null) {
                                    onConnectListener.onFailed(0);
                                }
                            }
                        });
                    }
                } else {
                    Log.i(TAG, "onConnectionStateChange disconnected normal");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (onConnectListener != null) {
                                onConnectListener.onDisconnected();
                            }else {
                            }
                        }
                    });
                    bleManager.setIsConnected(false);
                    Log.i(TAG, "disconnected " + status);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                isServiceDiscovered = true;
                mHandler.removeCallbacks(runnableDiscoverService);
                Log.i(TAG, "onServicesDiscovered received: go enable notification");
                bleManager.enableCharacteristicNotification();
//                Log.i(TAG, "onServicesDiscovered received: 发送命令获取key状态 看是否收到响应" + System.currentTimeMillis());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "send message to obtain key state");
                        sendBleInstruct(BluUUIDUtils.BluInstruct.OBTAIN_KEY_STATE.getUuid(), false);
                    }
                }, 150);

                runnableObtainKeyState = new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "onServicesDiscovered received: send OBTAIN_KEY_STATE again if is_Receive_Key_State is false--" + is_Receive_Key_State);
                        if (!is_Receive_Key_State) {
                            Log.i(TAG, "onServicesDiscovered received:send OBTAIN_KEY_STATE again");
                            sendBleInstruct(BluUUIDUtils.BluInstruct.OBTAIN_KEY_STATE.getUuid(), false);
                        }
                    }
                };
                mHandler.postDelayed(runnableObtainKeyState, 300);

            } else {
                isServiceDiscovered = false;
                Log.i(TAG, "onServicesDiscovered received: Services not Discovered" + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onCharacteristicRead : Characteristic" + characteristic.getUuid());
//
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicWrite ---" + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            final String bluMessage = bytesToHexString(characteristic.getValue());
            Log.i(TAG, "onCharacteristicChanged---" + bluMessage);
            if (bluMessage != null && !"".equals(bluMessage)) {
                if (bluMessage.startsWith("0f0f")) {
                    if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.NOT_KEY_STATE.getMsg())) {
                        Log.i(TAG, "onCharacteristicChanged: no key in pen ---" + bluMessage);
                        is_Receive_Key_State = true;
                        mHandler.removeCallbacks(runnableObtainKeyState);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "send NOT_KEY_WRITE to write key to pen");
                                sendBleInstruct(BluUUIDUtils.BluInstruct.NOT_KEY_WRITE.getUuid(), true);
                            }
                        }, 300);

                        runnableNoKeyStateWrite = new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "if send NOT_KEY_WRITE again if false " + is_Receive_No_Key_Write_Success_State);
                                if (!is_Receive_No_Key_Write_Success_State) {
                                    Log.i(TAG, "send NOT_KEY_STATE again " + is_Receive_No_Key_Write_Success_State);
                                    is_Receive_No_Key_Write_Success_State = false;
                                    sendBleInstruct(BluUUIDUtils.BluInstruct.NOT_KEY_WRITE.getUuid(), true);
                                }
                            }
                        };
                        mHandler.postDelayed(runnableNoKeyStateWrite, 600);

                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.HAVE_KEY_STATE.getMsg())) {
                        Log.i(TAG, "onCharacteristicChanged: have key in pen");
                        is_Receive_Key_State = true;
                        mHandler.removeCallbacks(runnableObtainKeyState);
                        if (onKeyListener != null) {
                            onKeyListener.onSetLocalKey();
                        }
                        if (!cacheKeyMessage.isEmpty()) {
                            Log.i(TAG, "local key is not empty ,send HAVE_KEY_WRITE to pen " + cacheKeyMessage);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendBleInstruct(BluUUIDUtils.BluInstruct.HAVE_KEY_WRITE.getUuid() + cacheKeyMessage, false);
                                }
                            }, 300);
                            runnableHaveKeyWite = new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "if send HAVE_KEY_WRITE again if false " + is_Receive_Have_Key_Write_Success_State);
                                    if (!is_Receive_Have_Key_Write_Success_State) {
                                        Log.i(TAG, "send HAVE_KEY_WRITE again ");
                                        sendBleInstruct(BluUUIDUtils.BluInstruct.HAVE_KEY_WRITE.getUuid() + cacheKeyMessage, false);
                                    }
                                }
                            };
                            mHandler.postDelayed(runnableHaveKeyWite, 600);
                        } else {
                            disconnectBleDevice();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    sendBleInstruct(BluUUIDUtils.BluInstruct.HAVE_KEY_WRITE.getUuid() + "170428121648", false);
                                    if (onConnectListener != null) {
                                        Log.e("Error", "10000：请添加setOnKeyListener监听，并在确保在onSetLocalKey中调用了setKey方法,或者将蓝牙笔设置为配对状态并再次尝试");
                                        onConnectListener.onFailed(1);
                                    }
                                }
                            });
                        }
                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.NOT_KEY_WRITE_SUCCEED_STATE.getMsg())) {
                        Log.i(TAG, "NOT_KEY_WRITE_SUCCEED_STATE:");
                        is_Receive_No_Key_Write_Success_State = true;
                        mHandler.removeCallbacks(runnableNoKeyStateWrite);
//                        String cacheKeyMessage = SharedPreUtils.getString(applicationContext, BluCommonUtils.SAVE_WRITE_PEN_KEY);
                        if (onKeyListener != null) {
                            onKeyListener.onSetLocalKey();
                        }

                        if (cacheKeyMessage != null && !"".equals(cacheKeyMessage)) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "NOT_KEY_WRITE_SUCCEED_STATE :send HAVE_KEY_WRITE" + cacheKeyMessage);
                                    sendBleInstruct(BluUUIDUtils.BluInstruct.HAVE_KEY_WRITE.getUuid() + cacheKeyMessage, false);
                                }
                            }, 300);

                            runnableHaveKeyWite = new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "send HAVE_KEY_WRITE again if false " + is_Receive_Have_Key_Write_Success_State);
                                    if (!is_Receive_Have_Key_Write_Success_State) {
                                        sendBleInstruct(BluUUIDUtils.BluInstruct.HAVE_KEY_WRITE.getUuid() + cacheKeyMessage, false);
                                        Log.i(TAG, "send HAVE_KEY_WRITE again");
                                    }
                                }
                            };
                            mHandler.postDelayed(runnableHaveKeyWite, 600);
                        } else {
//                            Log.e(TAG, "无key时写入成功但cacheKeyMessage为空或空串 可能key没有在sp中保存成功");
                            Log.e("Error", "10086：请添加setOnKeyListener监听，并在确保在onSetLocalKey中调用了setKey方法");
                        }

                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.NOT_KEY_WRITE_FAILURE_STATE.getMsg())) {
//                        执行断开连接
                        is_Receive_No_Key_Write_Success_State = true;
                        mHandler.removeCallbacks(runnableNoKeyStateWrite);
                        Log.i(TAG, "笔内没有保存key信息，写入失败,暂时还不知道什么情况会导致这种失败");
                        disconnectBleDevice();

                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.HAVE_KEY_WRITE_SUCCEED_STATE.getMsg())) {
                        Log.i(TAG, "connected 2 level, send QUERY_STORAGE_INFO and query historical info");
                        is_Receive_Have_Key_Write_Success_State = true;
                        mHandler.removeCallbacks(runnableHaveKeyWite);
                        bleManager.setIsConnected(true);
                        if (bleManager.getScanning()) {
                            stopScan();
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (onConnectListener != null) {
                                    onConnectListener.onConnected();
                                }
                                sendBleInstruct(BluUUIDUtils.BluInstruct.QUERY_STORAGE_INFO.getUuid(), false);
                            }
                        }, 300);

                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.HAVE_KEY_WRITE_FAILURE_STATE.getMsg())) {
                        //执行断开连接
                        Log.e(TAG, "HAVE_KEY_WRITE_FAILURE_STATE");
                        is_Receive_Have_Key_Write_Success_State = true;
                        mHandler.removeCallbacks(runnableHaveKeyWite);
                        disconnectBleDevice();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onConnectListener != null) {
                                    onConnectListener.onFailed(1);
                                }
                            }
                        });
//                        Log.i(TAG, "该情况失败一般因为笔在与第二个设备连接时，没有清除第一个设备的key，导致不匹配，写入失败，应该将笔设置成配对状态");
                        Log.e("Error", "10010: 请将笔设置成配对状态");

                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.NOT_STORAGE_INFO.getMsg())) {
                        //无存储信息，打开书写通道
                        Log.i(TAG, "NOT_STORAGE_INFO,send OPEN_WRITE_CHANNEL");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendBleInstruct(OPEN_WRITE_CHANNEL);
                            }
                        }, 300);

                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.HAVE_STORAGE_INFO.getMsg())) {
                        Log.i(TAG, "HAVE_STORAGE_INFO");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onLeNotificationListener != null) {
                                    onLeNotificationListener.onHistroyInfoDetected();
                                }
                            }
                        });

                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.STORAGE_DATA_READ_END.getMsg())) {
                        Log.i(TAG, "STORAGE_DATA_READ_END");
                        isHistoricalData = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onLeNotificationListener != null) {
                                    onLeNotificationListener.onReadHistroyInfo();
                                }
                            }
                        });
                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.STORAGE_DATA_EMPTY_END.getMsg())) {
                        Log.i(TAG, "STORAGE_DATA_EMPTY_END");
                        isHistoricalData = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onLeNotificationListener != null) {
                                    onLeNotificationListener.onHistroyInfoDeleted();
                                }
                            }
                        });
                    } else if (bluMessage.startsWith(BluUUIDUtils.BluInstructReplyMsg.ELECTRICITY_INFO.getMsg())) {
                        Log.i(TAG, "ELECTRICITY_INFO");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onElectricityRequestListener != null) {
                                    onElectricityRequestListener.onElectricityDetected(bluMessage);
                                }
                            }
                        });
                    }
//                  兼容旧版本
                    if (bluMessage.startsWith("0f0f71")) {
                        String str = bluMessage.substring(bluMessage.length() - 2, bluMessage.length());
                        int storeCount = Integer.parseInt(str);
                        if (storeCount > 1) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (onLeNotificationListener != null) {
                                        onLeNotificationListener.onHistroyInfoDetected();
                                    }
                                }
                            });
                        }
                    }
                } else {
//                    不是0f0f开头的 是笔迹信息
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (onCharacterReadListener != null) {
                                if (isHistoricalData) {
                                    onCharacterReadListener.onReadHistoricalData(bluMessage);
                                } else {
                                    onCharacterReadListener.onReadInstantData(bluMessage);
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "是否被触发？" + status);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onReadRssiListener.onSuccess(rssi);
                    }
                });
            }
        }
    };

    public void enableAntiLost(int millisecond, OnReadRssiListener onReadRssiListener) {
        this.onReadRssiListener = onReadRssiListener;
        bleManager.readRssi(millisecond);
    }

    public void disableAntiLost() {
        bleManager.cancelReadRssiTimerTask();
    }

    private String bytesToHexString(byte[] data) {
        if (data == null)
            return null;
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format("%02x", byteChar));
        return stringBuilder.toString();
    }

    public void setOnConnectListener(OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
    }

    public void setOnLeNotificationListener(OnLeNotificationListener onLeNotificationListener) {
        this.onLeNotificationListener = onLeNotificationListener;
    }

    public void setOnElectricityRequestListener(OnElectricityRequestListener onElectricityRequestListener) {
        this.onElectricityRequestListener = onElectricityRequestListener;
    }

    public void setOnKeyListener(OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
    }

    public void setOnBleScanListener(OnBleScanListener onBleScanListener){
        bleManager.setOnBleScanListener(onBleScanListener);
//        this.onBleScanListener = onBleScanListener;
    }

    public void setOnCharacterReadListener(OnCharacterReadListener onCharacterReadListener) {
        this.onCharacterReadListener = onCharacterReadListener;
    }

    public void setKey(String key) {
        this.cacheKeyMessage = key;
    }

    private void clearDeviceCache() {
        bleManager.clearDeviceCache();
    }

    public boolean getScanning() {
        return bleManager.getScanning();
    }

    public boolean getConnected() {
        return bleManager.getConnected();
    }

    //超时设置 默认10s
    public BluetoothLe setConnectTimeOut(int delayTime){
        bleManager.setDelayTime(delayTime);
        return this;
    }
}
