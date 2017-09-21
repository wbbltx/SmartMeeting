package com.newchinese.smartmeeting.entity.bean;

import com.newchinese.smartmeeting.entity.http.IModel;

/**
 * Created by Administrator on 2017-08-24.
 */

public class BaseResult<D> implements IModel {

    public String url, no, msg, verifyCode, sms;
    public boolean update;//标记用户信息更新
    public D data;

    @Override
    public boolean isNull() {
        return no == null;
    }

    @Override
    public boolean isAuthError() {
        return !"100000".equals(no);
    }

    @Override
    public boolean isBizError() {
        return false;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "url='" + url + '\'' +
                ", no='" + no + '\'' +
                ", msg='" + msg + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                ", sms='" + sms + '\'' +
                ", update=" + update +
                ", data=" + data +
                '}';
    }
}
