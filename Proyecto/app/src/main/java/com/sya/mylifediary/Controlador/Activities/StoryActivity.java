package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.sya.mylifediary.Controlador.Services.Location.LocationBroadcastReceiver;
import com.sya.mylifediary.Controlador.Services.Location.StoryActivityInf;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;

public class StoryActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private LocationBroadcastReceiver broadcastReceiver;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    TextView textAddress;
    ImageView image;
    EditText descr;
    Button camera, save;
    String location, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        broadcastReceiver = new LocationBroadcastReceiver(storyActivityInf);
        checkLocationPermission();
        textAddress = findViewById(R.id.textAddress);
        image = findViewById(R.id.photo);
        camera = findViewById(R.id.btn_cam);
        descr = findViewById(R.id.description);
        save = findViewById(R.id.buttonStory);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        description = descr.getText().toString();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Story story = new Story(location, description,3);
                Intent intent = new Intent(StoryActivity.this, ListStories.class);
                intent.putExtra("story", story);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();   // filtro de tipo key
        if (broadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(LocationManager.KEY_LOCATION_CHANGED);
            registerReceiver(broadcastReceiver, intentFilter);
        } else {
            Log.d(TAG, "broadcastReceiver is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Captura la imagen obtenida de la camara
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        image.setImageBitmap(bitmap);
    }

    public boolean checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        initGPS();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "Location not allowed");
                }
                return;
            }
        }
    }

    public void initGPS() {
        // enviara directamente el mensaje al LOcation broadcast receiver
        // llamado implicito
        Intent intent = new Intent((LocationManager.KEY_LOCATION_CHANGED));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(//sendBroadcast(...)
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, pendingIntent);
    }

    private StoryActivityInf storyActivityInf = new StoryActivityInf() {
        @Override
        public void DisplayLocationChange(double latitude, double longitude) {
            String address = convertToAddress(longitude, latitude);
            Log.d(TAG,  "Direccion mi casa :v: " + address);
            textAddress.setText(address);
            location = address;
        }
        //public void Display enabled
    };

    private String convertToAddress(double longitude, double latitude) {
        String addressText = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
