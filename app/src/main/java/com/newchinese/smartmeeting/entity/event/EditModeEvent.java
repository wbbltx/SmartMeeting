package com.newchinese.smartmeeting.entity.event;

/**
 * Created by Administrator on 2017/11/22 0022.
 */

public class EditModeEvent {

    public static final String CANCEL = "cancel";
    public static final String ALLSELECE = "allselece";
    public static final String NONESELECT = "noneselect";
    public static final String EDITMODE = "editmode";
    public static final String EXITEDITMODE = "exiteditmode";

    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public EditModeEvent(String mode) {

        this.mode = mode;
    }
}
