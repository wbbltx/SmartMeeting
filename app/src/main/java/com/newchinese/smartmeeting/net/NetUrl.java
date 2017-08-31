package com.newchinese.smartmeeting.net;

/**
 * Created by Administrator on 2017-08-25.
 */

public interface NetUrl {

    String NO_SUCC = "100000";

    String HOST = "http://182.92.99.12:8083";

    String QUICK_LOGIN = HOST + "";
    String DYNAMIC_PASS = HOST + "";

    String LOGIN_NORMAL = HOST + "/user/m/login";
    String REGIST = HOST + "/user/m/register";
    String VERIFY_CODE = HOST + "/user/m/sms";
    String FORGET_PASS = HOST + "/user/m/reset";
    String UPDATE_NICK = HOST + "/user/m/update";
    String UPDATE_ICON = HOST + "/user/m/update/icon";
    String FEED_BACK = HOST + "/user/m/feed_back";
    String UPDATE_PASS = HOST + "/user/m/pass";

}
