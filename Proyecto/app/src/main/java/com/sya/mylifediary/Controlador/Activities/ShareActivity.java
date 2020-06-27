package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import com.sya.mylifediary.Controlador.Services.Bluetooth.SendReceiveImage;
import com.sya.mylifediary.R;

public class ShareActivity extends AppCompatActivity {
    Button listDevices, send;
    ListView listView;
    TextView status;
    ImageView canvas;
    Bitmap bitmap;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice [] btArray; // contiene todos los dispositivos
    SendReceiveImage sendReceive;
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
        setContentView(R.layout.activity_share);

        findViewItems();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        try {
            bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            canvas.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        implementListeners();
    }

    private void findViewItems() {
        send = findViewById(R.id.buttonSend);
        listDevices = findViewById(R.id.buttonDevices);
        listView = findViewById(R.id.listDevices);
        canvas = findViewById(R.id.image);
        status = findViewById(R.id.txtstatus);
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
                    Bitmap bitmap =  BitmapFactory.decodeByteArray(readBuffer, 0, msg.arg1);
                    canvas.setImageBitmap(bitmap);
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShareActivity.this,
                            android.R.layout.simple_list_item_1, devices);
                    listView.setAdapter(arrayAdapter);
                }
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte [] imageBytes = stream.toByteArray();

                int subArraySize = 400;
                sendReceive.write(String.valueOf(imageBytes.length).getBytes());
                // para enviar los subarrays
                for(int i = 0; i < imageBytes.length; i += subArraySize){
                    byte [] tempArray = Arrays.copyOfRange(imageBytes, i, Math.min(imageBytes.length, i + subArraySize));
                    sendReceive.write(tempArray);
                }
            }
        });
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
                sendReceive = new SendReceiveImage(socket, handler);
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
