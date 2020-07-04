package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.UUID;

import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Bluetooth.SendReceiveImage;
import com.sya.mylifediary.Controlador.Services.Bluetooth.ServerClassImage;
import com.sya.mylifediary.R;

public class ReceiveActivity extends AppCompatActivity {
    Acelerometro acelerometro;
    private SharedPreferences sharedPreferences;
    Button listen;
    TextView status;
    ImageView canvas;
    BluetoothAdapter bluetoothAdapter;
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
        setContentView(R.layout.activity_receive);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        findViewItems();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClassImage serverClass = new ServerClassImage(handler, sendReceive, bluetoothAdapter, APP_NAME, MY_UUID);
                serverClass.start();
            }
        });
    }

    private void findViewItems() {
        listen = findViewById(R.id.buttonListen);
        canvas = findViewById(R.id.image);
        status = findViewById(R.id.txtstatus);
    }

    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        super.onRestart();
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
}
