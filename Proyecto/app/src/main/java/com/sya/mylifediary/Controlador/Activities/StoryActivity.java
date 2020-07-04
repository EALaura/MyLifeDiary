package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.sya.mylifediary.Controlador.Services.Location.LocationBroadcastReceiver;
import com.sya.mylifediary.Controlador.Services.Location.StoryActivityInf;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class StoryActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private LocationBroadcastReceiver broadcastReceiver;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    TextView textAddress;
    ImageView image;
    EditText titleText, descriptionText;
    Button camera, save;
    String title, location, description;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        broadcastReceiver = new LocationBroadcastReceiver(storyActivityInf, this);
        checkLocationPermission();
        findViewItems();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleText.getText().toString();
                description = descriptionText.getText().toString();
                if (title.equals("")){
                    titleText.setError("Required");
                } else if(description.equals("")) {
                    descriptionText.setError("Required");
                } else {
                    saveStory();
                }
            }
        });
    }

    private void findViewItems() {
        textAddress = findViewById(R.id.textAddress);
        image = findViewById(R.id.photo);
        camera = findViewById(R.id.btn_cam);
        descriptionText = findViewById(R.id.txt_description);
        titleText = findViewById(R.id.textTitle);
        save = findViewById(R.id.buttonStory);
    }

    private void saveStory() {
        createImageFromBitmap(bitmap);
        Story story = new Story(title, location, description, null);
        Intent intent = new Intent(StoryActivity.this, ListStories.class);
        intent.putExtra("story", story);
        startActivity(intent);
        Toast.makeText(StoryActivity.this, "Guardado Exitosamente!", Toast.LENGTH_SHORT).show();
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "photo";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = this.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
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
    // no se reciban lecturas en background
    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap)data.getExtras().get("data");   //Captura la imagen obtenida de la camara
        image.setImageBitmap(bitmap);
    }

    public boolean checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: { // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permission was granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                        broadcastReceiver.initGPS();
                } else {    // permission denied, boo! Disable the
                    Log.d(TAG, "Location not allowed");
                }
                return;
            }
        }
    }

    private StoryActivityInf storyActivityInf = new StoryActivityInf() {
        @Override
        public void DisplayLocationChange(String address) {
            Log.d(TAG,  "Mi ubicacion: " + address);
            textAddress.setText(address);
            location = address;
        }
    };
}
