package com.newchinese.smartmeeting.entity.bean;

/**
 * Created by Administrator on 2017-08-30.
 */

public class FeedBack {
    public String getConnect() {
        return connect;
    }

    public FeedBack setConnect(String connect) {
        this.connect = connect;
        return this;
    }

    public String getFeed_back() {
        return feed_back;
    }

    public FeedBack setFeed_back(String feed_back) {
        this.feed_back = feed_back;
        return this;
    }

    private String connect, feed_back;
}
