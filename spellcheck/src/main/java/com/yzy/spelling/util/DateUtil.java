package com.yzy.spelling.util;

import java.util.Calendar;
import java.util.Date;


public class DateUtil {
	/**
	 * 计算两日期相隔天数
	 * @param date1
	 * @param date2
	 * @return int days
	 */
	public static long getIntervalDays(Date date1, Date date2){
		long days = 0;
        Calendar cal = Calendar.getInstance();
        // date1的 时，分，秒，微秒置0
        cal.setTime(date1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date1 = cal.getTime();
        // date2的 时，分，秒，微秒置0
        cal.setTime(date2);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date2 = cal.getTime();
        
        long interval = Math.abs(date1.getTime() - date2.getTime());
        long dateMillSec = 24 * 60 * 60 * 1000;
        days =  interval / dateMillSec;
		return days;
	}
	
	/**
	 * 以baseDate为标准，返回n天后的date
	 * 
	 * @author chow 2010-7-22
	 */
	public static Date getDateAfterNDays(int days, Date baseDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseDate);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}
}
