package com.newchinese.smartmeeting.entity.event;

import com.newchinese.smartmeeting.entity.listener.PopWindowListener;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class HisInfoEvent {

    PopWindowListener listener;

    public HisInfoEvent(PopWindowListener listener) {
        this.listener = listener;
    }

    public PopWindowListener getListener() {
        return listener;
    }

    public void setListener(PopWindowListener listener) {
        this.listener = listener;
    }
}
