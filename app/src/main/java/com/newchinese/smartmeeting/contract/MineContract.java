package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseToolbar;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.FeedBack;
import com.newchinese.smartmeeting.entity.bean.LoginData;

/**
 * Created by Administrator on 2017-08-30.
 */

public interface MineContract {

    interface UpdateIVIew {

        void showLoading(String msg);

        void jumpLogin(String type);
    }

    interface UpdateIPresenter<V> {

        UpdateIPresenter attach(V v);

        void detach();

        void updatePass(String oldPass, String newPass);

        void setPass(final String tel, final String password);

        void feedBack(String content, String contact);

        void loading();

        <T> void onResult(boolean succ, BaseResult<T> data);
    }

    interface UpdateIModel {

        void updatePass(LoginData data);

        void feedBack(FeedBack data);
    }
}
