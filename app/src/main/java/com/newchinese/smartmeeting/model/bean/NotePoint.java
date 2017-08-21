package com.newchinese.smartmeeting.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Description:   点表
 * author         xulei
 * Date           2017/8/18 17:22
 */
@Entity
public class NotePoint {
    @Id(autoincrement = true)
    private Long id;
    private long strokeId;
    private float pX;
    private float pY;
    private float press;
    private float firstPress;
    private float testTime;
    private int pageIndex;
    private int pointType; //down:1 move:2 up:3
    @Generated(hash = 1047354121)
    public NotePoint(Long id, long strokeId, float pX, float pY, float press,
            float firstPress, float testTime, int pageIndex, int pointType) {
        this.id = id;
        this.strokeId = strokeId;
        this.pX = pX;
        this.pY = pY;
        this.press = press;
        this.firstPress = firstPress;
        this.testTime = testTime;
        this.pageIndex = pageIndex;
        this.pointType = pointType;
    }
    @Generated(hash = 1614831008)
    public NotePoint() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getStrokeId() {
        return this.strokeId;
    }
    public void setStrokeId(long strokeId) {
        this.strokeId = strokeId;
    }
    public float getPX() {
        return this.pX;
    }
    public void setPX(float pX) {
        this.pX = pX;
    }
    public float getPY() {
        return this.pY;
    }
    public void setPY(float pY) {
        this.pY = pY;
    }
    public float getPress() {
        return this.press;
    }
    public void setPress(float press) {
        this.press = press;
    }
    public float getFirstPress() {
        return this.firstPress;
    }
    public void setFirstPress(float firstPress) {
        this.firstPress = firstPress;
    }
    public float getTestTime() {
        return this.testTime;
    }
    public void setTestTime(float testTime) {
        this.testTime = testTime;
    }
    public int getPageIndex() {
        return this.pageIndex;
    }
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
    public int getPointType() {
        return this.pointType;
    }
    public void setPointType(int pointType) {
        this.pointType = pointType;
    }
}
