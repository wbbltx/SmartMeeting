package com.newchinese.smartmeeting.entity.listener;

/**
 * Created by Administrator on 2017/11/15 0015.
 */

public interface ProgressListener {
    /**
     * @param progress     已经下载或上传字节数
     * @param total        总字节数
     * @param done         是否完成
     */
    void onProgress(long progress, long total, boolean done);
}
