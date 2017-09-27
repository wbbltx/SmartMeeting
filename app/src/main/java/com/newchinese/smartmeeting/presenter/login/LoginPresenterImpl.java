package com.newchinese.smartmeeting.presenter.login;

import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.model.LoginModelImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;

/**
 * Created by Administrator on 2017-08-24.
 */

public class LoginPresenterImpl implements LoginContract.LoginIPresenter<LoginContract.LoginIView<BaseResult<LoginData>>> {

    private LoginContract.LoginIView<BaseResult<LoginData>> mV;
    private LoginModelImpl mLoginModel;
    private String password;

    @Override
    public void loginQQ(String openid, String token) {
        mLoginModel.loginQQ(new LoginData().setOpenid(openid).setToken(token).setFlag("1"));
    }

    @Override
    public void loginWechat(String openid, String accessToken) {
        mLoginModel.loginWeChat(new LoginData().setOpenid(openid).setAccess_token(accessToken));
    }

    @Override
    public void loginPhone(String user, String pass) {
        password = pass;
        mLoginModel.login(new LoginData().setTel(user).setPassword(pass));
    }

    @Override
    public void regist(String phone, String pass, String code) {
        mLoginModel.regist(new LoginData().setTel(phone).setPassword(pass).setCode(code).setNickname("nick").setIcon("icon").setIcon_format("icon_format"));
    }

    @Override
    public void verifyCode(String phone) {
        mLoginModel.verify(new LoginData().setTel(phone));
    }

    @Override
    public void verifyForgetCode(String phone) {
        mLoginModel.verifyForget(new LoginData().setTel(phone));
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
        }, start, end, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(tv.getContext().getResources().getColor(R.color.simple_blue)), start, end, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(span);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void dynamicPass(String phone) {
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
    public void onResult(boolean succ, String type, BaseResult<LoginData> data) {
        complete();
        if (succ && data == null) {
            CustomizedToast.showShort(App.getAppliction(), App.getContext().getString(R.string.wrong_data));
        } else if (succ) {
            if (Constant.LOGIN_DYNAMIC.equals(type)) { //获取验证码快捷登录单独处理
                if (NetUrl.NO_SUCC.equals(data.no) && data.data != null) {
                    CustomizedToast.showShort(App.getAppliction(), App.getAppliction().getString(R.string.send_success));
                    mV.getDynamicMsg(data.data);
                }
            } else if (data.update && NetUrl.NO_SUCC.equals(data.no)) {
                CustomizedToast.showShort(App.getAppliction(), data.msg);
                if (data.data != null) {
                    if (Constant.LOGIN.equals(type)) { //存普通登录类型
                        SharedPreUtils.setString(Constant.LOGIN_TYPE, type);
                        if (!TextUtils.isEmpty(password)) {
                            data.data.setPassword(password);
                        }
                    } else if (Constant.LOGIN_QQ.equals(type) || Constant.LOGIN_WE_CHAT.equals(type)) { //存三方登录类型
                        SharedPreUtils.setString(Constant.LOGIN_TYPE, type);
                    }
                    data.data.id = 1L;
                    GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().insertOrReplaceInTx(data.data);
                }
                if (mV != null) {
                    mV.updateView(data);
                }
            } else {
                CustomizedToast.showShort(App.getAppliction(), data.msg);
            }
        } else {
            if (data.msg.contains("Failed to connect")) {
                CustomizedToast.showShort(App.getAppliction(), App.getContext().getString(R.string.wrong_net));
            } else if ("login".equals(type)) {
                CustomizedToast.showShort(App.getAppliction(), App.getContext().getString(R.string.wrong_name_or_password));
            } else if ("regist".equals(type)) {
                CustomizedToast.showShort(App.getAppliction(), App.getContext().getString(R.string.wrong_confirm));
            } else {
                CustomizedToast.showShort(App.getAppliction(), data.msg);
            }
        }
    }

    @Override
    public void onSMSResult(boolean succ, String type, BaseResult<String> data) {
        complete();
        if (succ && data == null) {
            CustomizedToast.showShort(App.getAppliction(), App.getContext().getString(R.string.wrong_data));
        } else if (succ) {
            if (NetUrl.NO_SUCC.equals(data.no)) {
                CustomizedToast.showShort(App.getAppliction(), App.getAppliction().getString(R.string.send_success));
                if (Constant.VERIFY.equals(type) && data.data != null) {
                    mV.getDynamicMsg(data.data);
                } else if (Constant.VERIFY_FORGET.equals(type) && !TextUtils.isEmpty(data.sms)) {
                    mV.getDynamicMsg(data.sms);
                } else CustomizedToast.showShort(App.getAppliction(), data.msg);
            } else {
                CustomizedToast.showShort(App.getAppliction(), data.msg);
            }
        } else {
            if (data.msg.contains("Failed to connect")) {
                CustomizedToast.showShort(App.getAppliction(), App.getContext().getString(R.string.wrong_net));
            } else {
                CustomizedToast.showShort(App.getAppliction(), data.msg);
            }
        }
    }

    @Override
    public void loading() { //加载中
        if (mV != null) {
            mV.showLoading(App.getContext().getString(R.string.loading_request));
        }
    }

    @Override
    public void complete() {
        if (mV != null) {
            mV.showLoading(null);
        }
    }
}
