package com.newchinese.smartmeeting.util;

import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.bean.NoteRecord;

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
    private NoteRecord activeNoteRecord; //当前活动本
    private NotePage activeNotePage; //当前活动页
    private int currentColor = Constant.colors[0]; //笔的颜色
    private int currentColorPosition = 0; //当前选择笔色的index
    private float strokeWidth = 0; //线宽
    private String chosenClassifyName = Constant.CLASSIFY_NAME_OTHER; //选择的分类
    private List<NotePage> activeNotePageList = new ArrayList<>(); //缓存活动和记录表中当前所有页
    private String picSDCardDirectory = "";
    private int penState = 100;//记录笔的状态
    private boolean isRecording;    //记录是否处于录制状态
    private Set<Integer> pages = new HashSet<>();       //记录视频录制期间翻过的页

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

    public void addPages(int i){
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

    public String getPicSDCardDirectory() {
        return picSDCardDirectory;
    }

    public void setPicSDCardDirectory(String picSDCardDirectory) {
        this.picSDCardDirectory = picSDCardDirectory;
    }
}
