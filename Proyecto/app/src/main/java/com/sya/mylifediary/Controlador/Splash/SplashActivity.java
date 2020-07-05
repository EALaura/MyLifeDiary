package com.sya.mylifediary.Controlador.Splash;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import com.sya.mylifediary.Controlador.Activities.*;
import com.sya.mylifediary.Controlador.Utils.Util;

/* Splash Activity es la actividad que muestra una imagen cada
*  vez que se abra la aplicación, recupera datos de SharePreferences
*  para verificar si el usuario ya se encontraba logeado */
public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Intent intentLogin = new Intent(this, LoginActivity.class);
        Intent intentHome = new Intent(this, HomeActivity.class);

        /* Si existen datos de sesión de sharePreferences redirige al home,
        *  en caso contrario redirige al login */
        if (!TextUtils.isEmpty(Util.getUserEmailPrefs(sharedPreferences))
                && !TextUtils.isEmpty(Util.getUserPassPrefs(sharedPreferences))) {
            startActivity(intentHome);
        } else {
            startActivity(intentLogin);
        }
        finish();
    }
}
