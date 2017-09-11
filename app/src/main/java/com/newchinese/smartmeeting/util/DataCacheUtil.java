package com.newchinese.smartmeeting.util;

import android.bluetooth.BluetoothDevice;

import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description:   数据缓存工具类
 * author         xulei
 * Date           2017/8/19
 */

public class DataCacheUtil {
    private int penState = BluCommonUtils.PEN_DISCONNECTED;//记录笔的状态 默认断开连接
    private int currentColor = Constant.colors[0]; //笔的颜色
    private int currentColorPosition = 0; //当前选择笔色的index
    private float strokeWidth = 0; //线宽
    private boolean isRecording;    //记录是否处于录制状态
    private String picSDCardDirectory = "";
    private String chosenClassifyName = Constant.CLASSIFY_NAME_OTHER; //选择的分类
    private Set<Integer> pages = new HashSet<>();       //记录视频录制期间翻过的页
    private List<NotePage> activeNotePageList = new ArrayList<>(); //缓存活动记录表中当前所有页
    private List<CollectPage> activeCollectPageList = new ArrayList<>(); //缓存活动记录表中当前所有收藏页
    private NotePage activeNotePage; //当前活动页
    private NoteRecord activeNoteRecord; //当前活动记录
    private CollectRecord activeCollectRecord; //当前活动收藏记录
    private int progressMax;//笔记回放max
    private ArrayList<com.newchinese.coolpensdk.entity.NotePoint> playBackList;//笔记回放点的缓存
    private List<String> recordPathList;  //当页的录屏文件路径集合
    private String thumbPath;      //最近的缩略图的路径
    private BluetoothDevice device;//保存蓝牙的临时变量
    private boolean isFirstTime = true; //b草稿箱界面是否需要初始化蓝牙标志

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public List<String> getRecordPathList() {
        return recordPathList;
    }

    public void setRecordPathList(List<String> recordPathList) {
        this.recordPathList = recordPathList;
    }

    public ArrayList<NotePoint> getPlayBackList() {
        return playBackList;
    }

    public void setPlayBackList(ArrayList<NotePoint> playBackList) {
        this.playBackList = playBackList;
    }

    public int getProgressMax() {
        return progressMax;
    }

    public void setProgressMax(int progressMax) {
        this.progressMax = progressMax;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public Set<Integer> getPages() {
        return pages;
    }

    public void setPages(Set<Integer> pages) {
        this.pages = pages;
    }

    public void addPages(int i) {
        pages.add(i);
    }

    public int getPenState() {
        return penState;
    }

    public void setPenState(int penState) {
        this.penState = penState;
    }

    private static class SingleHolder {
        private static final DataCacheUtil INSTANCE = new DataCacheUtil();
    }

    private DataCacheUtil() {
    }

    public static DataCacheUtil getInstance() {
        return SingleHolder.INSTANCE;
    }

    public NoteRecord getActiveNoteRecord() {
        return activeNoteRecord;
    }

    public void setActiveNoteRecord(NoteRecord activeNoteRecord) {
        this.activeNoteRecord = activeNoteRecord;
    }

    public CollectRecord getActiveCollectRecord() {
        return activeCollectRecord;
    }

    public void setActiveCollectRecord(CollectRecord activeCollectRecord) {
        this.activeCollectRecord = activeCollectRecord;
    }

    public NotePage getActiveNotePage() {
        return activeNotePage;
    }

    public void setActiveNotePage(NotePage activeNotePage) {
        this.activeNotePage = activeNotePage;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public int getCurrentColorPosition() {
        return currentColorPosition;
    }

    public void setCurrentColorPosition(int currentColorPosition) {
        this.currentColorPosition = currentColorPosition;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getChosenClassifyName() {
        return chosenClassifyName;
    }

    public void setChosenClassifyName(String chosenClassifyName) {
        this.chosenClassifyName = chosenClassifyName;
    }

    public List<NotePage> getActiveNotePageList() {
        return activeNotePageList;
    }

    public void setActiveNotePageList(List<NotePage> activeNotePageList) {
        this.activeNotePageList = activeNotePageList;
    }

    public void clearActiveNotePageList() {
        activeNotePageList.clear();
    }

    public List<CollectPage> getActiveCollectPageList() {
        return activeCollectPageList;
    }

    public void setActiveCollectPageList(List<CollectPage> collectPageList) {
        this.activeCollectPageList = collectPageList;
    }

    public void clearActiveCollectPageList() {
        activeCollectPageList.clear();
    }

    public String getPicSDCardDirectory() {
        return picSDCardDirectory;
    }

    public void setPicSDCardDirectory(String picSDCardDirectory) {
        this.picSDCardDirectory = picSDCardDirectory;
    }
}
