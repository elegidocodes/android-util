package com.elegidocodes.android.util.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrength;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class NetworkUtil {

    /**
     * Checks if a network connection is available.
     * Compatible with Android API level 21 and later.
     *
     * <p>Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * }</pre>
     *
     * @param context The application context to access the system's connectivity service.
     * @return true if a network connection is available, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        // Get the system's connectivity manager service
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // For API level 23 and above, use Network and NetworkCapabilities
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                    return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                }
            } else {
                // For API level 21 to 22, use the deprecated getActiveNetworkInfo()
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }

        // Return false if the connectivity manager is null or for any other reason.
        return false;
    }

    /**
     * Checks if a network connection is available and returns the type of connection.
     * Compatible with Android API level 21 and later.
     *
     * <p>Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * }</pre>
     *
     * @param context The application context to access the system's connectivity service.
     * @return A NetworkConnectionType enum indicating the type of connection (e.g., WIFI_CONNECTION, MOBILE_DATA_CONNECTION, NO_INTERNET_CONNECTION).
     */
    public static NetworkConnectionType getNetworkConnectionType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // For API level 23 and above
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                    if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            return NetworkConnectionType.WIFI_CONNECTION;
                        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            return NetworkConnectionType.MOBILE_DATA_CONNECTION;
                        }
                    }
                }
            } else {
                // For API level 21 and 22
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    switch (activeNetworkInfo.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            return NetworkConnectionType.WIFI_CONNECTION;
                        case ConnectivityManager.TYPE_MOBILE:
                            return NetworkConnectionType.MOBILE_DATA_CONNECTION;
                    }
                }
            }
        }

        // Return NO_INTERNET_CONNECTION if no active connection is found
        return NetworkConnectionType.NO_INTERNET_CONNECTION;
    }

    /**
     * Determines the type of network connection and evaluates its speed.
     * Compatible with Android API level 21 and later.
     *
     * <p>Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * }</pre>
     *
     * @param context                 The application context to access the system's connectivity service.
     * @param slowDownstreamThreshold The threshold in Kbps for a "slow" downstream connection.
     * @param slowUpstreamThreshold   The threshold in Kbps for a "slow" upstream connection.
     * @return A NetworkConnectionType indicating the type and speed of the connection.
     */
    public static NetworkConnectionType getNetworkConnectionTypeAndSpeed(Context context,
                                                                         int slowDownstreamThreshold,
                                                                         int slowUpstreamThreshold) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // For API level 23 and above
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                    if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        int downstreamBandwidth = networkCapabilities.getLinkDownstreamBandwidthKbps();
                        int upstreamBandwidth = networkCapabilities.getLinkUpstreamBandwidthKbps();

                        boolean isSlowConnection = downstreamBandwidth < slowDownstreamThreshold || upstreamBandwidth < slowUpstreamThreshold;

                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            return isSlowConnection ? NetworkConnectionType.WIFI_CONNECTION_SLOW : NetworkConnectionType.WIFI_CONNECTION;
                        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            return isSlowConnection ? NetworkConnectionType.MOBILE_DATA_CONNECTION_SLOW : NetworkConnectionType.MOBILE_DATA_CONNECTION;
                        }
                    }
                }
            } else {
                // Fallback for API level 21 and 22
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    switch (activeNetworkInfo.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            return NetworkConnectionType.WIFI_CONNECTION; // No speed check for older APIs
                        case ConnectivityManager.TYPE_MOBILE:
                            return NetworkConnectionType.MOBILE_DATA_CONNECTION; // No speed check for older APIs
                    }
                }
            }
        }

        return NetworkConnectionType.NO_INTERNET_CONNECTION;
    }

    /**
     * Checks the intensity of the current Wi-Fi connection and evaluates if it's fast or slow.
     *
     * <p>Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     * }</pre>
     *
     * @param context The application context to access the system's Wi-Fi service.
     * @return true if the Wi-Fi connection is fast, false if it's slow or unavailable.
     */
    public static boolean isWifiFast(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                int rssi = wifiInfo.getRssi(); // Signal strength in dBm
                int linkSpeed = wifiInfo.getLinkSpeed(); // Link speed in Mbps

                // Define thresholds for fast Wi-Fi
                final int RSSI_THRESHOLD = -70; // Higher is better (e.g., -60 dBm is stronger than -70 dBm)
                final int LINK_SPEED_THRESHOLD = 15; // Mbps

                return rssi > RSSI_THRESHOLD && linkSpeed >= LINK_SPEED_THRESHOLD;
            }
        }

        return false; // Wi-Fi is unavailable or too weak
    }

    /**
     * Checks the intensity of the current mobile data connection and evaluates if it's fast or slow.
     *
     * <p>Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     * }</pre>
     *
     * @param context The application context to access the system's telephony service.
     * @return true if the mobile data connection is fast, false if it's slow or unavailable.
     */
    public static boolean isMobileDataFast(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            CellSignalStrength signalStrength = getCellSignalStrength(context);

            if (signalStrength != null) {
                int signalLevel = signalStrength.getLevel(); // Signal level (0-4)

                // Define a threshold for fast mobile data
                final int SIGNAL_LEVEL_THRESHOLD = 3; // Signal level 3 or 4 is considered strong

                return signalLevel >= SIGNAL_LEVEL_THRESHOLD;
            }
        }

        return false; // Mobile data is unavailable or too weak
    }

    /**
     * Helper method to get CellSignalStrength for the current network.
     *
     * <p>This method is experimental and may change in future versions.</p>
     *
     * <p>Required permissions in the AndroidManifest.xml:
     * <pre>{@code
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     * }</pre>
     *
     * @param context The application context to access the telephony service.
     * @return The signal strength of the currently registered network or null if not available.
     */
    private static CellSignalStrength getCellSignalStrength(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Location permission is required for accessing cell signal strength on API 29+
                return null;
            }

            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            if (cellInfoList != null && !cellInfoList.isEmpty()) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo.isRegistered()) {
                        if (cellInfo instanceof CellInfoLte) {
                            return ((CellInfoLte) cellInfo).getCellSignalStrength();
                        } else if (cellInfo instanceof CellInfoNr) {
                            return ((CellInfoNr) cellInfo).getCellSignalStrength();
                        } else if (cellInfo instanceof CellInfoGsm) {
                            return ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        } else if (cellInfo instanceof CellInfoCdma) {
                            return ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Enum representing different types of network connection statuses.
     */
    public enum NetworkConnectionType {
        NO_INTERNET_CONNECTION(-1),
        WIFI_CONNECTION(1),
        MOBILE_DATA_CONNECTION(2),
        WIFI_CONNECTION_SLOW(3),
        MOBILE_DATA_CONNECTION_SLOW(4);

        private final int value;

        NetworkConnectionType(int value) {
            this.value = value;
        }

        /**
         * Finds a NetworkConnectionType based on its integer value.
         *
         * @param value The integer value to look for.
         * @return The corresponding NetworkConnectionType, or null if not found.
         */
        public static NetworkConnectionType fromValue(int value) {
            for (NetworkConnectionType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return null; // Or throw an exception if needed
        }

        /**
         * Gets the integer value associated with the connection type.
         *
         * @return The integer value of the connection type.
         */
        public int getValue() {
            return value;
        }
    }

}
