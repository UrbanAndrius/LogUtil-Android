package com.andrius.logutil;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtil {

    private static final String TAG = "LogUtil";
    private static final String DEBUG = "*** DEBUG ***";
    private static String APP_TAG = "LogUtil";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US);

    private static File LOG_FILE;
    private static int FILE_LENGTH = 30_000;

    public static void init(String appTag) {
        APP_TAG = appTag;
    }

    public static void init(String appTag, File logFile) {
        APP_TAG = appTag;
        LOG_FILE = logFile;
    }

    public static void init(String appTag, File logFile, int fileLength) {
        APP_TAG = appTag;
        LOG_FILE = logFile;
        FILE_LENGTH = fileLength;
    }

    public static void d(String message) {
        d(message, false);
    }

    public static void d(String message, boolean toFile) {
        String log = DEBUG + " " + getCallerClassName() + ": " + message;
        Log.e(APP_TAG, log);
        if (toFile) {
            writeToFile("D: " + log);
        }
    }

    public static void i(String message) {
        i(message, true);
    }

    public static void i(String message, boolean toFile) {
        String log = getCallerClassName() + ": " + message;
        Log.i(APP_TAG, log);
        if (toFile) {
            writeToFile("I: " + log);
        }
    }

    public static void w(String message) {
        w(message, true);
    }

    public static void w(String message, boolean toFile) {
        String log = getCallerClassName() + ": " + message;
        Log.w(APP_TAG, log);
        if (toFile) {
            writeToFile("W: " + log);
        }
    }

    public static void e(String message) {
        e(message, true);
    }

    public static void e(Throwable throwable) {
        e(throwable.toString(), true);
    }

    public static void e(Throwable throwable, String message) {
        e(message + "\n" + throwable.toString(), true);
    }

    public static void e(String message, boolean toFile) {
        String log = getCallerClassName() + ": " + message;
        Log.e(APP_TAG, log);
        if (toFile) {
            writeToFile("E: " + log);
        }
    }

    private static void writeToFile(String text) {
        if (LOG_FILE != null) {
            prependText(LOG_FILE, dateFormat.format(new Date()) + " " + text);
        }
    }

    private static void prependText(File file, String text) {
        String fileString = readFileText(file);

        if (fileString.getBytes().length > FILE_LENGTH) {
            fileString = fileString.substring(0, (int) (FILE_LENGTH * 0.8));
        }

        try {
            FileOutputStream stream = new FileOutputStream(file);
            String log = text + "\n" + fileString;
            stream.write(log.getBytes());
            stream.close();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "File not found " + file.getPath());
        } catch (IOException e) {
            Log.w(TAG, "Cannot write to file " + file.getName());
        }
    }

    private static String readFileText(File file) {
        if (file == null) return "";

        int length = (int) file.length();

        byte[] bytes = new byte[length];

        try (FileInputStream in = new FileInputStream(file)) {
            in.read(bytes);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "File not found " + file.getPath());
            return "";
        } catch (IOException e) {
            Log.w(TAG, "Cannot read file " + file.getName());
        }
        return new String(bytes);
    }

    private static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length < 3) return "UNKNOWN";

        boolean found = false;

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String name = className.substring(className.lastIndexOf(".") + 1);
            if (!found) {
                if (LogUtil.class.getSimpleName().equals(name)) {
                    found = true;
                }
                continue;
            }
            if (!LogUtil.class.getSimpleName().equals(name)) {
                return name;
            }
        }
        return "UNKNOWN";
    }
}
