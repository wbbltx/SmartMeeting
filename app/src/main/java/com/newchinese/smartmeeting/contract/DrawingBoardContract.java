package com.newchinese.smartmeeting.contract;

import android.graphics.Bitmap;
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
    }
}
