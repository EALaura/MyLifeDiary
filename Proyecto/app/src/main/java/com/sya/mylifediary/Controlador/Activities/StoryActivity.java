package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Firebase.FirebaseService;
import com.sya.mylifediary.Controlador.Services.Location.LocationBroadcastReceiver;
import com.sya.mylifediary.Controlador.Services.Location.StoryActivityInf;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;
import java.io.File;

/* Es la Activity para la creación de la historia, el usuario puede tomar una foto,
*  añadir un titulo, una descripcion, la ubicación se obtiene por medio de
*  LocationBroadcastReceiver que recibe la posicion exacta del usuario */
public class StoryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Acelerometro acelerometro;
    private static final String TAG = "MainActivity";
    private LocationBroadcastReceiver broadcastReceiver;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private TextView textAddress;
    private ImageView image;
    private EditText titleText, descriptionText;
    private Button camera, save;
    private String title, location, description;
    private double latitude_, longitude_;
    private Bitmap bitmap;
    private ProgressDialog loading;
    private FirebaseAuth mAuth;
    private Story story;
    private Uri uriImg;
    private FirebaseService service;
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
        story = new Story();
        service = new FirebaseService();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
        } else {
            signInAnonymously();
        }
        implementsListeners();
    }

    // Permite a la Aplicacion usar Firebase de forma anonima para hacer las pruebas de almacenamiento
    private void signInAnonymously(){
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override public void onSuccess(AuthResult authResult) {
            }
        }) .addOnFailureListener(this, new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception exception) {
                Log.e("TAG", "signInAnonymously:FAILURE", exception);
            }
        });
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
                startActivityForResult(intent, 0);
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
        descriptionText = findViewById(R.id.txt_description);
        titleText = findViewById(R.id.textTitle);
        save = findViewById(R.id.buttonStory);
    }

    // Obtener valores de los campos
    private void getValues(){
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
        final Intent intent = new Intent(StoryActivity.this, HomeActivity.class);

        service.initReferences().putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // procesar el archivo para el storage
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                story.setImageAddress(downloadUri.toString());
                // insertar en firebase
                service.save(story);
                loading.dismiss();
                Toast.makeText(StoryActivity.this, "Guardado Exitosamente!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
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
        bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/story.jpg");
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
            Log.d(TAG, "Mi ubicacion: " + address);
            textAddress.setText(address);
            location = address;
            latitude_ = latitude;
            longitude_ = longitude;
        }
    };
}
