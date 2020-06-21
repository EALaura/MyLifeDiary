package com.sya.mylifediary.Controlador.Apps;

import android.app.Application;
import android.os.SystemClock;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Que muestre la imagen de carga x 2.5 segundos
        SystemClock.sleep(2000);
    }
}
