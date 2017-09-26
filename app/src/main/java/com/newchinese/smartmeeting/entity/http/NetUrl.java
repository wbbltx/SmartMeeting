package com.newchinese.smartmeeting.entity.http;

/**
 * Created by Administrator on 2017-08-25.
 */

public interface NetUrl {
    String NO_SUCC = "100000";

    //    String HOST = "http://182.92.99.12:8083";
    String HOST = "http://182.92.99.12:9005";
    String TEST = "http://192.168.1.52:8080";

    String DYNAMIC_PASS = HOST + "/user/m/sms_fastLogin";

    String LOGIN_NORMAL = HOST + "/user/m/login";
    String REGIST = HOST + "/user/m/register";
    String VERIFY_CODE = HOST + "/user/m/rsms";
    String FORGET_PASS = HOST + "/user/m/reset";
    String UPDATE_NICK = HOST + "/user/m/update";
    String UPDATE_ICON = HOST + "/user/m/update/icon";
    String FEED_BACK = HOST + "/user/m/feed_back";
    String UPDATE_PASS = HOST + "/user/m/pass";
    String LOGIN_QQ = HOST + "/user/getQqUserInfo";
    String LOGIN_WE_CHAT = HOST + "/user/getWechatUserInfo";
}
