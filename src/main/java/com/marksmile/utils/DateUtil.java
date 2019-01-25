package com.marksmile.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author HuangFeng(2010-11-2)
 */
public class DateUtil {

    public static final String YYYY = "yyyy";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";

    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHH = "yyyyMMddHH";
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSS = "yyyyMMddHHmmssS";

    /**
     * Method：时间格式化，一般在页面上显示比较多，用户可以自定义格式化格式<br>
     * Remark：把数据库里的DATE格式转换成HTML页面需要的时间字符串格式：
     * <code>DateUtil.format(date, DateUtil.YYYY_MM_DD);</code><br>
     * <br>
     * Author：HF-JWinder(2008-10-6 下午04:04:12)
     * 
     * @param date
     *            时间Date
     * @param format
     *            时间格式
     * @return 时间字符串
     */
    public static final String format(Date date, String format) {
        if (format == null || format.trim().length() == 0) {
            format = YYYY_MM_DD;
        }
        if (date == null) {
            date = new Date();
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Method：时间格式化，一般在HTML页面的时间格式转换成数据库存储DATE格式，根据时间格式，用户可以自定义格式化。
     * <code>DateUtil.parse(date, DateUtil.YYYY_MM_DD);</code><br>
     * <br>
     * Author：HF-JWinder(2008-10-6 下午04:11:13)
     * 
     * @param date
     *            时间字符串
     * @param format
     *            时间格式
     * @return Date 数据库存储时间
     */
    public static final Date parse(String date, String format) {
        if (date == null || date.trim().length() == 0) {
            return null;
        }
        if (format == null || format.trim().length() == 0) {
            format = YYYY_MM_DD;
        }
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 现有时间添加天数
     * 
     * @param date
     *            指定时间
     * @param days
     *            添加或减少指定天数
     * @return 最终时间
     * @author JWinder.Huang (Jun 26, 2012)
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

}
