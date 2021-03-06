package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.core.content.FileProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Firebase.FirebaseService;
import com.sya.mylifediary.Controlador.Services.LightSensor.LightSensor;
import com.sya.mylifediary.Controlador.Services.Location.LocationBroadcastReceiver;
import com.sya.mylifediary.Controlador.Services.Location.StoryActivityInf;
import com.sya.mylifediary.Controlador.Utils.Util;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;
import java.io.File;

/* Es la Activity para la creación de la historia, el usuario puede tomar una foto,
 *  añadir un titulo, una descripcion, la ubicación se obtiene por medio de
 *  LocationBroadcastReceiver que recibe la posicion exacta del usuario */
public class StoryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Acelerometro acelerometro;
    private LightSensor lightSensor;
    private static final String TAG = "MainActivity";
    private LocationBroadcastReceiver broadcastReceiver;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private LinearLayout storyView;
    private TextView textAddress;
    private ImageView image;
    private EditText titleText, descriptionText;
    private Button camera, gallary, save;
    private String title, description;
    private double latitude_, longitude_;
    private Bitmap bitmap;
    private ProgressDialog loading;
    private Story story;
    private Uri uriImg;
    private FirebaseService service;
    private final int CODE_CAMERA = 0;
    private final int CODE_GALLARY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // inicializa el braodcastReceiber
        broadcastReceiver = new LocationBroadcastReceiver(storyActivityInf, this);
        checkLocationPermission();  // Verifica los permisos de ubicación
        findViewItems();
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        story = new Story();
        service = new FirebaseService();    // instancia para servicio de Firebase
        implementsListeners();
    }

    // Implementar los listeners de los dos botones
    private void implementsListeners() {
        // Con intent implicito inicia el servicio de camara y se recupera la imagen
        camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                File file = new File(getApplicationContext().getExternalFilesDir(null), "story.jpg");
                uriImg = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImg);
                startActivityForResult(intent, CODE_CAMERA);
            }
        });
        //Permite abrir la galería y seleccionar una foto de alli
        gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CODE_GALLARY);
            }
        });
        // Valida que los campos de titulo y descripcion de la imagen no estén vacios
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
        gallary = findViewById(R.id.btn_up);
        descriptionText = findViewById(R.id.txt_description);
        titleText = findViewById(R.id.textTitle);
        save = findViewById(R.id.buttonStory);
        storyView = findViewById(R.id.storyView);
    }

    // Obtener valores de los campos
    private void getValues() {
        story.setTitle(titleText.getText().toString());
        story.setLocation(textAddress.getText().toString());
        story.setDescription(descriptionText.getText().toString());
        story.setLatitude(latitude_);
        story.setLongitude(longitude_);
    }

    /* Cuando se guarda una historia, la imagen se guarda en la carpeta Storys_img en el storage
     *  se recupera la url del storage para enlazar la imagen a la ruta ImageUrl del modelo Story
     *  Finalmente se almacena la historia completa en database y se lanza la siguiente actividad */
    private void saveStory() {
        loading.setTitle("Subiendo Historia");
        loading.setMessage("Espere por favor");
        loading.show();
        getValues();
        service.initReferences().putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // procesar el archivo para el storage
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUri = uriTask.getResult();
                story.setImageAddress(downloadUri.toString());
                // insertar en firebase
                service.saveStory(story);
                loading.dismiss();
                Toast.makeText(StoryActivity.this, "Guardado Exitosamente!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StoryActivity.this, HomeActivity.class));
            }
        });
    }

    // Se inicializan los datos se sesion sharedPreferences, el acelerometro y el BroadcastReceiver
    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        lightSensor = new LightSensor(this, storyView); //Se agrega el sensor de Luz
        if (broadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(LocationManager.KEY_LOCATION_CHANGED);
            registerReceiver(broadcastReceiver, intentFilter);
        } else {
            Log.d(TAG, "broadcastReceiver is null");
        }
    }

    /* Cuando la activity esta en background se detienen las lecturas del acelerometro,
        sensor de Luz y del broadcast Receiver*/
    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        lightSensor.getSensorManager().unregisterListener(lightSensor);
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        lightSensor.iniciarSensor();
        super.onRestart();
    }

    // Recupera la foto subida de la galería o capturada con la camara
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_CAMERA:
                bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/story.jpg");
                image.setImageBitmap(bitmap);
                Util.enableButton(save, this);
                break;
            case CODE_GALLARY:
                if (data != null) {   // Si el usuario prefiere sacar una foto
                    uriImg = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImg);
                        image.setImageBitmap(bitmap);
                        Util.enableButton(save, this);
                    } catch (Exception e) {
                        Log.e("Error", "Exception", e);
                    }
                }
                break;
        }
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // Permiso concedido
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                        broadcastReceiver.initGPS();
                } else     // permission denied
                    Log.d(TAG, "Location not allowed");
                return;
            }
        }
    }

    // La ubicacion exacta se recupera con un objeto storyActivityInf desde el LocationBroadcastReceiber
    private StoryActivityInf storyActivityInf = new StoryActivityInf() {
        @Override
        public void DisplayLocationChange(String address, double latitude, double longitude) {
            textAddress.setText(address);
            latitude_ = latitude;
            longitude_ = longitude;
        }
    };
}
