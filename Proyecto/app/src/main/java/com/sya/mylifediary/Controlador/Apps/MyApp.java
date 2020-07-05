package com.sya.mylifediary.Controlador.Apps;

import android.app.Application;
import android.os.SystemClock;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Muestra la imagen de carga por 2 segundos.
        SystemClock.sleep(2000);
    }
}
