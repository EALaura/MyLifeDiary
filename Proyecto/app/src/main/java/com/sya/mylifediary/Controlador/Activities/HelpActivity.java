package com.sya.mylifediary.Controlador.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.LightSensor.LightSensor;
import com.sya.mylifediary.R;

public class HelpActivity extends AppCompatActivity {
    private LinearLayout helpView;
    private SharedPreferences sharedPreferences;
    private Acelerometro acelerometro;
    private LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        helpView = findViewById(R.id.helpView);

        // Configuracion de Sensores
        acelerometro = new Acelerometro(this, sharedPreferences);
        lightSensor = new LightSensor(this, helpView);
    }

    // Cuando la activity esta en background se detienen las
    // lecturas del acelerometro y light sensor
    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        lightSensor.getSensorManager().unregisterListener(lightSensor);
        super.onPause();
    }

    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        lightSensor.iniciarSensor();
        super.onRestart();
    }
}
