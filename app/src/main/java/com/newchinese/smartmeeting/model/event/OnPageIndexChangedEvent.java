package com.newchinese.smartmeeting.model.event;

import com.newchinese.coolpensdk.entity.NotePoint;

/**
 * Description:   换页event
 * author         xulei
 * Date           2017/8/19 13:32
 */
public class OnPageIndexChangedEvent {
    private int fromType;
    private com.newchinese.coolpensdk.entity.NotePoint notePoint;

    public OnPageIndexChangedEvent(int fromType, NotePoint notePoint) {
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
