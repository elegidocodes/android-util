package com.elegidocodes.android.util.device;

import android.content.Context;
import android.content.res.Configuration;

public class DeviceOrientationUtil {

    /**
     * Determines the number of columns (span count) for a layout based on the device's orientation.
     *
     * <p>This method checks the current orientation of the device (landscape or portrait)
     * and returns the appropriate column count specified for each orientation.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * // Example: Setting span count for a GridLayoutManager
     * int landscapeSpan = 4;
     * int portraitSpan = 2;
     * int spanCount = getSpanCount(context, landscapeSpan, portraitSpan);
     * GridLayoutManager layoutManager = new GridLayoutManager(context, spanCount);
     * recyclerView.setLayoutManager(layoutManager);
     * }</pre>
     * </p>
     *
     * @param context   The context used to access resources and determine the device orientation.
     * @param landscape The span count to use when the device is in landscape orientation.
     * @param portrait  The span count to use when the device is in portrait orientation.
     * @return The span count based on the device's current orientation.
     */
    public static int getSpanCount(Context context, int landscape, int portrait) {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return landscape;
        } else {
            return portrait;
        }
    }

}
