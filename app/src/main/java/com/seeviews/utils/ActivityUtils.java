package com.seeviews.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import java.util.List;

/**
 * Created by Jan-Willem on 1-12-2016.
 */

public class ActivityUtils {

    private static boolean hasNavBar(Context context) {
        // Kitkat and less shows container above nav bar
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            return false;
        }
        // Emulator
        if (Build.FINGERPRINT.startsWith("generic")) {
            return true;
        }
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasNoCapacitiveKeys = !hasMenuKey && !hasBackKey;
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean hasOnScreenNavBar = id > 0 && resources.getBoolean(id);
        return hasOnScreenNavBar || hasNoCapacitiveKeys || getNavigationBarHeight(context, true) > 0;
    }

    public static int getNavigationBarHeight(Context context, boolean skipRequirement) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && (skipRequirement || hasNavBar(context))) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getStatusBarHeight(Context c) {
        if (c == null)
            return 0;

        int result = 0;
        int resourceId = c.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = c.getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    public static int getNumActivitiesForIntent(Context c, Intent i) {
        if (c == null || i == null)
            return 0;
        else {
            PackageManager manager = c.getPackageManager();
            List<ResolveInfo> appsThatCanOpenIntent = manager.queryIntentActivities(i, 0);
            return appsThatCanOpenIntent.size();
        }
    }
}
