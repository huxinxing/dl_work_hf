package com.bcb.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    //获取查询的凌晨时间
    public static Date DateZero(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //获取查询时间的23:59:59
    public static Date DateTweentyFour(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    //获取当前时间多少天之后/多少天之前时间 0 为当前时间，past为负数时为前几天， past为正数的时候为后几天
    public String StrTime(int past,String Timeformate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(Timeformate);
        String result = format.format(today);
        return result;
    }

    // 两者时间之间相差多少天,已24小时为界，低于24小时不算做一天时间
    public static Integer surplusDay(Date beginTime, Date endTime) {
        double dateTime = (endTime.getTime() - beginTime.getTime());
        double days =  Math.ceil ( (dateTime / (1000*3600*24)));
        if(dateTime%(1000*3600*24) == 0){
            return (int)days;
        }
        return  (int)days - 1 ;
    }

    // 时间转化为相应的字符串类型
    public static String DateToStr(Date date, String Timeformate) {
        if (date == null) {
            return "-";
        }
        SimpleDateFormat simpleDateFormate = new SimpleDateFormat(Timeformate);
        return simpleDateFormate.format(date);
    }

    // 获取当前时间多少天之后/多少天之前 00:00:00 时间 0 为当前时间，past为负数时为前几天， past为正数的时候为后几天
    public static Date TimeAfterPath(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
