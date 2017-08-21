package com.newchinese.smartmeeting.util;

import com.newchinese.coolpensdk.entity.NotePoint;

import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description:   第一笔缓存Util
 * author         xulei
 * Date           2017/8/19
 */

public class PointCacheUtil {
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<NotePoint> queueArray = new ConcurrentLinkedQueue<>();

    private boolean canAddFlag = true;//表示开启第一笔缓存功能

    public static PointCacheUtil getInstance() {
        return PointCacheUtil.PointCacheUtilInstance.instance;
    }

    public void putInQueue(String src) {
        queue.add(src);
    }

    public void putInQueue(NotePoint info) {
        if (canAddFlag) {
            queueArray.add(info);
        }
    }

    public void putAllInQueue(String[] src) {
        Collections.addAll(queue, src);
    }

    public String getAndRemoveInQueue() {
        return queue.poll();
    }

    public String[] getAll() {
        canAddFlag = false;
        String[] strs = new String[queue.size()];
        queue.toArray(strs);
        return strs;
    }

    public NotePoint[] getArrayAll() {
        canAddFlag = false;
        NotePoint[] arrayList = new NotePoint[queueArray.size()];
        queueArray.toArray(arrayList);
        return arrayList;
    }

    public boolean isCanAddFlag() {
        return canAddFlag;
    }

    public void setCanAddFlag(boolean canAddFlag) {
        this.canAddFlag = canAddFlag;
    }

    public void clearQueue() {
        queue.clear();
        queueArray.clear();
    }

    private static class PointCacheUtilInstance {
        private static final PointCacheUtil instance = new PointCacheUtil();
    }
}
