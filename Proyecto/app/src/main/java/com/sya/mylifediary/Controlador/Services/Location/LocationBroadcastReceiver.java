package com.sya.mylifediary.Controlador.Services.Location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;
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
