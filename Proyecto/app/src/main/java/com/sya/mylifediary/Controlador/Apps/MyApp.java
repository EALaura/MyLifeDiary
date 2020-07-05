package com.sya.mylifediary.Controlador.Apps;

import android.app.Application;
import android.os.SystemClock;

/* Esta clase permite que la carga del splash screen dure 2 segundos
*  ya que es una activity que por si sola redirige al home o al login.
*  En el manifest se declara esta aplicación para iniciar en ella
*  android:name=".Controlador.Apps.MyApp"    */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Muestra la imagen de carga por 2 segundos.
        SystemClock.sleep(2000);
    }
}
