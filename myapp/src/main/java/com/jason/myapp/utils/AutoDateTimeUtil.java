package com.jason.myapp.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by sunjianchao on 18/5/21.
 */
public class AutoDateTimeUtil {

    /**
     * 判断系统使用的是24小时制还是12小时制
     *
     * @param mContext
     * @return
     */
    public static boolean is24HourFormat(Context mContext) {
        return DateFormat.is24HourFormat(mContext);
    }

    /**
     * 24小时制
     *
     * @param mContext
     */
    public static void set24HourFormat(Context mContext) {
        Settings.System.putString(mContext.getContentResolver(),
                Settings.System.TIME_12_24, "24");
    }

    /**
     * 12小时制
     *
     * @param mContext
     */
    public static void set12HourFormat(Context mContext) {
        Settings.System.putString(mContext.getContentResolver(),
                Settings.System.TIME_12_24, "12");
    }

    /**
     * 判断系统的时区是否是自动获取的
     *
     * @param mContext
     * @return
     */
    public static boolean isTimeZoneAuto(Context mContext) {
        try {
            return Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置系统的时区是否自动获取
     *
     * @param checked
     * @param mContext
     */
    public static void setAutoTimeZone(int checked, Context mContext) {
        Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.AUTO_TIME_ZONE, checked);
    }

    /**
     * 判断系统的时间是否自动获取的
     *
     * @param mContext
     * @return
     */
    public static boolean isDateTimeAuto(Context mContext) {
        try {
            return Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置系统的时间是否需要自动获取
     * 0:不自动获取 1：自动获取
     *
     * @param checked
     * @param mContext
     */
    public static void setAutoDateTime(int checked, Context mContext) {
        Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.AUTO_TIME, checked);
    }

    /**
     * 设置系统日期
     *
     * @param year
     * @param month
     * @param day
     * @param mContext
     */
    public void setSysDate(int year, int month, int day, Context mContext) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /**
     * 设置系统时间
     *
     * @param hour
     * @param minute
     * @param mContext
     */
    public void setSysTime(int hour, int minute, Context mContext) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /**
     * 设置系统时区
     *
     * @param timeZone
     */
    public void setTimeZone(String timeZone) {
        final Calendar now = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        now.setTimeZone(tz);
    }

    /**
     * 获取系统当前的时区
     *
     * @return
     */
    public String getDefaultTimeZone() {
        return TimeZone.getDefault().getDisplayName();
    }
}
