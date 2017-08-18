package com.newchinese.coolpensdk.manager;

import android.graphics.Bitmap;

import com.newchinese.coolpensdk.entity.NotePoint;

/**
 * Description:   逻辑控制器
 * author         xulei
 * Date           2017/3/23
 */

class LogicController {
    //用户唯一标识
    private String appKey;
    //是否允许使用sdk状态
    private boolean approved = false;
    //是否是第一个点
    private boolean isFirstpoint = true;
    //用于判断页数是否相同的点
    private NotePoint cachePoint;
    //前一个点的缓存    
    private NotePoint previousPoint;
    private Bitmap bgBitmap;
    private Bitmap foreGroundBitmap;
    private int width = -1;
    private int height = -1;
    private float offsetX = 0; // 横坐标偏移量
    private float offsetY = 0; // 纵坐标偏移量
    private float baseXOffset = 0; // 原点X偏移量
    private float baseYOffset = 0; // 原点Y偏移量

    private static class LogicControllerInstance {
        private static final LogicController instance = new LogicController();
    }

    static LogicController getInstance() {
        return LogicControllerInstance.instance;
    }

    boolean getIsFirstpoint() {
        return isFirstpoint;
    }

    void setIsFirstpoint(boolean isFirstpoint) {
        this.isFirstpoint = isFirstpoint;
    }

    NotePoint getPreviousPoint() {
        return previousPoint;
    }

    void setPreviousPoint(NotePoint previousPoint) {
        this.previousPoint = previousPoint;
    }

    Bitmap getBgBitmap() {
        return bgBitmap;
    }

    void setBgBitmap(Bitmap bgBitmap) {
        this.bgBitmap = bgBitmap;
    }

    Bitmap getForeGroundBitmap() {
        return foreGroundBitmap;
    }

    void setForeGroundBitmap(Bitmap foreGroundBitmap) {
        this.foreGroundBitmap = foreGroundBitmap;
    }

    int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }

    float getOffsetX() {
        return offsetX;
    }

    void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    float getOffsetY() {
        return offsetY;
    }

    void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    private NotePoint getCachePoint() {
        return cachePoint;
    }

    void setCachePoint(NotePoint cachePoint) {
        this.cachePoint = cachePoint;
    }

    String getAppKey() {
        return appKey;
    }

    void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    boolean isApproved() {
        return approved;
    }

    void setApproved(boolean approved) {
        this.approved = approved;
    }

    float getBaseXOffset() {
        return baseXOffset;
    }

    void setBaseXOffset(float baseXOffset) {
        this.baseXOffset = baseXOffset;
    }

    float getBaseYOffset() {
        return baseYOffset;
    }

    void setBaseYOffset(float baseYOffset) {
        this.baseYOffset = baseYOffset;
    }

    /**
     * 初始化本宽高
     */
    void setBookInfo() {
        setWidth(-1);
        setHeight(-1);
    }

    /**
     * 判断当前传来的点与保存的前一点是否是同一页的点
     *
     * @param point 在接收到down点时传入NotePoint
     */
    boolean isSameNotePage(NotePoint point) {
        int currentPageindex = point.getPageIndex();
        NotePoint cachePoint = getCachePoint();
        if (cachePoint == null) {
            return false;
        }
        int previousPageIndex = cachePoint.getPageIndex();
        return currentPageindex == previousPageIndex;
    }
}

