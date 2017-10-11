package com.newchinese.coolpensdk.manager;

import android.app.Service;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Description:   主要功能是，分页，获取屏幕宽度，获取纸张大小，根据参数计算需要的比例
 * author         xulei
 * Date           2017/4/25 10:55
 */
public class AdaptManager {
    /**
     * 通过屏幕宽高进行判断，所以该界面必须是全屏的。
     * 如果不是全屏的话，那么需要指定控件的大小。
     *
     * @param context
     * @return
     */
    private static DisplayMetrics getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * @param context
     * @param rowSize 纸张一页的坐标大小
     * @return 纸张对应在屏幕上的显示区域的大小
     */
    public static float[] getTargetSize(Context context, float[] rowSize) {
        DisplayMetrics dm = getScreenSize(context);
        float[] result = new float[2];

        if (rowSize[0] / dm.widthPixels > rowSize[1] / dm.heightPixels) {
            result[0] = dm.widthPixels;
            result[1] = rowSize[0] / dm.widthPixels * dm.heightPixels;
        } else {
            result[0] = rowSize[1] / dm.heightPixels * dm.widthPixels;
            result[1] = dm.heightPixels;
        }
        return result;
    }

    /**
     * @param context
     * @return 张对应在屏幕上的显示区域的大小
     */
    public static float[] getTargetSize(Context context, float x, float y) {
        DisplayMetrics dm = getScreenSize(context);
        float[] result = new float[2];

        if (x / dm.widthPixels > y / dm.heightPixels) {
            result[0] = dm.widthPixels;
            result[1] = x / dm.widthPixels * dm.heightPixels;
        } else {
            result[0] = y / dm.heightPixels * dm.widthPixels;
            result[1] = dm.heightPixels;
        }
        return result;
    }

    /**
     * 根据控件的大小来确定 大小范围
     * x、y需要得到一个比例去填充w、h的区域-------那么以x、y为分母，以w、h为分子，取比值更小的那一个
     */
    public static float[] getTargetSize(float w, float h, float x, float y) {
        float[] result = new float[2];
        if (w / x < h / y) {
            result[0] = w;
            result[1] = y * w / x;
        } else {
            result[0] = x * h / y;
            result[1] = h;
        }
        return result;
    }
}
