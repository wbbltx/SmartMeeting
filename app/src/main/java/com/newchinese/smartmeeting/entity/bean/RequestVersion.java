package com.newchinese.smartmeeting.entity.bean;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class RequestVersion {
    private String platform;
    private String version;


    public String getPlatform() {
        return platform;
    }

    public RequestVersion setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public RequestVersion setVersion(String version) {
        this.version = version;
        return this;
    }
}
