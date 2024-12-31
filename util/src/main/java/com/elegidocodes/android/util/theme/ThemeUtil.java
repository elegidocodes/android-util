package com.elegidocodes.android.util.theme;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
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

    /**
     * Toggles the application's day/night mode programmatically.
     *
     * @param enableDarkMode Whether to enable dark (night) mode.
     */
    public static void toggleNightMode(boolean enableDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
                enableDarkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /**
     * Sets the status bar color depending on whether dark mode is enabled.
     *
     * @param activity      The activity whose status bar color is to be changed.
     * @param dayColorRes   The color resource for day (light mode).
     * @param nightColorRes The color resource for night (dark mode).
     */
    public static void setStatusBarColorForTheme(
            @NonNull Activity activity,
            @ColorRes int dayColorRes,
            @ColorRes int nightColorRes
    ) {

        boolean isDarkMode = isDarkModeEnabled(activity);

        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = activity.getColor(isDarkMode ? nightColorRes : dayColorRes);
        } else {
            color = ContextCompat.getColor(activity, isDarkMode ? nightColorRes : dayColorRes);
        }
        activity.getWindow().setStatusBarColor(color);
    }

    /**
     * Retrieves a color from the current theme's attributes.
     *
     * @param context The context to access the theme.
     * @param attrRes The attribute resource ID (e.g., R.attr.colorPrimary).
     * @return The resolved color as an integer.
     */
    @ColorInt
    public static int getThemeColor(@NonNull Context context, @AttrRes int attrRes) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        if (theme.resolveAttribute(attrRes, typedValue, true)) {
            if (typedValue.resourceId != 0) {
                return ContextCompat.getColor(context, typedValue.resourceId);
            } else {
                return typedValue.data;
            }
        }
        // Fallback or throw if attribute not found
        return Color.BLACK;
    }

    /**
     * Applies the current theme's colors to a Toolbar.
     *
     * @param context  The context to resolve theme attributes.
     * @param toolbar  The Toolbar to style.
     * @param bgAttr   The attribute for the background color (e.g., R.attr.colorPrimary).
     * @param iconAttr The attribute for the icon tint color (e.g., R.attr.colorOnPrimary).
     */
    public static void applyThemeToToolbar(
            @NonNull Context context,
            @NonNull Toolbar toolbar,
            @AttrRes int bgAttr,
            @AttrRes int iconAttr
    ) {
        int backgroundColor = getThemeColor(context, bgAttr);
        int iconColor = getThemeColor(context, iconAttr);

        toolbar.setBackgroundColor(backgroundColor);
        toolbar.setTitleTextColor(iconColor);

        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setTint(iconColor);
        }
    }

    /**
     * Sets the background color or drawable of a view depending on day/night mode.
     *
     * @param view               The view to apply the background to.
     * @param dayBackgroundRes   The background (color or drawable) used in light mode.
     * @param nightBackgroundRes The background (color or drawable) used in dark mode.
     */
    public static void setDayNightBackground(
            @NonNull View view,
            @DrawableRes int dayBackgroundRes,
            @DrawableRes int nightBackgroundRes
    ) {
        Context context = view.getContext();
        boolean isDarkMode = isDarkModeEnabled(context);

        Drawable drawable = ContextCompat.getDrawable(context, isDarkMode ? nightBackgroundRes : dayBackgroundRes);
        if (drawable != null) {
            view.setBackground(drawable);
        }
    }

}
