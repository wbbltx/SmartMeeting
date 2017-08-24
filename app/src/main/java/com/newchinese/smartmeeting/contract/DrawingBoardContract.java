package com.newchinese.smartmeeting.contract;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.view.View;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Description:   画板页Contract
 * author         xulei
 * Date           2017/8/18
 */

public interface DrawingBoardContract {
    interface View<E> extends BaseView<E> {
        void getDataBasePoint(com.newchinese.coolpensdk.entity.NotePoint notePoint, int strokeColor, float strokeWidth);

        void getFirstStrokeCachePoint(com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void clearCanvars();

        void setTitleText(int pageIndex);
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void readDataBasePoint(int pageIndex);

        void loadFirstStokeCache();

        Bitmap viewToBitmap(android.view.View view);

        void savePageThumbnail(Bitmap bitmap, int pageIndex);
//        void scanBlueDevice();

        boolean isBluetoothOpen();

//        void openBle();

//        void connectDevice(String address);

        /**
         * 录屏保存到数据库
         */
        void saveRecord(String path);

        boolean isConnected();

        /**
         * 初始化录屏
         */
        void initRecord(Context context);
        /**
         * 是否正在录屏
         */
        boolean isRecording();

        /**
         * 停止录屏
         */
        void stopRecord();

        /**
         * 开始luping
         */
        void startRecord(Context context);

        /**
         *
         */
        void extra(MediaProjection mediaProjection);
    }
}
