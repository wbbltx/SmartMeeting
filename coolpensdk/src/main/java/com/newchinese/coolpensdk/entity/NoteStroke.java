package com.newchinese.coolpensdk.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Description:   线对象，用于数据库存储
 * author         xulei
 * Date           2017/5/5
 */

public class NoteStroke implements Serializable {
    private List<NotePoint> notePointList;
    private int strokeColor; //笔色
    private float strokeWidth; //线宽

    public NoteStroke() {
    }

    public NoteStroke(List<NotePoint> notePointList) {
        this.notePointList = notePointList;
    }

    public NoteStroke(List<NotePoint> notePointList, int strokeColor) {
        this.notePointList = notePointList;
        this.strokeColor = strokeColor;
    }

    public NoteStroke(List<NotePoint> notePointList, int strokeColor, float strokeWidth) {
        this.notePointList = notePointList;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    public List<NotePoint> getNotePointList() {
        return notePointList;
    }

    public void setNotePointList(List<NotePoint> notePointList) {
        this.notePointList = notePointList;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    public String toString() {
        return "NoteStroke{" +
                "notePointList=" + notePointList +
                ", strokeColor=" + strokeColor +
                ", strokeWidth=" + strokeWidth +
                '}';
    }
}
