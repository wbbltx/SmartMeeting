package com.newchinese.smartmeeting.presenter.login;

import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.model.LoginModelImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

/**
 * Created by Administrator on 2017-08-24.
 */

public class LoginPresenterImpl implements LoginContract.LoginIPresenter<LoginContract.LoginIView<BaseResult<LoginData>>> {

    private LoginContract.LoginIView<BaseResult<LoginData>> mV;
    private LoginModelImpl mLoginModel;

    @Override
    public void loginWechat() {

    }

    @Override
    public void loginQQ() {

    }

    @Override
    public void loginPhone(String user, String pass) {
        mLoginModel.login(new LoginData().setTel(user).setPassword(pass), false);
    }

    @Override
    public void regist(String phone, String pass, String code) {
        mLoginModel.regist(new LoginData().setTel(phone).setPassword(pass).setCode(code).setNickname("nick").setIcon("icon").setIcon_format("icon_format"));
    }

    @Override
    public void loginQuick(String phone, String code) {
        mLoginModel.login(new LoginData().setTel(phone).setCode(code), true);
    }

    @Override
    public void verifyCode(String phone) {
        mLoginModel.verify(new LoginData().setTel(phone));
    }

    @Override
    public void uploadInfo(String nick, String icon) {
        GreenDaoUtil.getInstance().getDaoSession().clear();
        LoginData data = GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().queryBuilder().unique();
        data.setId(null).setTel(null).setNickname(null);
        mLoginModel.updateIcon(data.setNickname(nick).setIcon(icon).setIcon_format("png"));
        mLoginModel.updateNick(data.setNickname(nick).setIcon(icon).setIcon_format("png"));
    }

    @Override
    public void forgetPass(String phone, String code, String pass) {
        mLoginModel.forget(new LoginData().setTel(phone).setCode(code).setPassword(pass));
    }

    @Override
    public void getSpan(TextView tv, String txt) {
        if (txt.length() < 2) {
            tv.setVisibility(View.GONE);
            return;
        }
        SpannableStringBuilder span = new SpannableStringBuilder(txt);
        int start = txt.length() - 2;
        int end = txt.length();
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (mV != null) {
                    mV.skipWhat();
                }
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        },start, end, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(tv.getContext().getResources().getColor(R.color.simple_blue)),start, end, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(span);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void dynamicPass(String phone){
        mLoginModel.dynamic(new LoginData().setTel(phone));
    }

    @Override
    public LoginContract.LoginIPresenter attach(LoginContract.LoginIView<BaseResult<LoginData>> v) {
        mV = v;
        mLoginModel = new LoginModelImpl(this);
        return this;
    }

    @Override
    public LoginContract.LoginIPresenter detach() {
        mV = null;
        return this;
    }

    @Override
    public void onResult(boolean succ, BaseResult<LoginData> data) {
        complete();
        if (succ && data == null) {
            CustomizedToast.showLong(App.getAppliction(), "数据异常");
        } else if (succ) {
            CustomizedToast.showLong(App.getAppliction(), data.msg);
            if (data.update && NetUrl.NO_SUCC.equals(data.no)) {
                if (data.data != null) {
                    data.data.id = 1L;
                    GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().insertOrReplaceInTx(data.data);
                }
                if (mV != null) {
                    mV.updateView(data);
                }
            }
        } else {
            CustomizedToast.showLong(App.getAppliction(), data.msg);
        }
    }

    @Override
    public void loading() { //加载中
        if (mV != null) {
            mV.showLoading("正在请求...");
        }
    }

    @Override
    public void complete() {
        if (mV != null) {
            mV.showLoading(null);
        } 
    }
}
