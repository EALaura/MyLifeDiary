package com.sya.mylifediary.Controlador.Services.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SendReceiveChat extends Thread{
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private Handler handler;
    static final int STATE_MESSAGE_RECEIVED = 5;

    public SendReceiveChat(BluetoothSocket socket, Handler handler){
        bluetoothSocket = socket;
        this.handler = handler;
        InputStream tempIn = null;
        OutputStream tempOut = null;
        try {
            tempIn = bluetoothSocket.getInputStream();
            tempOut = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStream = tempIn;
        outputStream =  tempOut;
    }

    public void run(){
        byte [] buffer = new byte[1024];
        int bytes;
        while (true){
            try {
                bytes = inputStream.read(buffer); // contiene el mensaje
                handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte [] bytes){
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}