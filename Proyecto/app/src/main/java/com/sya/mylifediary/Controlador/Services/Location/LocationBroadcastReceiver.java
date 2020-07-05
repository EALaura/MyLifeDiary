package com.sya.mylifediary.Controlador.Services.Location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* Por medio de la implementación del BroadcastReceiver se recibe los cambios de
   Location mediante el LocationManager. Cuando se le crea se necesita especificar la
   interfaz que devuelve los datos a la activity y el contexto del activity */
public class LocationBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "LocationBroadcastReceiver";
    private StoryActivityInf storyActivityInf;
    private Context context;
    private String address;

    /* Constructor, mainActivityInf es el objeto en el que retornara la dirección al activity,
       context es la referencia al activity que llama a esta clase */
    public LocationBroadcastReceiver(StoryActivityInf mainActivityInf, Context context) {
        this.storyActivityInf = mainActivityInf;
        this.context = context;
    }

    /* Detecta si hay cambios de ubicación, entonces captura los datos de latitud y longitud.*/
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
            String locationKey = LocationManager.KEY_LOCATION_CHANGED;
            Location location = (Location) intent.getExtras().get(locationKey);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG, "latitud: " + latitude + ", " + "longitud: " + longitude);
            address = convertToAddress(longitude, latitude);
            storyActivityInf.DisplayLocationChange(address);
        }
    }

    /*
    * Inicializa el LocationManager mediante un pendingIntent especifica una acción
    * a tomar en el futuro. Le permite pasar un intent futuro a requestLocationUpdates
    * y permitir que la aplicación ejecute ese intent.
     */
    public void initGPS() {
        // Envia directamente el mensaje al Location broadcast receiver, llamado implicito
        Intent intent = new Intent((LocationManager.KEY_LOCATION_CHANGED));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, pendingIntent);
    }

    /* Convierte las coordenadas a una dirección real para que el usuario lo entienda
    *  Usa el paquete Geocoder y retorna un String de la dirección exacta.
    * Se hacen las validaciones necesarias si se encuentra algunos errores al momento de
    * convertir la dirección como errores de red, valores inválidos de longitud y latitud,
    *  si no se convierte las coordenadas en una dirección */
    private String convertToAddress(double longitude, double latitude) {
        String addressText = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) {
            // Porblemas de redes
            Log.e(TAG, "network_service_error", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Invalidos valores de long/ lat
            Log.e(TAG, "invalid_long_lat" + ". " + "Latitude = " + latitude + ", Longitude = " + longitude, illegalArgumentException);
        }
        // Si no se encontró la dirección
        if (addresses == null || addresses.size() == 0) {
            Log.e(TAG, "no_address_found");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Une los datos en una sola dirección
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "address_found");
            addressText = TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }
        return addressText;     // retorna la dirección como un string
    }
}
