package com.sya.mylifediary.Controlador.Utils;

import android.content.SharedPreferences;

/* Util es la clase que maneja los datos de SharePreferences,
   para el manejo de los datos de sesión del usuario
   LAS ACTIVITYS QUE IMPLEMENTEN A ACELEROMETRO TAMBIEN IMPLEMENTARAN
   UNA INSTANCIA DE SHAREPREFERENCES*/

public class Util {

    /* Retorna el email del usuario que esté guardado */
    public static String getUserEmailPrefs(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("email", "");
    }

    /* Retorna la contraseña del usuario que esté guardado */
    public static String getUserPassPrefs(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("password", "");
    }

    /* Borra los datos de SharePreferences de la sesión existente */
    public static void removeSharedPreferences(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
    }
}
