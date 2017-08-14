package com.seeviews.utils;

import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jan-Willem on 30-11-2016.
 */

public class StringUtils {

    private static final String TAG = "StringUtils";

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean hasEmpty(String... strings) {
        if (strings == null || strings.length == 0)
            return true;
        else
            for (String s : strings)
                if (isEmpty(s))
                    return true;
        return false;
    }

    public static String[] toArray(ArrayList<String> strings) {
        if (strings == null)
            return new String[0];
        else {
            String[] res = new String[strings.size()];
            for (int i = 0; i < strings.size(); i++) {
                res[i] = strings.get(i);
            }
            return res;
        }
    }

    public static String toDuration(int duration) {
        Log.d(TAG, "toDuration: " + duration);
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        try {
            return sdf.format(new Date(duration));
        } catch (Exception e) {
            Log.e(TAG, "toDuration: " + e.getLocalizedMessage());
            return "";
        }
    }

    private static String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String fileSize(File file) {
        return readableFileSize(file.length());
    }
}
