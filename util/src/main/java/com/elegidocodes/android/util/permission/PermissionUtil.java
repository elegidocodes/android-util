package com.elegidocodes.android.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    /**
     * Checks if the Notification permission is granted.
     *
     * @param context the application context
     * @return true if the notification permission is granted, false otherwise
     */
    public static boolean isNotificationPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 and above
            return ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }
        // For API levels below 33, notifications don't require special permission
        return true;
    }

    /**
     * Requests Notification permission.
     * This method is for use in an Activity or Fragment.
     * If the user has previously denied the permission, a rationale message is displayed as a toast.
     * <p>
     * Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * &lt;uses-permission android:name="android.permission.POST_NOTIFICATIONS" /&gt;
     * &lt;uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" /&gt;
     * }</pre>
     *
     * @param activity    the activity requesting the permission
     * @param context     the application context used for displaying the rationale message
     * @param message     the rationale message to show if the user previously denied the permission
     * @param requestCode the request code for handling permission result
     */

    public static void requestNotificationPermission(Activity activity, Context context, String message, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 and above
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    android.Manifest.permission.POST_NOTIFICATIONS
            )) {
                // Optionally, explain why the permission is needed here
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    requestCode
            );
        }
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Checks if the Camera permission is granted.
     *
     * @param context the application context
     * @return true if the camera permission is granted, false otherwise
     */
    public static boolean isCameraPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests Camera permission.
     * This method is for use in an Activity or Fragment.
     * If the user has previously denied the permission, a rationale message is displayed as a toast.
     * <p>
     * Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * &lt;uses-permission android:name="android.permission.CAMERA" /&gt;
     * }</pre>
     *
     * @param activity    the activity requesting the permission
     * @param context     the application context used for displaying the rationale message
     * @param message     the rationale message to show if the user previously denied the permission
     * @param requestCode the request code for handling permission result
     */
    public static void requestCameraPermission(Activity activity, Context context, String message, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                android.Manifest.permission.CAMERA
        )) {
            // Optionally, explain why the permission is needed here
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(
                activity,
                new String[]{android.Manifest.permission.CAMERA},
                requestCode
        );
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Checks if the Location permission (either ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION) is granted.
     *
     * @param context the application context
     * @return true if any location permission is granted, false otherwise
     */
    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests Location permissions.
     * This method is for use in an Activity or Fragment.
     * If the user has previously denied the permission, a rationale message is displayed as a toast.
     * <p>
     * Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * &lt;uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /&gt;
     * &lt;uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /&gt;
     * }</pre>
     *
     * @param activity    the activity requesting the permission
     * @param context     the application context used for displaying the rationale message
     * @param message     the rationale message to show if the user previously denied the permission
     * @param requestCode the request code for handling permission result
     */
    public static void requestLocationPermission(Activity activity, Context context, String message, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        )) {
            // Optionally, explain why the permission is needed here
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                },
                requestCode
        );
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Checks if the necessary storage permissions are granted based on the API level.
     *
     * @param context the application context
     * @return true if all required storage permissions are granted, false otherwise
     */
    public static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) { // API 29 and below
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) { // API 30 to 32
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        // No general "storage" permission for API 33+
        return true;
    }

    /**
     * Requests Storage permissions.
     * This method is for use in an Activity or Fragment.
     * Handles different permissions based on the API level.
     * If the user has previously denied the permission, a rationale message is displayed as a toast.
     * <p>
     * Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * &lt;uses-permission
     *     android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     *     android:maxSdkVersion="29" /&gt;
     * &lt;uses-permission
     *     android:name="android.permission.READ_EXTERNAL_STORAGE"
     *     android:maxSdkVersion="32" /&gt;
     * }</pre>
     * <p>
     * Additionally, for API levels 29 and below, include the following inside the &lt;application&gt; tag:
     * <pre>{@code
     * android:requestLegacyExternalStorage="true"
     * }</pre>
     *
     * @param activity    the activity requesting the permission
     * @param context     the application context used for displaying the rationale message
     * @param message     the rationale message to show if the user previously denied the permission
     * @param requestCode the request code for handling permission result
     */
    public static void requestStoragePermission(Activity activity, Context context, String message, int requestCode) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) { // API 29 and below
            String[] permissions = {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            };

            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) { // API 30 to 32
            String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Checks if the necessary media permissions are granted.
     *
     * @param context the application context
     * @return true if all required media permissions are granted, false otherwise
     */
    public static boolean isMediaPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        }
        // No explicit media permissions required for API 32 and below
        return true;
    }

    /**
     * Requests the necessary media permissions (API 33+).
     * This method is for use in an Activity or Fragment.
     * Handles permissions required for accessing images, videos, and audio files on devices running Android 13 (API 33) or later.
     * <p>
     * Note: These permissions do NOT cover PDFs or other general file types. For accessing such files, use the Storage Access Framework (SAF).
     * <p>
     * Required permissions in the AndroidManifest.xml for API 33+:
     * <pre>{@code
     * &lt;uses-permission android:name="android.permission.READ_MEDIA_IMAGES" /&gt;
     * &lt;uses-permission android:name="android.permission.READ_MEDIA_VIDEO" /&gt;
     * &lt;uses-permission android:name="android.permission.READ_MEDIA_AUDIO" /&gt;
     * }</pre>
     *
     * @param activity    the activity requesting the permissions
     * @param context     the application context used for displaying the rationale message
     * @param message     the rationale message to show if the user previously denied the permission
     * @param requestCode the request code for handling permission result
     */
    public static void requestMediaPermission(Activity activity, Context context, String message, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            String[] permissions = {
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_VIDEO,
                    android.Manifest.permission.READ_MEDIA_AUDIO
            };

            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }


}

