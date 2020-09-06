package com.jason.myapp.utils;

import android.util.Log;
import com.meiguan.ipsplayer.base.BuildConfig;

/**
 * LogUtils工具说明:
 * 只输出等级大于等于LEVEL的日志
 * 所以在开发和产品发布后通过修改LEVEL来选择性输出日志.
 * 当LEVEL=NOTHING则屏蔽了所有的日志.
 */
public class LogUtils {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final String TAG = "dsms";
    private int manner = VERBOSE;

    private static LogUtils instance;

    public static LogUtils getInstance() {

        if (instance == null) {
            instance = new LogUtils();
        }
        return instance;
    }

    private LogUtils() {
        manner = BuildConfig.LOG;
    }

    private boolean checkLogLevel(int flag) {
        return flag >= manner;
    }

    public void v(String tag, String message) {
        if (checkLogLevel(VERBOSE)) {

            Log.v(TAG, tag + ":" + message);
        }
    }

    public void d(String tag, String message) {
        if (checkLogLevel(DEBUG)) {

            Log.d(TAG, tag + ":" + message);
        }
    }

    public void i(String tag, String message) {
        if (checkLogLevel(INFO)) {

            Log.i(TAG, tag + ":" + message);
        }
    }

    public void w(String tag, String message) {
        if (checkLogLevel(WARN)) {

            Log.w(TAG, tag + ":" + message);
        }
    }

    public void e(String tag, String message) {
        if (checkLogLevel(ERROR)) {

            Log.e(TAG, tag + ":" + message);
        }
    }

    public void w(String tag, String message, Throwable e) {
        if (checkLogLevel(WARN)) {

            Log.w(TAG, tag + ":" + message, e);
        }
    }

    public void e(String tag, String message, Throwable e) {
        if (checkLogLevel(ERROR)) {

            Log.e(TAG, tag + ":" + message, e);
        }
    }


}
