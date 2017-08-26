package com.newchinese.smartmeeting.contract;

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

    }

    interface Presenter extends BaseSimplePresenter<View>{

        void readData(DrawingBoardView drawingBoardView,int pageIndex);

    }
}
