package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Bluetooth.SendReceiveChat;
import com.sya.mylifediary.Controlador.Services.LightSensor.LightSensor;
import com.sya.mylifediary.Controlador.Utils.Util;
import com.sya.mylifediary.R;

/* La clase ChatActivity permite el intercambio de mensajes entre dos moviles,
 *  ambos debes inicializar los procesos de Cliente y Servidor a la vez, establecen un socket
 *  de conexion y permite el intercambio de mensajes por un objeto sendReceiveChat*/
public class ChatActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    public Button listen, listDevices, send;
    public ListView listView;
    public TextView box_canvas, status;
    public EditText writeMsg;
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice[] btArray; // contiene todos los dispositivos
    public String previusChat = "", currentMsg;
    public SendReceiveChat sendReceive;
    public Acelerometro acelerometro;
    //Interfaz
    private LightSensor lightSensor;
    private LinearLayout chatView;
    // variables para el Handler
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final String APP_NAME = "MyLifeDiary";   // nombre de la aplicacion
    private static final UUID MY_UUID = UUID.fromString("19b29419-3b3e-4d87-aefd-2488b6e8dd3b");    // ID de identificacion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        findViewItems();
        lightSensor = new LightSensor(this, chatView);
        // inicializa el Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // si Bluetooth no está activado pedirá permiso al usuario
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        implementListeners();
    }

    //Enlazar con la interfaz
    private void findViewItems() {
        listen = findViewById(R.id.buttonListen);
        send = findViewById(R.id.buttonSend);
        listDevices = findViewById(R.id.buttonDevices);
        listView = findViewById(R.id.listDevices);
        box_canvas = findViewById(R.id.canvas);
        status = findViewById(R.id.txtstatus);
        writeMsg = findViewById(R.id.editText);
        chatView = findViewById(R.id.chatView);

    }

    // Cuando la activity esta en background se detienen las lecturas del acelerometro
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    /* Se definen los diferentes estados del handler al establecer la conexion
       y cuando se recibe el mensaje se establece en el canvas para visualizarlo */
    Handler handler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_CONNECTING:
                    Util.disableButton(listen, ChatActivity.this);
                    Util.disableButton(listDevices, ChatActivity.this);
                    Toast.makeText(ChatActivity.this, "Conexion abierta, esperando dispositivos", Toast.LENGTH_LONG).show();
                    status.setText("Conectando");
                    break;
                case STATE_CONNECTED:
                    status.setText("Conectado");
                    Util.enableButton(send, ChatActivity.this);
                    listView.setVisibility(View.GONE);
                    break;
                case STATE_CONECTION_FAILED:
                    status.setText("Error");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    status.setText("Chateando");
                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempMsg = new String(readBuffer, 0, msg.arg1);
                    previusChat = previusChat + tempMsg;
                    box_canvas.setText(previusChat);
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
                Util.disableButton(listen, ChatActivity.this);
                Util.disableButton(listDevices, ChatActivity.this);
                Toast.makeText(ChatActivity.this, "Seleccione el dispositivo a conectarse", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChatActivity.this,
                            android.R.layout.simple_list_item_1, devices);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
        /* Cuando se hace click en lister se inicializa el proceso de BluetoothServerSocket */
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
            }
        });
        // Cuando se muestra la lista establece un Client que se conectara al server
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ChatActivity.this, "Estableciendo conexion", Toast.LENGTH_SHORT).show();
                ClientClass clientClass = new ClientClass(btArray[position]);
                clientClass.start();
                status.setText("Conectando");
            }
        });
        // Cuando se envia un emnsaje se muestra en el canvas y con writeMsg se escribe el mensaje a enviar
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_message = String.valueOf(writeMsg.getText());
                currentMsg = bluetoothAdapter.getName() + ": " + str_message + "\n";
                previusChat = previusChat + currentMsg;
                box_canvas.setText(previusChat);
                sendReceive.write(currentMsg.getBytes());
                writeMsg.setText("");
            }
        });
    }

    /* Es la clase servidor que implementa el BluetoothServerSocket
     *  para abrir la conexion, maneja los estados con handler */
    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Se estableceran los diferentes estados dependiendo de la conexion
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONECTION_FAILED;
                    handler.sendMessage(message);
                }
                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    // Se escribe el mensaje recibido
                    sendReceive = new SendReceiveChat(socket, handler);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    // La clase cleinte establece un BluetoothSocket para la conexion con el servidor
    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        // Recibe el dispositivo y se conecta con el que compartta el ID de identificacion de app
        public ClientClass(BluetoothDevice device) {
            this.device = device;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {   // cuando haya conexion de establece como conectado
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
                sendReceive = new SendReceiveChat(socket, handler);
                sendReceive.start();    // esta listo para enviar mensajes

            } catch (IOException e) {   // si hay algun error
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }
}