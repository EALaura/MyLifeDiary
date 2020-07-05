package com.sya.mylifediary.Controlador.Services.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

public class ServerClassImage extends Thread {
    private BluetoothServerSocket serverSocket;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONECTION_FAILED = 4;
    Handler handler;
    SendReceiveImage sendReceive;

    // Constructor
    public ServerClassImage(Handler handler, SendReceiveImage sendReceive, BluetoothAdapter bluetoothAdapter, String APP_NAME, UUID MY_UUID) {
        this.handler = handler;
        this.sendReceive = sendReceive;
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Establece la conexión del socket y el handler fijará el status como conectado,
     * una vez que esto ocurra estará listo para recibir la imagen
     */
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
                // write some code for send / receive
                sendReceive = new SendReceiveImage(socket, handler);
                sendReceive.start();
                break;
            }
        }
    }
}
