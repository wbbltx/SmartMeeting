package com.newchinese.smartmeeting.contract;

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
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void readDataBasePoint(int pageIndex);

        void loadFirstStokeCache();
    }
}
