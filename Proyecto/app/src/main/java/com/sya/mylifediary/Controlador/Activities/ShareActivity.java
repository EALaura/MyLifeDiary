package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Bluetooth.SendReceiveImage;
import com.sya.mylifediary.Controlador.Services.LightSensor.LightSensor;
import com.sya.mylifediary.Controlador.Utils.Util;
import com.sya.mylifediary.R;

/* Esta Activity se activa cuando el usuario hace click en el icono de Bluetooth
*  en la lista, la imagen de su historia puede ser compartida por Bluetooth */
public class ShareActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    public Acelerometro acelerometro;
    private LightSensor lightSensor;
    private LinearLayout shareView;
    public Button listDevices, send;
    public ListView listView;
    public TextView status;
    public ImageView canvas;
    public Bitmap bitmap;
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice[] btArray; // contiene todos los dispositivos
    public SendReceiveImage sendReceive;
    // variables para el Handler
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONECTION_FAILED = 4;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final UUID MY_UUID = UUID.fromString("19b29419-3b3e-4d87-aefd-2488b6e8dd3b");    // ID de identificacion unico

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // datos de sharedPreferences para la sesion y acelerometro
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        findViewItems();
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        lightSensor = new LightSensor(this, shareView); // Se agrega el sensor de Luz
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Si el bluetooth esta desactivado se le pide que active
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        try {
            bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            canvas.setImageBitmap(bitmap);  // la imagen recibida de ListActivity se muestra en el canvas
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        implementListeners();
    }

    // Enlaza con la interfaz
    private void findViewItems() {
        send = findViewById(R.id.buttonSend);
        listDevices = findViewById(R.id.buttonDevices);
        listView = findViewById(R.id.listDevices);
        canvas = findViewById(R.id.image);
        status = findViewById(R.id.txtstatus);
        shareView = findViewById(R.id.shareView);
    }
    // Cuando la activity esta en background se detienen las lecturas de los Sensores
    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        lightSensor.getSensorManager().unregisterListener(lightSensor);
        super.onPause();
    }
    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        lightSensor.iniciarSensor();
        super.onRestart();
    }

    // Estados de acuerdo a la conexion
    Handler handler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_CONNECTING:
                    status.setText("Conectando");
                    break;
                case STATE_CONNECTED:
                    status.setText("Conectado");
                    listView.setVisibility(View.GONE);
                    Util.enableButton(send, ShareActivity.this);
                    break;
                case STATE_CONECTION_FAILED:
                    status.setText("Error");
                    break;
            }
            return true;
        }
    });

    // Funcionalidades de los botones
    private void implementListeners() {

        /* Se obtiene una lista de los dispositivos enlazados
         *  se copia los nombre de los dispositivos en una lista
         *  para mostrarle al usuario con un ArrayAdapter basico de String */
        listDevices.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Util.disableButton(listDevices, ShareActivity.this);
                Toast.makeText(ShareActivity.this, "Seleccione el dispositivo a conectarse", Toast.LENGTH_SHORT).show();
                Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
                String[] devices = new String[bluetoothDevices.size()];
                btArray = new BluetoothDevice[bluetoothDevices.size()];
                int index = 0;
                if (bluetoothDevices.size() > 0) {
                    for (BluetoothDevice device : bluetoothDevices) {
                        btArray[index] = device;
                        devices[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShareActivity.this,
                            android.R.layout.simple_list_item_1, devices);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        // Cuando se hace click sobre un nombre de la lista de dispositivos,
        // se inicia el proceso Cliente y se establece la coneccion
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ShareActivity.this, "Estableciendo conexion", Toast.LENGTH_SHORT).show();
                ClientClass clientClass = new ClientClass(btArray[position]);
                clientClass.start();
                status.setText("Conectando");
            }
        });

        /* La imagen debe comprimirse al enviar y se envía por subarrays (por partes),
         * mediante otro proceso sendReceive se escribe los datos que se van enviando por cada
         * iteración según el tamaño de la imagen, el envio dura aproximadamente un minuto */
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] imageBytes = stream.toByteArray();
                int subArraySize = 400;
                sendReceive.write(String.valueOf(imageBytes.length).getBytes());

                // para enviar los subarrays
                for (int i = 0; i < imageBytes.length; i += subArraySize) {
                    byte[] tempArray = Arrays.copyOfRange(imageBytes, i, Math.min(imageBytes.length, i + subArraySize));
                    sendReceive.write(tempArray);
                }
                status.setText("Enviado");
                Util.disableButton(send, ShareActivity.this);
                Util.enableButton(listDevices, ShareActivity.this);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    /*
     * La clase ClientClass es el proceso que se inicia con el BluetoothSocket y se inicia
     * para establecer la comunicación con su propio sendReceive y conectar con el
     * sendReceive del Server:
     */
    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device) {
            this.device = device;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();   // si la conexion es exitosa
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
                sendReceive = new SendReceiveImage(socket, handler);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();    // si ocurre un error
                Message message = Message.obtain();
                message.what = STATE_CONECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }
}
