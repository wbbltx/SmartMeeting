package com.newchinese.coolpensdk.manager;


import com.newchinese.coolpensdk.entity.NotePoint;

import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description:   DrawingBoardView初始化完成之前，需要缓存从蓝牙笔中传递过来的第一笔数据
 * author         xulei
 * Date           2017/4/25 10:29
 */
class FirstStrokeCache {
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<NotePoint> queueArray = new ConcurrentLinkedQueue<>();

    private boolean canAddFlag = true;//表示开启第一笔缓存功能

    public static FirstStrokeCache getInstance() {
        return FirstStrokeCacheInstance.instance;
    }

    public void putInQueue(String src) {
        queue.add(src);
    }

    public void putInQueue(NotePoint info) {
        if (canAddFlag) { // TODO: 2017/4/26 && !LogicController.getInstance().isBlePairing()
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

    private static class FirstStrokeCacheInstance {
        private static final FirstStrokeCache instance = new FirstStrokeCache();
    }

}
