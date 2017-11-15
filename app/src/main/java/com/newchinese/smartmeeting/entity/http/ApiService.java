package com.newchinese.smartmeeting.entity.http;

import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.FeedBack;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.bean.RequestVersion;
import com.newchinese.smartmeeting.entity.bean.VersionInfo;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017-08-25.
 */

public interface ApiService {

    //登录
    @POST(NetUrl.LOGIN_NORMAL)
    Flowable<BaseResult<LoginData>> login(@Body LoginData data);

    //注册
    @POST(NetUrl.REGIST)
    Flowable<BaseResult<LoginData>> regist(@Body LoginData data);

    //忘记密码
    @POST(NetUrl.FORGET_PASS)
    Flowable<BaseResult<LoginData>> forget(@Body LoginData data);

    //动态密码
    @POST(NetUrl.DYNAMIC_PASS)
    Flowable<BaseResult<LoginData>> dynamic(@Body LoginData data);

    //验证码
    @POST(NetUrl.VERIFY_CODE)
    Flowable<BaseResult<String>> verify(@Body LoginData data);

    //忘记密码验证码
    @POST(NetUrl.VERIFY_FORGET_CODE)
    Flowable<BaseResult<String>> verifyForget(@Body LoginData data);

    //更新资料
    @POST(NetUrl.UPDATE_NICK)
    Flowable<BaseResult<LoginData>> updateNick(@Body LoginData data);

    //更新头像
    @POST(NetUrl.UPDATE_ICON)
    Flowable<BaseResult<String>> updateIcon(@Body LoginData data);

    //一键反馈
    @POST(NetUrl.FEED_BACK)
    Flowable<BaseResult<LoginData>> feedBack(@Body FeedBack data);

    @POST(NetUrl.UPDATE_PASS)
    Flowable<BaseResult<LoginData>> updatePass(@Body LoginData data);

    //QQ登录
    @POST(NetUrl.LOGIN_QQ)
    Flowable<BaseResult<LoginData>> loginQQ(@Body LoginData data);

    //微信登录
    @POST(NetUrl.LOGIN_WE_CHAT)
    Flowable<BaseResult<LoginData>> loginWeChat(@Body LoginData data);

    //版本查询
    @POST(NetUrl.VERSION_INFO)
    Flowable<BaseResult<VersionInfo>> checkVersion(@Body RequestVersion requestVersion);

    //下载新版本
    @GET("http://182.92.99.12:8080/images/M00/00/69/tlxjDFoKsXeAfFkuAIANTG_1BTI289.apk")
    Call<ResponseBody> loadApk();
}
