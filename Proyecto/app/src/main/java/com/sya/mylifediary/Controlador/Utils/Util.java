package com.sya.mylifediary.Controlador.Utils;

import android.content.SharedPreferences;

public class Util {

    public static String getUserEmailPrefs(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("email", "");
    }

    public static String getUserPassPrefs(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("password", "");
    }

    public static void removeSharedPreferences(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
    }
}
