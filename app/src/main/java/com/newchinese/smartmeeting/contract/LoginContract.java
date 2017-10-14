package com.newchinese.smartmeeting.contract;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.base.BaseToolbar;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;

/**
 * Created by Administrator on 2017-08-24.
 */

public interface LoginContract {

    interface LoginIView<E> extends BaseToolbar {

        void skipWhat();

        void updateView(E e);

        void showLoading(String msg);

        void getDynamicMsg(LoginData data);

        void getDynamicMsg(String data);
    }


    abstract class LoginIView1<E> extends AppCompatActivity implements BaseView<E>, BaseToolbar {
        abstract void skipRegist();

        abstract void skipForget(String user);

        abstract void skipLogin();
    }

    interface LoginIPresenter<V> {

        void loginPhone(String user, String pass);

        void loginQQ(String openid, String token);

        void loginWechat(String openid, String accessToken);

        void regist(String phone, String pass, String code, String nick, String icon);

        void verifyCode(String phone);

        void verifyForgetCode(String phone);

//        void uploadInfo(String nick, String icon);

        void forgetPass(String phone, String code, String pass);

        void dynamicPass(String phone);

        LoginIPresenter attach(V v);

        LoginIPresenter detach();

        void getSpan(TextView tv, String txt);

        void onResult(boolean succ, String type, BaseResult<LoginData> data);

        void onSMSResult(boolean succ, String type, BaseResult<String> data);

        void loading();

        void complete();
    }

    interface LoginIModel {
        void login(LoginData data);

        void loginQQ(LoginData data);

        void loginWeChat(LoginData data);

        void regist(LoginData data);

        void forget(LoginData data);

        void verify(LoginData data);

        void verifyForget(LoginData data);

        void dynamic(LoginData data);

        void updateNick(LoginData data);

        void updateIcon(LoginData data);
    }
}
