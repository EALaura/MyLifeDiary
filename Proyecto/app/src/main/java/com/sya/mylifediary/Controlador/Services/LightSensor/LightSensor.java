package com.sya.mylifediary.Controlador.Services.LightSensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.LinearLayout;

public class LightSensor implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensorLuz;
    private LinearLayout linearLayout;

    //Constructor de light sensor recibe el contexto y el layout a modificar.
    public LightSensor(Context context, LinearLayout linearLayout) {
        this.context = context;
        this.linearLayout = linearLayout;
        iniciarSensor();
    }

    // Inicia el Sensor de Luz
    private void iniciarSensor() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            //Menor Luz
            if (sensorEvent.values[0] < 20) {
                 // a menor luz
                linearLayout.setBackgroundColor(Color.WHITE); // a mayor luz
            } else {
                linearLayout.setBackgroundColor(Color.rgb(154,154,154));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
