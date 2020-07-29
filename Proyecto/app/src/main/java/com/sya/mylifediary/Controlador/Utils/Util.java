package com.sya.mylifediary.Controlador.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.sya.mylifediary.R;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void enableButton(Button btn, Context context){
        btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.pink_button));
        btn.setEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void disableButton(Button btn, Context context){
        btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray_button));
        btn.setEnabled(false);
    }
}
