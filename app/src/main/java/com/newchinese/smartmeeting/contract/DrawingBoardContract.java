package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.model.bean.NotePoint;

/**
 * Description:   画板页Contract
 * author         xulei
 * Date           2017/8/18
 */

public interface DrawingBoardContract {
    interface View<E> extends BaseView<E> {
        void getDataBasePoint(NotePoint notePoint, int strokeColor, float strokeWidth);
        void getFirstStrokeCachePoint(com.newchinese.coolpensdk.entity.NotePoint notePoint);
        
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void readDataBasePoint();
        void loadFirstStokeCache();
    }
}
