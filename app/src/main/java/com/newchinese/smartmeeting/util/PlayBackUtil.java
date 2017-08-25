package com.newchinese.smartmeeting.util;


import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.log.XLog;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class PlayBackUtil implements Runnable {

    private static final java.lang.String TAG = "PlayBackUtil";
    private ArrayList<NotePoint> lastNotePoints = new ArrayList<>();
    private ArrayList<NotePoint> drawingPointList = new ArrayList<>();
    private DrawingBoardView canvarView;

    private boolean isClose = false;

    //IObtainData为一个接口，因为很多程序在用，因此拿Map存储；
    private int currentPage = 0;
    private boolean isPause = false;

    private PlayBackUtil() {
    }

    //
    private static PlayBackUtil single = null;

    public synchronized static PlayBackUtil getInstance() {
        if (single == null) {
            single = new PlayBackUtil();
        }
        return single;
    }

    /**
     * 重新启动
     *
     * @param notePoints
     */
    public synchronized void addAllNewsBrief(ArrayList<NotePoint> lastNotePoints, ArrayList<NotePoint> notePoints, DrawingBoardView canvarView) {
        XLog.d(TAG,"将集合设置进来："+notePoints.size());
        this.lastNotePoints = lastNotePoints;
        drawingPointList = notePoints;
        this.canvarView = canvarView;
        //有数据要处理时唤醒线程
//            this.notify();
    }

    public synchronized void addAllNewsBrief(DrawingBoardView canvarView) {
        this.canvarView = canvarView;
        //有数据要处理时唤醒线程
        this.notify();
    }

    public void setObtainDataListener(int channelId) {
        //添加回调

    }

    public boolean getIsPaus() {
        return isPause;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
    }

    /**
     * 暂停线程
     */
    public synchronized void onThreadPause() {
        isPause = true;
    }

    /**
     * 线程等待,不提供给外部调用
     */
    private void onThreadWait() {
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 线程继续运行
     */
    public synchronized void onThreadResume() {
        isPause = false;
        this.notify();
    }

    /**
     * 关闭线程
     */
    public synchronized void closeThread() {
        try {
            notify();
            setClose(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean isClose) {
        this.isClose = isClose;
    }

    @Override
    public void run() {
        if (drawingPointList != null && drawingPointList.size() > 0) {
            XLog.d(TAG,"playbackutil中的run方法被调用");
//            for (int i = 0; i < lastNotePoints.size(); i++) {
//                if (lastNotePoints.get(i).getId() == drawingPointList.get(0).getId()) {
//                    canvarView.setIsDown(true);//关闭开线点许可
//
//                    canvarView.setPaintColor(lastNotePoints.get(i).getNoteStroke().getStrokeColor());
//                }
//            }

//            MyLog.e("notePointArrayList。Size()", "-----" + drawingPointList.size());

            canvarView.drawLine(drawingPointList.get(0));

            drawingPointList.remove(drawingPointList.get(0));
        }


    }
}
