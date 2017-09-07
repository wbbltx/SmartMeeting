package com.newchinese.smartmeeting.entity.event;

import com.newchinese.coolpensdk.entity.NotePoint;

/**
 * Description:   收到点event
 * author         xulei
 * Date           2017/7/28 11:42
 */
public class OnPointCatchedEvent {
    private int fromType;
    private com.newchinese.coolpensdk.entity.NotePoint notePoint;

    public OnPointCatchedEvent(int fromType, NotePoint notePoint) {
        this.fromType = fromType;
        this.notePoint = notePoint;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public NotePoint getNotePoint() {
        return notePoint;
    }

    public void setNotePoint(NotePoint notePoint) {
        this.notePoint = notePoint;
    }
}
