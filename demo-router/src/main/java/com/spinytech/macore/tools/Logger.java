package com.spinytech.macore.tools;

import android.util.Log;

/**
 * Created by wanglei on 2017/1/10.
 */
public class Logger {


    public static int LOG_LEVEL = -1;


    public static void e(String tag, String msg) {
        if (LOG_LEVEL >= Log.ERROR)
            Log.e(tag, msg);
    }
    public static void e(String tag, Throwable e) {
        if (LOG_LEVEL >= Log.ERROR)
            Log.e(tag, "error: ",e);
    }

    public static void w(String tag, String msg) {
        if (LOG_LEVEL >= Log.WARN)
            Log.w(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (LOG_LEVEL >= Log.INFO)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (LOG_LEVEL >= Log.DEBUG)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (LOG_LEVEL >= Log.VERBOSE)
            Log.v(tag, msg);
    }
}
