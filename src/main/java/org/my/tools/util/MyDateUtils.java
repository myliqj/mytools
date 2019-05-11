package org.my.tools.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDateUtils {

    /** ��׼���ڸ�ʽ */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    /** ��׼ʱ���ʽ */
    public static final String PATTERN_TIME = "HH:mm:ss";
    /** ��׼����ʱ���ʽ����ȷ���� yyyy-MM-dd HH:mm */
    /** ��׼����ʱ���ʽ����ȷ����  yyyy-MM-dd HH:mm:ss */
    /** ��׼����ʱ���ʽ����ȷ������ */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss:SSS";
//	public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /** ��׼���ڣ�����ʱ�䣩��ʽ���� */
    //public static final java.text.SimpleDateFormat formater_date = new java.text.SimpleDateFormat(PATTERN_DATE);
    private static ThreadLocal<SimpleDateFormat> NORM_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        synchronized protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(PATTERN_DATE);
        };
    };
    /** ��׼ʱ���ʽ���� */
    //public static final java.text.SimpleDateFormat formater_time = new java.text.SimpleDateFormat(PATTERN_TIME);
    private static ThreadLocal<SimpleDateFormat> NORM_TIME_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        synchronized protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(PATTERN_TIME);
        };
    };
    /** ��׼����ʱ���ʽ���� */
    //public static final java.text.SimpleDateFormat formater_datetime = new java.text.SimpleDateFormat(PATTERN_DATETIME);
    private static ThreadLocal<SimpleDateFormat> NORM_DATETIME_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        synchronized protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(PATTERN_DATETIME);
        };
    };
    /**
     * ��ʽ yyyy-MM-dd
     *
     * @param date ����ʽ��������
     * @return ��ʽ������ַ���
     */
    public static String formatDate(Date date) {
        return NORM_DATE_FORMAT.get().format(date);
    }
    /**
     * ��ʽ HH:mm:ss
     *
     * @param date ����ʽ����ʱ��
     * @return ��ʽ������ַ���
     */
    public static String formatTime(Date date) {
        return NORM_TIME_FORMAT.get().format(date);
    }
    /**
     * ��ʽ yyyy-MM-dd HH:mm:ss:SSS
     *
     * @param date ����ʽ��������
     * @return ��ʽ���������
     */
    public static String formatDateTime(Date date) {
        return NORM_DATETIME_FORMAT.get().format(date);
    }
    public static String formatCurrentDateTime() {
        return NORM_DATETIME_FORMAT.get().format(new Date());
    }
}
