package com.newchinese.coolpensdk.manager;


import android.content.Context;
import android.util.Log;

import com.newchinese.coolpensdk.constants.PointFromType;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.listener.OnPointListener;

/**
 * Description:   API初始化类
 * author         xulei
 * Date           2017/4/25
 */

public class DrawingboardAPI {
    private DrawingboardManager manager;
    private boolean isLimitWrite = false; //限制书写标记 true限制，false不限制

    private static class SingletonHolder {
        private static final DrawingboardAPI INSTANCE = new DrawingboardAPI();
    }

    public static DrawingboardAPI getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private DrawingboardAPI() {
    }

    /**
     * 限制书写
     */
    public void setIsLimitWrite(boolean isLimitWrite) {
        this.isLimitWrite = isLimitWrite;
    }

    /**
     * 初始化
     */
    public void init(Context context, String appKey) {
        initState(context, appKey);
    }

    /**
     * 初始化带限制
     */
    public void init(Context context, String appKey, boolean isLimitWrite) {
        this.isLimitWrite = isLimitWrite;
        initState(context, appKey);
    }

    private void initState(Context context, String appKey) {
        if (manager == null) {
            manager = new DrawingboardManager(context.getApplicationContext(), appKey);
        }
        //初始化蓝牙
        initBlue(context);
        //初始化相关设置
        LogicController.getInstance().setBookInfo();
    }

    private void initBlue(Context context) {
        BluetoothLe.getDefault().init(context);
        BluetoothLe.getDefault().setOnCharacterReadListener(new OnCharacterReadListener() {
            @Override
            public void onReadHistoricalData(String historicalInfo) {
                if (!isLimitWrite)
                    startFilterPoint(historicalInfo, PointFromType.POINT_FROM_SAVE);
            }

            @Override
            public void onReadInstantData(String instantInfo) {
                if (!isLimitWrite)
                    startFilterPoint(instantInfo, PointFromType.POINT_FROM_DRAW);
            }
        });
    }

    public void setOnPointListener(OnPointListener onPointListener) {
        if (manager != null)
            manager.setPointListener(onPointListener);
        else
            Log.e("coolPenError", "11001:未对SDK进行初始化");
    }

    NotePoint getAnalysisPoint(String value) {
        return manager.getAnalysisPoint(value);
    }

    private void startFilterPoint(String value, int fromType) {
        if (manager != null)
            manager.startFilterPoint(value, manager.getAnalysisPoint(value), fromType);
        else
            Log.e("coolPenError", "11001:未对SDK进行初始化");
    }

    public int getPageIndex(NotePoint notePoint) {
        if (manager != null)
            return manager.getPageIndex(notePoint);
        else {
            Log.e("coolPenError", "11001:未对SDK进行初始化");
            return 0;
        }
    }

    public boolean isSameNotePage(NotePoint notePoint) {
        if (manager != null)
            return manager.isSameNotePage(notePoint);
        else {
            Log.e("coolPenError", "11001:未对SDK进行初始化");
            return false;
        }
    }

    public void setBookSize(float width, float height) {
        if (manager != null)
            manager.setBookSize(width, height);
        else
            Log.e("coolPenError", "11001:未对SDK进行初始化");
    }

    public void setBaseOffset(float baseXOffset, float baseYOffset) {
        if (manager != null)
            manager.setBaseOffset(baseXOffset, baseYOffset);
        else
            Log.e("coolPenError", "11001:未对SDK进行初始化");
    }

    public void clearCache() {
        if (manager != null)
            manager.clearCache();
        else
            Log.e("coolPenError", "11001:未对SDK进行初始化");
    }
}
