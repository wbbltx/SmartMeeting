package com.newchinese.smartmeeting.contract;

import android.app.Activity;

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

        void showToast(String toastMsg);
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void requestPermissing(Activity activity);

        void initListener();

        void checkjumpDrawingBoard(com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void initNoteRecord();

        void savePage(final com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void saveStrokeAndPoint(final com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void createSDCardDirectory();

        void saveRecordPage(int i);
    }
}
