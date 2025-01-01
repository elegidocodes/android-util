package com.elegidocodes.android.util.device;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

/**
 * Utility class for retrieving device screen dimensions in pixels.
 * <p>
 * Note: Some values represent the absolute pixel dimensions of the screen
 * and do not take into account density or the current orientation (portrait/landscape)
 * beyond what the system resources automatically handle.
 */
public class DeviceScreenUtil {

    /**
     * Retrieves the height of the device's screen in pixels.
     *
     * @param context the context of the application, must not be null
     * @return the height of the device's screen in pixels
     */
    public static int getDeviceHeight(@NonNull Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /**
     * Retrieves the width of the device's screen in pixels.
     *
     * @param context the context of the application, must not be null
     * @return the width of the device's screen in pixels
     */
    public static int getDeviceWidth(@NonNull Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

}
