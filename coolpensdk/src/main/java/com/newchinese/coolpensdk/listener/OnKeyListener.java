package com.newchinese.coolpensdk.listener;

/**
 * @anthor wubinbin
 * @time 2017/4/25 16:14
 */

public interface OnKeyListener {

    /**
     * 保存生成的key
     * @param key
     */
    void onKeyGenerated(String key);

    void onSetLocalKey();
}
