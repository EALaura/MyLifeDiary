package com.sya.mylifediary.Controlador.Services.Acelerometro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;
import com.sya.mylifediary.Controlador.Activities.LoginActivity;
import com.sya.mylifediary.Controlador.Utils.Util;

public class Acelerometro implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor acelerometro;
    private int banderaOrientacion;
    private int banderaMovimiento;
    private static final float ALPHA = 0.8f;
    private float mHighPassX = 0;
    private float mHighPassY = 0;
    private float mHighPassZ = 0;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private SharedPreferences share;

    public Acelerometro(Context context, SharedPreferences share){
        this.context = context;
        this.share = share;
        iniciarSensor();
    }

    public void iniciarSensor(){
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        banderaOrientacion = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float xAcc = sensorEvent.values[0];
        float yAcc = sensorEvent.values[1];
        float zAcc = sensorEvent.values[2];

        //orientacion del dispositivo
        orientacion(xAcc, yAcc);
        movimiento(xAcc, yAcc);
        peligro(xAcc, yAcc, zAcc);
    }

    //Verifica si el dispositivo esta en Horizontal muestra una alerta
    private void orientacion(float xAcc, float yAcc) {
        if (yAcc > 7 | yAcc < -7) {
            banderaOrientacion = 0;
        }
        else if ((xAcc > 7 | xAcc < -7)){ /*& (yAcc < 2 | yAcc > -2))*/
            banderaOrientacion++;
            if (banderaOrientacion == 1){
                alerta();
            }
        }
    }

    //Alerta
    private void alerta() {
        Toast.makeText(context, "Para mejor experiencia usar My Life Diary en Vertical", Toast.LENGTH_SHORT).show();

        /*AlertDialog.Builder alerta = new AlertDialog.Builder(context);
        alerta.setTitle("Advertencia");
        alerta.setMessage("Para mejor experiencia usar My Life Diary en Vertical");
        alerta.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alerta.create();
        alertDialog.show();*/
    }

    //Para ver si se mueve el dispositivo de izquierda a derecha para el cambio de historia
    private void movimiento(float xAcc, float yAcc){
        //Vertical
        if (xAcc < -3 & banderaMovimiento == 0) {
            banderaMovimiento++;
        }
        else if (xAcc > 3 & banderaMovimiento == 1){
            banderaMovimiento++;
        }
        else if (banderaMovimiento == 2) {
            //Implementar funcion para mover las historias
        }
    }

    //Determinar si el dispositivo esta siendo robado y cerrar sesion
    private void peligro(float xAcc, float yAcc, float zAcc){
        mHighPassX = highPass(xAcc, mLastX, mHighPassX);
        mHighPassY = highPass(yAcc, mLastY, mHighPassY);
        mHighPassZ = highPass(zAcc, mLastZ, mHighPassZ);
        mLastX = xAcc;
        mLastY = yAcc;
        mLastZ = zAcc;

        double aceleracion = aceleracionTotal(mHighPassX, mHighPassY, mHighPassZ);
        Log.d("Sensor Acelerometro", " X: " + mHighPassX + ", \tY: " + mHighPassY + ", \t Z: " + mHighPassZ + ", \t Ac. Total: " + aceleracion);
        /*Toast toast = Toast.makeText(MainActivity.getContext(),aceleracion + " ac", Toast.LENGTH_SHORT );
        toast.show();*/
        if (aceleracion > 50) {  // hay un forsejeo por robo
            // MainActivity.text.setText("ac"+ aceleracion);
            //Toast toast1 = Toast.makeText(MainActivity.getContext(),"ME ROBAN!!!", Toast.LENGTH_SHORT );
            //toast1.show();
            logOut();
        }
    }

    private float highPass(float current, float last, float filtered) {
        return ALPHA * (filtered + current - last);
    }

    private double aceleracionTotal(float x, float y, float z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    private void logOut(){
        Util.removeSharedPreferences(share);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public SensorManager getSensorManager(){
        return sensorManager;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
}
