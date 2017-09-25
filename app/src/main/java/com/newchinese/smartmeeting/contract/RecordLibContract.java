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
        //删除从画板页面跳转的录屏文件
        void deleteRecord(List<String> pathList, List<Boolean> isSelectedList,int pageIndex);
        //删除从记录页面跳转的录屏文件
        void deleteCollectRecord(List<String> pathList, List<Boolean> isSelectedList,int pageIndex);

    }
}
