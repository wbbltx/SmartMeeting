package com.newchinese.smartmeeting.contract;

import android.app.Activity;
import android.content.Context;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18
 */

public interface MainActContract {
    interface View<E> extends BaseView<E> {
        void showToast(String toastMsg);

        void showDialog();

        void initMaskView();
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void initListener();

        void saveCache(com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void initNoteRecord();

        void savePage(final com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void saveStrokeAndPoint(final com.newchinese.coolpensdk.entity.NotePoint notePoint);

        void createSDCardDirectory();

        void saveRecordPage(int i);

        void disconnect();

        void checkVersion();

        void downLoadApk(Context context);
    }
}
