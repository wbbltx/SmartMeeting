package com.newchinese.smartmeeting.contract;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public interface AboutContract {

    interface AboutIView{
    };

    interface AboutIPresenter<V>{

        AboutIPresenter attach(V v);

        AboutIPresenter detach();

        void checkVersion();

        void loading();

        void result();
    };

    interface AboutIModel{

        void checkVersion();

    };
}
