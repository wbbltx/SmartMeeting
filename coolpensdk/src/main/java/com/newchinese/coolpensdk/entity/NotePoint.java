package com.newchinese.coolpensdk.entity;

/**
 * Description:   点的bean
 * author         xulei
 * Date           2017/4/25 11:00
 */
public class NotePoint implements java.io.Serializable {

    private Float pX;
    private Float pY;
    private Float testTime;
    private Float firstPress;
    private Float press;
    private Integer pageIndex;
    private Integer pointType; //down:1 move:2 up:3
    private long id;//对应的笔画stroke id

    public NotePoint() {
    }

    public NotePoint(Float pX, Float pY, Float testTime, Float firstPress, Float press, Integer pageIndex, Integer pointType) {
        this.pX = pX;
        this.pY = pY;
        this.testTime = testTime;
        this.firstPress = firstPress;
        this.press = press;
        this.pageIndex = pageIndex;
        this.pointType = pointType;
    }

    public NotePoint(Float pX, Float pY, Float testTime, Float firstPress, Float press, Integer pageIndex, Integer pointType, long id) {
        this.pX = pX;
        this.pY = pY;
        this.testTime = testTime;
        this.firstPress = firstPress;
        this.press = press;
        this.pageIndex = pageIndex;
        this.pointType = pointType;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Float getPX() {
        return pX;
    }

    public void setPX(Float pX) {
        this.pX = pX;
    }

    public Float getPY() {
        return pY;
    }

    public void setPY(Float pY) {
        this.pY = pY;
    }

    public Float getTestTime() {
        return testTime;
    }

    public void setTestTime(Float testTime) {
        this.testTime = testTime;
    }

    public Float getFirstPress() {
        return firstPress;
    }

    public void setFirstPress(Float firstPress) {
        this.firstPress = firstPress;
    }

    public Float getPress() {
        return press;
    }

    public void setPress(Float press) {
        this.press = press;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPointType() {
        return pointType;
    }

    public void setPointType(Integer pointType) {
        this.pointType = pointType;
    }

    @Override
    public String toString() {
        return "NotePoint{" +
                "pX=" + pX +
                ", pY=" + pY +
                ", testTime=" + testTime +
                ", firstPress=" + firstPress +
                ", press=" + press +
                ", pageIndex=" + pageIndex +
                ", pointType=" + pointType +
                '}';
    }
}
