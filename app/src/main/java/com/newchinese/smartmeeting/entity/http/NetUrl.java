package com.newchinese.smartmeeting.entity.http;

/**
 * Created by Administrator on 2017-08-25.
 */

public interface NetUrl {
    String NO_SUCC = "100000";
    
    String HOST = "http://newsso.coolpen.net"; //正式服
    //    String HOST = "http://182.92.99.12:8083"; //测试服
    //    String HOST = "http://182.92.99.12:9005"; //个人测试服
    String TEST = "http://192.168.1.65:8080";

    String LOGIN_NORMAL = HOST + "/user/m/login"; //普通登录
    String DYNAMIC_PASS = HOST + "/user/m/sms_fastLogin"; //快捷登录
    String LOGIN_QQ = HOST + "/user/getQqUserInfo"; //QQ登录
    String LOGIN_WE_CHAT = HOST + "/user/getWechatUserInfo"; //微信登录
    String REGIST = HOST + "/user/m/register"; //注册
    String VERIFY_CODE = HOST + "/user/m/rsms"; //注册获取验证码
    String VERIFY_FORGET_CODE = HOST + "/user/m/sms"; //忘记密码获取验证码
    String FORGET_PASS = HOST + "/user/m/reset"; //忘记密码
    String UPDATE_PASS = HOST + "/user/m/pass"; //修改密码
    String UPDATE_NICK = HOST + "/user/m/update"; //更新用户昵称
    String UPDATE_ICON = HOST + "/user/m/update/icon"; //更新用户头像
    String FEED_BACK = HOST + "/user/m/feed_back"; //反馈
}
