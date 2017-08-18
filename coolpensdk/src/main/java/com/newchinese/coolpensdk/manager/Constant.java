package com.newchinese.coolpensdk.manager;

/**
 * Description:   常量
 * author         xulei
 * Date           2017/4/25 11:01
 */
class Constant {
    static final int POINT_MAX_X = 16000;                    //坐标系X轴最大值
    static final int POINT_MAX_Y = 16000;                    //坐标系Y轴最大值
    static final int DEFAULT_STROKE_COLOR = 0x000000;        //默认线的颜色
    static final float DEFAULT_STROKE_WIDTH = 3.0f;          //默认线的宽度
    static final float DEFAULT_STROKE_PRESS = 1.0f;          //默认的压力
    static final int DEFAULT_STROKE_TRANS = 0xff000000;      //默认线的透明度
    static final String AXIS_HEAD_FLAG = "60";               //报文头
    //下面这两个值（ACTIVE_PAGE_X、ACTIVE_PAGE_Y）决定了纸张的大小和适配规则
    static float ACTIVE_PAGE_X = 109.0f; //默认一页的X坐标，由LogicalController的setActiveNoteBook直接决定，由BookManager里的initA4NoteBook提供源数据
    static float ACTIVE_PAGE_Y = 153.0f; //默认一页的Y坐标
    //下面这两个值（AXIS_NUM_X*AXIS_NUM_Y）决定了我们能写出哪些页【跳页和这没有关系】
    static int AXIS_NUM_X = (int) Math.floor(Constant.POINT_MAX_X / Constant.ACTIVE_PAGE_X);//X方向上能谱多少张纸,需要自己算
    static int AXIS_NUM_Y = (int) Math.floor(Constant.POINT_MAX_Y / Constant.ACTIVE_PAGE_Y);//Y方向上能铺多少张纸，需要自己算

//    static final String CONFIRM_URL = "http://192.168.1.34:8080"; //个人测试
    static final String CONFIRM_URL = "http://182.92.99.12:8091"; //测试
}
