package com.elegidocodes.android.util.device;


import android.content.Context;
import android.content.res.Configuration;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class for device type detection.
 */
public class DeviceTypeUtil {

    /**
     * Determines the device type (Phone, Tablet, TV, Car, Watch, Desk, or Unknown)
     * by checking both the screen layout and UI mode.
     * <p>
     * Note: Foldable devices often require libraries such as Jetpack WindowManager for
     * more precise detection (e.g., posture), which is beyond the scope of this simple approach.
     *
     * @param context the application or activity context (must not be null)
     * @return a {@link DeviceType} value indicating the device category
     */
    public static @NotNull DeviceType getDeviceType(@NotNull Context context) {
        // 1. Check the UI mode (television, car, watch, etc.)
        int uiMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK;
        switch (uiMode) {
            case Configuration.UI_MODE_TYPE_TELEVISION:
                return DeviceType.TV;
            case Configuration.UI_MODE_TYPE_CAR:
                return DeviceType.CAR;
            case Configuration.UI_MODE_TYPE_WATCH:
                return DeviceType.WATCH;
            case Configuration.UI_MODE_TYPE_DESK:
                return DeviceType.DESK;
            // Add other checks here if more UI_MODE_TYPE_* constants become relevant
            default:
                // If none matched, proceed to check phone/tablet logic
                break;
        }

        // 2. Check screen size to differentiate phone vs. tablet
        int screenLayout = context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenLayout >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            // "LARGE" or "XLARGE" typically indicates a tablet
            return DeviceType.TABLET;
        } else {
            // "NORMAL" or "SMALL" is typically a phone
            return DeviceType.PHONE;
        }
    }

    /**
     * Enum representing possible device types.
     */
    public enum DeviceType {
        PHONE,
        TABLET,
        TV,
        CAR,
        WATCH,
        DESK,
        UNKNOWN
    }

}

