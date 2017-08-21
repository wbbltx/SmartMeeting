package com.newchinese.smartmeeting.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

    public static String formatLongDate(long longDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd-HH:mm");
        return sdf.format(longDate);
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param available_time 时间参数 1 格式：1990-01-01 12:00:00
     * @param type           时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String available_time, String type) {
        DateFormat df;
        if (type == "time") {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            df = new SimpleDateFormat("yyyy-MM-dd");
        }
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff = 0;
        try {
            one = new Date();
            two = df.parse(available_time);
            long time1 = one.getTime();
            long time2 = two.getTime();

            if (time1 < time2) {
                diff = time2 - time1;
            } else {
//				diff = time1 - time2;
                long[] times = {0};
                return times;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
//			e.printStackTrace();
        }
        long interval = diff;
        long[] times = {interval, day, hour, min, sec};
        return times;
    }

    /**
     * 根据time获取 时分秒
     *
     * @param time
     * @return
     */
    public static long[] getHourMinSecTimes(Long time) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            day = time / (24 * 60 * 60 * 1000);
            hour = (time / (60 * 60 * 1000) - day * 24);
            min = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
        }
        long[] times = {day, hour, min, sec};
        return times;
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy年MM月dd日 HH:mm");
    }

    public static String getCurrentTime1() {
        return getCurrentTime("yyyy-MM-dd");
    }


    /**
     * 根据String时间获取年份
     *
     * @param startTime
     * @return
     */
    public static int getYear(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 根据String时间获取月份
     *
     * @param startTime
     * @return
     */
    public static int getMonth(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据String时间获取日
     *
     * @param startTime
     * @return
     */
    public static int getDay(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据String时间获取周
     *
     * @param startTime
     * @return
     */
    public static String getWeekByDay(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String weekStr = "";
        switch (calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
            case 1:
                weekStr = "一";
                break;
            case 2:
                weekStr = "二";
                break;
            case 3:
                weekStr = "三";
                break;
            case 4:
                weekStr = "四";
                break;
        }
        return weekStr;
    }

    /**
     * 将时间戳转换成时间
     */
    public static String timestampToDate(String beginDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(new Date(Long.parseLong(beginDate)));
        return sd;
    }

    /**
     * 将时间戳转换成时间
     */
    public static String timestampToDate1(String beginDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        String sd = sdf.format(new Date(Long.parseLong(beginDate)));
        return sd;
    }

    /**
     * 将时间戳转换成时间
     */
    public static String timestampToDate2(String beginDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM.dd-HH:mm");
        String sd = sdf.format(new Date(Long.parseLong(beginDate)));
        return sd;
    }


    //获取当前时间
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    /*
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {

            if (milliSecond > 0) {
                second++;
            }
            sb.append((second) + "秒");
        } else {
            if (milliSecond > 0) {
                second++;
            }
            sb.append((second) + "秒");
        }
//		if(milliSecond > 0) {
//			sb.append(milliSecond+"毫秒");
//		}
        return sb.toString();
    }
}
