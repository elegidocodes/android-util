package com.elegidocodes.android.util.network;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UrlUtil {

    /**
     * Determines if the given string is a valid URL starting with "http://" or "https://".
     *
     * <p>This method checks if the input string is non-null and starts with a valid HTTP or HTTPS prefix.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String data = "https://www.example.com";
     * boolean isValidUrl = UrlUtils.isUrl(data); // Returns true
     * }</pre>
     * </p>
     *
     * @param data The string to be checked.
     * @return {@code true} if the string is a valid URL, {@code false} otherwise.
     */
    public static boolean isUrl(String data) {
        return data != null && (data.startsWith("http://") || data.startsWith("https://"));
    }

    /**
     * Opens the specified URL in the default browser or associated app on the user's device.
     *
     * <p>This method constructs an intent with the given URL and starts an activity to handle the URL.
     * Ensure that the URL is valid before calling this method.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String url = "https://www.example.com";
     * UrlUtils.openUrl(context, url); // Opens the URL
     * }</pre>
     * </p>
     *
     * @param context The context used to start the activity.
     * @param url     The URL to be opened.
     * @throws IllegalArgumentException if the provided URL is null or empty.
     */
    public static void openUrl(Context context, String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);

    }

}