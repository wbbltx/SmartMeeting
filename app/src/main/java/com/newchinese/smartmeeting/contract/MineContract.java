package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseToolbar;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.FeedBack;
import com.newchinese.smartmeeting.entity.bean.LoginData;

/**
 * Created by Administrator on 2017-08-30.
 */

public interface MineContract {

    interface UpdateIVIew extends BaseToolbar {

        void showLoading(String msg);

        void jumpLogin();
    }

    interface UpdateIPresenter<V> {

        UpdateIPresenter attach(V v);

        void detach();

        void updateNick(String nick);

        void updatePass(String oldPass, String newPass);

        void feedBack(String content, String contact);

        void loading();

        <T> void onResult(boolean succ, BaseResult<T> data);
    }

    interface UpdateIModel {

        void updateNick(LoginData data);

        void updatePass(LoginData data);

        void feedBack(FeedBack data);
    }
}
