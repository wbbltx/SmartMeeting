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


//    String REGIST = "http://www.mocky.io/v2/59a12fcc110000b20d644326";
//    String VERIFY_CODE = "http://www.mocky.io/v2/59a1305b110000ac0d644327";
//    String FORGET_PASS = "http://www.mocky.io/v2/59a13083110000960d644328";
//    String QUICK_LOGIN = "http://www.mocky.io/v2/59a130c0110000ac0d644329";
//    String DYNAMIC_PASS = "http://www.mocky.io/v2/59a1305b110000ac0d644327";
//    String UPDATE_NICK = "http://www.mocky.io/v2/59a1311c110000d00d64432a";
//    String LOGIN_NORMAL = "http://www.mocky.io/v2/59a12573110000080d64431a";
}
