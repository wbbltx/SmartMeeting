package com.newchinese.smartmeeting.net;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017-08-25.
 */

public class NetProviderImpl implements NetProvider {

    @Override
    public Interceptor[] configInterceptors() {
        return new Interceptor[0];
    }

    @Override
    public void configHttps(OkHttpClient.Builder builder) {

    }

    @Override
    public CookieJar configCookie() {
        return null;
    }

    @Override
    public RequestHandler configHandler() {
        return new RequestHandler() {
            @Override
            public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
                return request.newBuilder()
                        .header("User-Agent", "SmartMeeting")
                        .build();
            }

            @Override
            public Response onAfterRequest(Response response, Interceptor.Chain chain) {
                return null;
            }
        };
    }

    @Override
    public long configConnectTimeoutMills() {
        return 0;
    }

    @Override
    public long configReadTimeoutMills() {
        return 0;
    }

    @Override
    public boolean configLogEnable() {
        return true;
    }

    @Override
    public boolean handleError(NetError error) {
        return false;
    }
}
