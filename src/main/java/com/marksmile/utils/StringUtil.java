/*
 * Copyright @2010 Net365. All rights reserved.
 */
package com.marksmile.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author wangkunshuang 2015年9月30日 下午3:16:12
 */
public class StringUtil extends StringUtils {

    /**
     * 四舍五入 并保留小数
     * 
     * @param num
     * @param limit
     * @return
     * @author wangkunshuang
     */
    public static BigDecimal roundHalfUp(String num, int limit) {
        BigDecimal result = new BigDecimal(num);
        return result.setScale(limit, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 生成11位流水号
     * 
     * @author wangkunshuang
     */
    public static String createTradeCode() {
        // 格式:mmddHH + 6位
        Date date = new Date();
        long mills = date.getTime();
        String strMills = mills + "";
        String str = strMills.substring(strMills.length() - 7,
                strMills.length());

        String result = Math.round(Math.random() * mills + 1000) + str;
        // 截取后11位
        if (result.length() > 11) {
            result = result.substring(result.length() - 11, result.length());
        }

        return "E" + result;
    }

    /**
     * 验证时间有效性
     * @param str
     * @return
     * @Description :
     */
    public static boolean isValidDate(String str, String dateFormat) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            convertSuccess = false;
        }
        return convertSuccess;
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(converDate(" 2015-04-16T22:15:01Z"));
    }
    public static Date converDate(String input) throws Exception {
        Date result = null;
        String[] dates = input.split(",");
        String date_str = dates[0].toLowerCase();
        try {
            result = parseFromUTC(date_str);
            return result;
        } catch (Exception e) {

        }
        try {
            result = parseFromUTCOnlyT(date_str);
            return result;
        } catch (Exception e) {

        }
        try {
            result = parseFromEnglist(date_str);
            return result;
        } catch (Exception e) {

        }
        try {
            result = parseFromTime(date_str);
            return result;
        } catch (Exception e) {

        }
        try {
            result = parseFromDate(date_str);
            return result;
        } catch (Exception e) {

        }
        try{
            result = parseFromGMT(date_str);
            return result;
        }catch(Exception e){
            
        }
        try {
            result = parseFromYMD(date_str);
            return result;
        } catch (Exception e) {

        }
        throw new Exception("时间："+input+"-时间格式转换出错");
    }

    private static Date parseFromUTC(String input) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd't'hh:mm:ss'z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(input.replaceAll(" ", ""));
    }

    private static Date parseFromUTCOnlyT(String input) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd't'hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(input.replaceAll(" ", ""));
    }

    private static Date parseFromEnglist(String input) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",
                Locale.ENGLISH);
        return sdf.parse(input);
    }

    private static Date parseFromTime(String input) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.parse(input.replaceAll("/", "-"));
    }

    private static Date parseFromDate(String input) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(input.replace("/", "-").trim());
    }

    private static Date parseFromGMT(String input) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'gmt' yyyy", Locale.US);  
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(input);
    }
    
    private static Date parseFromYMD(String input) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.parse(input);
    }
    
}
