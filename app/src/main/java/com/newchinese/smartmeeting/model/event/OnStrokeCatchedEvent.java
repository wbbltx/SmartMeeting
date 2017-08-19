package com.newchinese.smartmeeting.model.event;

import com.newchinese.coolpensdk.entity.NoteStroke;

/**
 * Description:   收到线event
 * author         xulei
 * Date           2017/8/19 13:33
 */
public class OnStrokeCatchedEvent {
    private int fromType;
    private com.newchinese.coolpensdk.entity.NoteStroke noteStroke;

    public OnStrokeCatchedEvent(int fromType, NoteStroke noteStroke) {
        this.fromType = fromType;
        this.noteStroke = noteStroke;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public NoteStroke getNoteStroke() {
        return noteStroke;
    }

    public void setNoteStroke(NoteStroke noteStroke) {
        this.noteStroke = noteStroke;
    }
}
