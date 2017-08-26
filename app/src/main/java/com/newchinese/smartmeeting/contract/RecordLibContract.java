package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;

import java.util.List;

/**
 * Created by Administrator on 2017/8/26 0026.
 */

public interface RecordLibContract {

    interface View<E> extends BaseView<E>{

        void setTitle(int pageIndex);

        void refreshRecord(List<String> recordPath);

    }

    interface Presenter extends BaseSimplePresenter<View>{
        void deleteRecord(List<String> pathList, List<Boolean> isSelectedList,int pageIndex);

    }
}
