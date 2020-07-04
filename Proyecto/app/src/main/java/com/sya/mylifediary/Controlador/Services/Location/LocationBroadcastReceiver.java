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

public class LocationBroadcastReceiver extends BroadcastReceiver{

    private String TAG = "LocationBroadcastReceiver";
    private StoryActivityInf storyActivityInf;
    private Context context;
    private String address;

    public LocationBroadcastReceiver(StoryActivityInf mainActivityInf, Context context){
        this.storyActivityInf = mainActivityInf;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
            String locationKey = LocationManager.KEY_LOCATION_CHANGED;
            Location location = (Location) intent.getExtras().get(locationKey);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG,  "latitud: " + latitude + ", " + "longitud: " + longitude);
            address = convertToAddress(longitude, latitude);
            storyActivityInf.DisplayLocationChange(address);
        }
    }

    public void initGPS() {
        // enviara directamente el mensaje al LOcation broadcast receiver, llamado implicito
        Intent intent = new Intent((LocationManager.KEY_LOCATION_CHANGED));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, pendingIntent);
    }

    private String convertToAddress(double longitude, double latitude) {
        String addressText = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) {
            // Network or other I/O issues
            Log.e(TAG, "network_service_error", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Invalid long / lat
            Log.e(TAG, "invalid_long_lat" + ". " + "Latitude = " + latitude + ", Longitude = " + longitude, illegalArgumentException);
        }
        // No address was found
        if (addresses == null || addresses.size() == 0) {
            Log.e(TAG, "no_address_found");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines, join them, and return to thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "address_found");
            addressText = TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }
        return addressText;
    }
}
