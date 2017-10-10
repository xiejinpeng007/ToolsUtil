package com.util.xiejinpeng.toolsutil;

import io.reactivex.functions.Consumer;

public class LogUtil {
    private static boolean debug = BuildConfig.DEBUG;

    private static final Consumer<Throwable> errorAction = LogUtil::e;

    private LogUtil(){}

    public static void setDebug(boolean debug) {
        LogUtil.debug = debug;
    }

    public static void m(String tag, String msg) {
        if(debug)
            System.out.println(tag + "| " + msg);
    }

    public static void m(Class clazz, String msg) {
        m(clazz.getSimpleName(), msg);
    }

    public static void m(Object from, String msg) {
        m(from.getClass().getSimpleName(), msg);
    }

    public static void m(String msg) {
        if(debug)
            System.out.println(msg);
    }

    public static void e(Throwable t) {
        if(debug)
            t.printStackTrace();
    }

    public static void e(String tag, String msg, Throwable t) {
        if(debug) {
            m(tag, msg);
            t.printStackTrace();
        }
    }

    public static Consumer<Throwable> logError() {
        return errorAction;
    }
}