package com.imrd.copy.util;

import android.util.Log;

public class LogProcessUtil {
    private static final boolean LOG_D = true;
    private static final boolean LOG_E = true;
    private static final boolean LOG_I = true;

    public static void LogPushD(Object source, String log){
        if(LOG_D) Log.d(source.getClass().getName(), log);
    }

    public static void LogPushE(Object source, String log){
        if(LOG_E) Log.e(source.getClass().getName(), log);
    }

    public static void LogPushI(Object source, String log){
        if(LOG_I) Log.i(source.getClass().getName(), log);
    }
}
