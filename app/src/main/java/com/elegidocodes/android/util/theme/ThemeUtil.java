package com.elegidocodes.android.util.theme;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ThemeUtil {

    /**
     * Checks if dark mode is enabled in the current theme configuration.
     *
     * @param context The context of the application.
     * @return True if dark mode is enabled, false otherwise.
     */
    public static boolean isDarkModeEnabled(@NonNull Context context) {
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Sets the color scheme and progress background color of the given
     * SwipeRefreshLayout based on whether the current theme is in dark mode.
     *
     * @param swipeRefreshLayout The SwipeRefreshLayout to set colors for. Must not be null.
     * @param dayColorRes        The color resource to use in light mode (e.g., R.color.day_color).
     * @param nightColorRes      The color resource to use in dark mode (e.g., R.color.night_color).
     * @param dayBackgroundRes   The background color resource to use in light mode.
     * @param nightBackgroundRes The background color resource to use in dark mode.
     */
    public static void setSwipeRefreshLayoutColorsForTheme(
            @NonNull SwipeRefreshLayout swipeRefreshLayout,
            @ColorRes int dayColorRes,
            @ColorRes int nightColorRes,
            @ColorRes int dayBackgroundRes,
            @ColorRes int nightBackgroundRes
    ) {
        Context context = swipeRefreshLayout.getContext();

        boolean isDarkMode = isDarkModeEnabled(context);

        int backgroundColor;
        int color;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use the modern API when running on Marshmallow (API 23) or higher.
            backgroundColor = context.getColor(isDarkMode ? nightBackgroundRes : dayBackgroundRes);
            color = context.getColor(isDarkMode ? nightColorRes : dayColorRes);
        } else {
            // For older devices, use the ContextCompat approach.
            backgroundColor = ContextCompat.getColor(context, isDarkMode ? nightBackgroundRes : dayBackgroundRes);
            color = ContextCompat.getColor(context, isDarkMode ? nightColorRes : dayColorRes);
        }

        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(backgroundColor);
        swipeRefreshLayout.setColorSchemeColors(color);
    }

}
