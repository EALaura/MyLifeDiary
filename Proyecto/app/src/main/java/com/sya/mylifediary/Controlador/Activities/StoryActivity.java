package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Location.LocationBroadcastReceiver;
import com.sya.mylifediary.Controlador.Services.Location.StoryActivityInf;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/* Es la Activity para la creación de la historia, el usuario puede tomar una foto,
*  añadir un titulo, una descripcion, la ubicación se obtiene por medio de
*  LocationBroadcastReceiver que recibe la posicion exacta del usuario */
public class StoryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    public Acelerometro acelerometro;
    private static final String TAG = "MainActivity";
    private LocationBroadcastReceiver broadcastReceiver;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    public TextView textAddress;
    public ImageView image;
    public EditText titleText, descriptionText;
    public Button camera, save;
    public String title, location, description;
    public Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // inicializa el braodcastReceiber
        broadcastReceiver = new LocationBroadcastReceiver(storyActivityInf, this);
        checkLocationPermission();  // Verifica los permisos de ubicación
        findViewItems();
        // Con intent implicito inicia el servicio de camara
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
        // Valida que los campos de titulo y descripcion de la imegn no estén vacios
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleText.getText().toString();
                description = descriptionText.getText().toString();
                if (title.equals("")) {
                    titleText.setError("Required");
                } else if (description.equals("")) {
                    descriptionText.setError("Required");
                } else {
                    saveStory();
                }
            }
        });
    }

    // Enlaza con la interfaz
    private void findViewItems() {
        textAddress = findViewById(R.id.textAddress);
        image = findViewById(R.id.photo);
        camera = findViewById(R.id.btn_cam);
        descriptionText = findViewById(R.id.txt_description);
        titleText = findViewById(R.id.textTitle);
        save = findViewById(R.id.buttonStory);
    }

    /* Cuando se guarda una historia, se crea un objeto Story se llenan todos los datos, menos la imagen,
    *  y la imagen se envia por separado al ser mucha información, este proceso se mejorará futuramente
    *  al guardar estos datos en Firebase*/
    private void saveStory() {
        createImageFromBitmap(bitmap);
        Story story = new Story(title, location, description, null);
        Intent intent = new Intent(StoryActivity.this, ListStories.class);
        intent.putExtra("story", story);
        startActivity(intent);
        Toast.makeText(StoryActivity.this, "Guardado Exitosamente!", Toast.LENGTH_SHORT).show();
    }

    /* Comprime la imagen con un nombre, esto permite el envio de la imagen
       al siguiente activity ya que si se envia por un intent se exede el tamaño maximo*/
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
    // Se inicializan los datos se sesion sharedPreferences, el acelerometro y el BroadcastReceiver
    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        if (broadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(LocationManager.KEY_LOCATION_CHANGED);
            registerReceiver(broadcastReceiver, intentFilter);
        } else {
            Log.d(TAG, "broadcastReceiver is null");
        }
    }
    /* Cuando la activity esta en background se detienen las lecturas del acelerometro
       y del broadcast Receiver*/
    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        super.onRestart();
    }
    // Recupera la foto capturada con la camara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) data.getExtras().get("data");   //Captura la imagen obtenida de la camara
        image.setImageBitmap(bitmap);
    }

    // Permisos para usar la Localizacion
    public boolean checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        return true;
    }
    // si los permisos son concedidos, inicia el servicio de GPS de broadcastReceiber
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // Permiso concedido
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                        broadcastReceiver.initGPS();
                } else {    // permission denied
                    Log.d(TAG, "Location not allowed");
                }
                return;
            }
        }
    }
    // La ubicacion exacta se recupera con un objeto storyActivityInf desde el LocationBroadcastReceiber
    private StoryActivityInf storyActivityInf = new StoryActivityInf() {
        @Override
        public void DisplayLocationChange(String address) {
            Log.d(TAG, "Mi ubicacion: " + address);
            textAddress.setText(address);
            location = address;
        }
    };
}
