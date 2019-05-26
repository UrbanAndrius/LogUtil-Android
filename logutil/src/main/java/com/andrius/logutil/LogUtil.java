package com.andrius.logutil;

import android.util.Log;

public final class LogUtil {

    private LogUtil() {
    }

    public static void d(String message) {
        Log.d("TEST TAG", message);
    }
}
