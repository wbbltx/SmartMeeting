package com.newchinese.smartmeeting.app;

import android.graphics.Color;

import com.newchinese.smartmeeting.R;

/**
 * Description:
 * author         xulei
 * Date           2017/8/19
 */

public class Constant {
    public static int[] colors = {Color.parseColor("#000000"), Color.parseColor("#FF4A4A"),
            Color.parseColor("#FF8534"), Color.parseColor("#F3F300"), Color.parseColor("#00E704"),
            Color.parseColor("#C453E0"), Color.parseColor("#ffffff")}; //笔色集合
    public static final int[] classifyPics = {R.mipmap.classify_work, R.mipmap.classify_project,
            R.mipmap.classify_explore, R.mipmap.classify_study, R.mipmap.classify_report,
            R.mipmap.classify_review, R.mipmap.classify_add, R.mipmap.classify_other}; //会议分类图标
    //各分类记录表名称
    public static final String CLASSIFY_NAME_WORK = "工作例会";
    public static final String CLASSIFY_NAME_PROJECT = "项目会议";
    public static final String CLASSIFY_NAME_STUDY = "学习培训";
    public static final String CLASSIFY_NAME_EXPLORE = "研讨会";
    public static final String CLASSIFY_NAME_REPORT = "工作汇报";
    public static final String CLASSIFY_NAME_REVIEW = "评审会";
    public static final String CLASSIFY_NAME_OTHER = "其他";
    //各分类记录表缩略图SD卡存储一级目录
    public static final String SD_DIRECTORY_BASE = "SmartMeeting";
    //各分类记录表缩略图SD卡存储二级目录
    public static final String SD_DIRECTORY_IMAGE = "Images";
    //各分类记录表缩略图SD卡存储三级目录
    public static final String SD_DIRECTORY_WORK = "WorkRecords";
    public static final String SD_DIRECTORY_PROJECT = "ProjectRecords";
    public static final String SD_DIRECTORY_STUDY = "StudyRecords";
    public static final String SD_DIRECTORY_EXPLORE = "ExploreRecords";
    public static final String SD_DIRECTORY_REPORT = "ReportRecords";
    public static final String SD_DIRECTORY_REVIEW = "ReviewRecords";
    public static final String SD_DIRECTORY_OTHER = "OtherRecords";
    public static final String[] SD_DIRECTORY_RECORD_LIST = {SD_DIRECTORY_WORK, SD_DIRECTORY_PROJECT,
            SD_DIRECTORY_STUDY, SD_DIRECTORY_EXPLORE, SD_DIRECTORY_REPORT, SD_DIRECTORY_REVIEW,
            SD_DIRECTORY_OTHER}; //记录目录集合
    //存当天日期的tag
    public static final String DAY_NUM = "day_num";
}
