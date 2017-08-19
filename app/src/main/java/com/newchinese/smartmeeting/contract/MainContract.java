package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface MainContract {
    interface View<E> extends BaseView<E> {
        void jumpDrawingBoard();
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void checkjumpDrawingBoard();

        void saveRecord();

        void savePage(final com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void saveStrokeAndPoint(final com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void initListener();
    }
}
