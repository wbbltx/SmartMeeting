package com.newchinese.smartmeeting.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.projection.MediaProjection;
import android.util.DisplayMetrics;
import android.view.View;

import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

import io.reactivex.disposables.Disposable;

/**
 * Description:   画板页Contract
 * author         xulei
 * Date           2017/8/18
 */

public interface DrawingBoardActContract {
    interface View<E> extends BaseView<E> {
        void getDataBasePoint(com.newchinese.coolpensdk.entity.NotePoint notePoint, int strokeColor,
                              float strokeWidth, int cachePageIndex);

        void getFirstStrokeCachePoint(com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void clearCanvars();

        void setTitleText(int pageIndex);

        void setRecordTime(String time);

        void setRecordCount(int i);

        void setInsertViewBitmap(Bitmap insertBitmap, int cachePageIndex);

        void setInsertViewMatrix(Matrix matrix, int cachePageIndex);

        void hideTakePhotoWindow();

        void openEditInsertImage();

        void closeEditInsertImage();
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void readDataBasePoint(int pageIndex);

        void loadFirstStokeCache();

        Bitmap viewToBitmap(android.view.View view);

        void savePageThumbnail(Bitmap bitmap, int pageIndex);

        void saveInsertImageToData(int pageIndex, Matrix imageMatrix);

        void readInsertImageFromData(int pageIndex);

        void deleteInsertImageToData(int pageIndex);

        void isCurrentPageHasInsertImage(int pageIndex);

        String operateInsertImag(Activity activity, int requestCode, Matrix matrix, Intent data, int cachePageIndex);

        void loadCacheMatrix(int cachePageIndex);

        boolean isBluetoothOpen();

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
        void stopRecordTimer();

        /**
         * 开始luping
         */
        void startRecordTimer();

        /**
         * 待扩展
         */
        void extra(MediaProjection mediaProjection);

        void queryRecordCount(int pageIndex);

        /**
         * 记录回放
         */
        void playBack(DrawingBoardView drawingBoardView);
    }
}
