package com.elegidocodes.android.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

public abstract class AbstractNetworkCallback {

    private ConnectivityManager.NetworkCallback networkCallback;

    /**
     * Called when the network becomes available.
     */
    protected abstract void onNetworkAvailable();

    /**
     * Called when the network is lost.
     */
    protected abstract void onNetworkLost();

    /**
     * Called when the network capabilities change.
     *
     * @param networkCapabilities The updated {@link NetworkCapabilities}.
     */
    protected abstract void onNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities);

    /**
     * Starts monitoring network connectivity changes.
     *
     * @param context The application context.
     */
    public void startMonitoring(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return; // Handle the case where the ConnectivityManager is unavailable
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API 24+ (Android 7.0+): Use registerDefaultNetworkCallback
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    onNetworkAvailable();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    onNetworkLost();
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    onNetworkCapabilitiesChanged(networkCapabilities);
                }
            };
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            // API 21–23 (Android 5.0–6.0): Use registerNetworkCallback with a NetworkRequest
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    onNetworkAvailable();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    onNetworkLost();
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    onNetworkCapabilitiesChanged(networkCapabilities);
                }
            };
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        }
    }

    /**
     * Stops monitoring network connectivity changes.
     *
     * @param context The application context.
     */
    public void stopMonitoring(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

}


