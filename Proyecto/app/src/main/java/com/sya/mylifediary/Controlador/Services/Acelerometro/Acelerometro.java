package com.sya.mylifediary.Controlador.Services.Acelerometro;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AlertDialog;

import com.sya.mylifediary.Controlador.Activities.ListStories;

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


    public Acelerometro(Context context){
        this.context = context;
        iniciarSensor();
    }

    private void iniciarSensor(){
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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
        /*Toast toast = Toast.makeText(context, "poner en posicion vertica", Toast.LENGTH_SHORT);
        toast.show();*/

        AlertDialog.Builder alerta = new AlertDialog.Builder(ListStories.getContext());
        alerta.setTitle("Advertencia");
        alerta.setMessage("Para mejor experiencia usar My Life Diary en Vertical");
        alerta.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alerta.create();
        alertDialog.show();
    }

    //Determinar si el dispositivo esta siendo robado y cerrar sesion
    private void peligro(float xAcc, float yAcc, float zAcc){
        mHighPassX = highPass(xAcc, mLastZ, mHighPassZ);
        mHighPassY = highPass(yAcc, mLastY, mHighPassY);
        mHighPassZ = highPass(zAcc, mLastZ, mHighPassZ);
        mLastX = xAcc;
        mLastY = yAcc;
        mLastZ = zAcc;

        double aceleracion = aceleracionTotal(mHighPassX, mHighPassY, mHighPassZ);
        /*Toast toast = Toast.makeText(MainActivity.getContext(),aceleracion + " ac", Toast.LENGTH_SHORT );
        toast.show();*/
        if (aceleracion > 3) {
            // MainActivity.text.setText("ac"+ aceleracion);
            //Toast toast1 = Toast.makeText(MainActivity.getContext(),"ME ROBAN!!!", Toast.LENGTH_SHORT );
            //toast1.show();

        }

    }

    private double aceleracionTotal(float x, float y, float z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    private float highPass(float current, float last, float filtered) {
        return ALPHA * (filtered + current - last);
    }

}
