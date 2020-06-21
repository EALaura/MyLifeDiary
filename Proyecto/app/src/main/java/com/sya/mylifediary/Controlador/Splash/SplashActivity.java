package com.sya.mylifediary.Controlador.Splash;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import com.sya.mylifediary.Controlador.Activities.*;
import com.sya.mylifediary.Controlador.Utils.Util;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Intent intentLogin = new Intent(this, LoginActivity.class);
        Intent intentHome = new Intent(this, HomeActivity.class);

        if(!TextUtils.isEmpty(Util.getUserEmailPrefs(sharedPreferences))
                && !TextUtils.isEmpty(Util.getUserPassPrefs(sharedPreferences))) {
            startActivity(intentHome);
        } else{
            startActivity(intentLogin);
        }
        finish();
    }
}
