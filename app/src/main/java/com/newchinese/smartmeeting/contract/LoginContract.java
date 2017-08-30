package com.newchinese.smartmeeting.contract;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.base.IToolbar;
import com.newchinese.smartmeeting.model.bean.BaseResult;
import com.newchinese.smartmeeting.model.bean.LoginData;

/**
 * Created by Administrator on 2017-08-24.
 */

public interface LoginContract {

    interface LoginIView<E> extends IToolbar {

        void skipWhat();

        void updateView(E e);

        void showLoading(String msg);
    }


    abstract class  LoginIView1<E> extends AppCompatActivity implements BaseView<E>, IToolbar {
        abstract void skipRegist();
        abstract void skipForget(String user);
        abstract void skipLogin();


    }

    interface LoginIPresenter<V> {

        void loginPhone(String user, String pass);

        void loginWechat();

        void loginQQ();

        void regist(String phone, String pass, String code);

        void loginQuick(String phone, String code);

        void verifyCode(String phone);

        void uploadInfo(String nick, String icon);

        void forgetPass(String phone, String code, String pass);

        void dynamicPass(String phone);

        LoginIPresenter attach(V v);

        LoginIPresenter detach();

        void getSpan(TextView tv, String txt);

        void onResult(boolean succ, BaseResult<LoginData> data);

        void loading();

        void complete();
    }

    interface LoginIModel {
        void login(LoginData data, boolean quick);
        void regist(LoginData data);
        void forget(LoginData data);

        void verify(LoginData data);

        void dynamic(LoginData data);

        void updateNick(LoginData data);

        void updateIcon(LoginData data);
    }
}
