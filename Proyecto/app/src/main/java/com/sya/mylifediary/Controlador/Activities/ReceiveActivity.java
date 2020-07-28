package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Bluetooth.SendReceiveImage;
import com.sya.mylifediary.Controlador.Services.Bluetooth.ServerClassImage;
import com.sya.mylifediary.R;

/* Es la Activity que recibirá la imagen de la Historia que otro dispositivo
*  le comparte por medio de Bluetooth, la imagen previa será reemplaza por la historia cuando
*  se reciba, el proceso de envio demora aproximadamente un minuto */
public class ReceiveActivity extends AppCompatActivity {
    public Acelerometro acelerometro;
    private SharedPreferences sharedPreferences;
    public Button listen, download;
    public TextView status;
    public ImageView canvas;
    public BluetoothAdapter bluetoothAdapter;
    public SendReceiveImage sendReceive;
    // variables para el Handler
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final String APP_NAME = "MyLifeDiary";   // Nombre de la app
    private static final UUID MY_UUID = UUID.fromString("19b29419-3b3e-4d87-aefd-2488b6e8dd3b");    // Codigo unico de app
    // Variables para permisos
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // sharedPreferences para datos de sesion y acelerometro
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        findViewItems();

        // Permisos de lectura y escritura
        verifyStoragePermissions(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Si Bluetooth esta desactivado pedirá permisos para activarlo
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        implementListeners();
    }

    // Funcionalidad de los botones
    private void implementListeners() {
        // Al hacer click en listen se inicia el proceso Servidor para abrir la conexion
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClassImage serverClass = new ServerClassImage(handler, sendReceive, bluetoothAdapter, APP_NAME, MY_UUID);
                serverClass.start();
            }
        });
        // Descargar la imagen
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable)canvas.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byteArray = stream.toByteArray();
                saveImage(byteArray);
            }
        });
    }

    // Enlazar con la interfaz
    private void findViewItems() {
        listen = findViewById(R.id.buttonListen);
        download = findViewById(R.id.buttonDownload);
        canvas = findViewById(R.id.image);
        status = findViewById(R.id.txtstatus);
    }
    // Cuando la activity esta en background se detienen las lecturas del acelerometro
    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        super.onPause();
    }
    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        super.onRestart();
    }

    // Método para guardar la imagen en una carpeta propia de la app y mostrarla en la galería del celular
    private void saveImage(byte[] byteArray) {
        // Guardar la imagen JPEG en external storage
        FileOutputStream outStream;
        try {
            // Crear Fichero
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/My Life Diary/");
            //Verifica si el directorio esta creado
            if(!path.exists()){
                path.mkdirs();
            }
            // Se pone como nombre de la foto la fecha y hora actual para que sea único
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = timeStamp + ".jpg";
            File outputFile = new File(path, imageFileName);
            String imagePath =  outputFile.getAbsolutePath();

            //Escanea la imagen para mostrarla en album
            MediaScannerConnection.scanFile(this,
                    new String[] { imagePath }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {  }
                    });
            Toast.makeText(getApplicationContext(), "Imagen Guardada!", Toast.LENGTH_SHORT).show();
            outStream = new FileOutputStream(outputFile);
            outStream.write(byteArray);
            outStream.close();
            // mostrar un log con la ruta de la imagen
            Log.d("Error", "path:" + path.toString());
        // Mostrar errores al guardar la imagen
        } catch (FileNotFoundException e) {
            Log.e("Error", "File Not Found", e);
        } catch (IOException e) {
            Log.e("Error", "IO Exception", e);
        }
    }

    // Estados de acuerdo a la conexion, cuando se recibe la imagen se visualiza en el canvas
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_CONNECTING:
                    status.setText("Conectando");
                    break;
                case STATE_CONNECTED:
                    status.setText("Conectado");
                    break;
                case STATE_CONECTION_FAILED:
                    status.setText("Error");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    status.setText("Recibido");
                    byte[] readBuffer = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(readBuffer, 0, msg.arg1);
                    canvas.setImageBitmap(bitmap);
                    break;
            }
            return true;
        }
    });

    // Verificar los permisos de escritura
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Si no se tiene permisos, pedirlo al usuario
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
