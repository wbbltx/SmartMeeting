package com.newchinese.smartmeeting.contract;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public interface PlayBackContract {
    interface View<E> extends BaseView<E>{

        void clearCanvars();

        void setTitleText(int pageIndex);

        void insertPic(String path, Matrix matrix);

    }

    interface Presenter extends BaseSimplePresenter<View>{

        void readData(int pageIndex);

        void hasPic(int pageIndex);

    }
}
