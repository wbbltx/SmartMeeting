package com.newchinese.smartmeeting.app;

import android.graphics.Color;

import com.newchinese.smartmeeting.R;

/**
 * Description:
 * author         xulei
 * Date           2017/8/19
 */

public class Constant {
    public static final int[] colors = {Color.parseColor("#000000"), Color.parseColor("#FF4A4A"),
            Color.parseColor("#FF8534"), Color.parseColor("#F3F300"), Color.parseColor("#00E704"),
            Color.parseColor("#FF00FF"), Color.parseColor("#3366FF")}; //笔颜色
    public static final int[] classifyPics = {R.mipmap.classify_work, R.mipmap.classify_project,
            R.mipmap.classify_explore, R.mipmap.classify_study, R.mipmap.classify_report,
            R.mipmap.classify_review, R.mipmap.classify_add, R.mipmap.classify_other};
    public static final String CLASSIFY_NAME_WORK = "工作例会";
    public static final String CLASSIFY_NAME_PROJECT = "项目会议";
    public static final String CLASSIFY_NAME_STUDY = "学习培训";
    public static final String CLASSIFY_NAME_EXPLORE = "研讨会";
    public static final String CLASSIFY_NAME_REPORT = "工作汇报";
    public static final String CLASSIFY_NAME_REVIEW = "评审会";
    public static final String CLASSIFY_NAME_OTHER = "其他";
}
