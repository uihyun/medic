package com.uihyun.lib.logger;

import android.app.Application;

import org.acra.ACRA;

/**
 * Created by Uihyun on 2017. 1. 19..
 */

public class Logger {

    private static boolean isDebug;
    private static String packageName;
    private static boolean isDebugReport;

    public static void initialize(Application application, boolean isDebug, boolean isDebugReport) {
        Logger.isDebug = isDebug;
        Logger.isDebugReport = isDebugReport;

        if (isDebugReport) {
            ACRA.init(application);
            ACRA.getErrorReporter().setEnabled(true);
        }

        packageName = application.getPackageName();
    }

    public static void info(String className, String string) {
        android.util.Log.i(packageName, "LOGGER | " + className + " | " + string);
    }

    public static void debug(String className, String string) {
        if (isDebug) {
            android.util.Log.i(packageName, "LOGGER | " + className + " | " + string);
        }
    }

    public static void trace(String className, String string) {
        if (isDebugReport) {
            ACRA.getErrorReporter().handleSilentException(new Throwable(className + " | " + string));
        }
    }
}
