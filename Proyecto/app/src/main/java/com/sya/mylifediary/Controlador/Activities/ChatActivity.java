package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import com.sya.mylifediary.Controlador.Services.Bluetooth.SendReceiveChat;
import com.sya.mylifediary.R;

public class ChatActivity extends AppCompatActivity {
    Button listen, listDevices, send;
    ListView listView;
    TextView box_canvas, status;
    EditText writeMsg;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice [] btArray; // contiene todos los dispositivos
    String previusChat = "", currentMsg;
    SendReceiveChat sendReceive;
    // variables para el Handler
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final String APP_NAME = "MyLifeDiary";
    private static final UUID MY_UUID = UUID.fromString("19b29419-3b3e-4d87-aefd-2488b6e8dd3b");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewItems();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        implementListeners();
    }

    private void findViewItems() {
        listen = findViewById(R.id.buttonListen);
        send = findViewById(R.id.buttonSend);
        listDevices = findViewById(R.id.buttonDevices);
        listView = findViewById(R.id.listDevices);
        box_canvas = findViewById(R.id.canvas);
        status = findViewById(R.id.txtstatus);
        writeMsg = findViewById(R.id.editText);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case STATE_LISTENING:
                    status.setText("Escuchando"); break;
                case STATE_CONNECTING:
                    status.setText("Conectando"); break;
                case STATE_CONNECTED:
                    status.setText("Conectado"); break;
                case STATE_CONECTION_FAILED:
                    status.setText("Error"); break;
                case STATE_MESSAGE_RECEIVED:
                    byte [] readBuffer = (byte[]) msg.obj;
                    String tempMsg = new String(readBuffer, 0, msg.arg1);
                    previusChat = previusChat + tempMsg;
                    box_canvas.setText(previusChat);
                    break;
            }
            return true;
        }
    });

    private void implementListeners() {
        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
                String [] devices = new String[bluetoothDevices.size()];
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

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientClass clientClass = new ClientClass(btArray[position]);
                clientClass.start();
                status.setText("Conectando");
            }
        });

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

    private class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;
        public ServerClass(){
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;
            while(socket == null){
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
                if (socket != null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    // write some code for send / receive
                    sendReceive = new SendReceiveChat(socket, handler);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device){
            this.device = device;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run(){
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
                sendReceive = new SendReceiveChat(socket, handler);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }
}