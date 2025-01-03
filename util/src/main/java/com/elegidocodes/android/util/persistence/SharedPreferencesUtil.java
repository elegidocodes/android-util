package com.elegidocodes.android.util.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * A utility class for managing SharedPreferences with generic support for saving and retrieving objects.
 */
public class SharedPreferencesUtil {

    /**
     * Saves an object to SharedPreferences as a JSON string.
     *
     * @param context         The application context.
     * @param preferencesName The name of the SharedPreferences file.
     * @param key             The key under which the object will be stored.
     * @param value           The object to save. It will be serialized to JSON.
     * @param <T>             The type of the object to save.
     */
    public static <T> void save(Context context, String preferencesName, String key, T value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        String json = new Gson().toJson(value);
        sharedPreferences.edit().putString(key, json).apply();
    }

    /**
     * Retrieves an object from SharedPreferences by deserializing its JSON representation.
     *
     * @param context         The application context.
     * @param preferencesName The name of the SharedPreferences file.
     * @param key             The key under which the object is stored.
     * @param type            The class type of the object to retrieve.
     * @param <T>             The type of the object to retrieve.
     * @return The deserialized object, or {@code null} if no data is found.
     */
    public static <T> T get(Context context, String preferencesName, String key, Class<T> type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        if (json != null) {
            return new Gson().fromJson(json, type);
        }
        return null;
    }

    /**
     * Removes a key-value pair from SharedPreferences.
     *
     * @param context         The application context.
     * @param preferencesName The name of the SharedPreferences file.
     * @param key             The key to remove.
     */
    public static void remove(Context context, String preferencesName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).apply();
    }

    /**
     * Clears all data from a specified SharedPreferences file.
     *
     * @param context         The application context.
     * @param preferencesName The name of the SharedPreferences file.
     */
    public static void clear(Context context, String preferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

}
