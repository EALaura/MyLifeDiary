package com.sya.mylifediary.Controlador.Services.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* Es la clase que maneja el intercambio de mensajes entre dos
*  moviles para el chat, recibe informacion por InputStream y manda información
*  por OutputStream */
public class SendReceiveChat extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private Handler handler;
    static final int STATE_MESSAGE_RECEIVED = 5;

    /* Constructor, se recibe el socket de coneccion y en handler
    *  para fijar el estado */
    public SendReceiveChat(BluetoothSocket socket, Handler handler) {
        bluetoothSocket = socket;
        this.handler = handler;
        InputStream tempIn = null;
        OutputStream tempOut = null;
        try {
            tempIn = bluetoothSocket.getInputStream();  // inicializar
            tempOut = bluetoothSocket.getOutputStream();    // inicializar
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStream = tempIn;
        outputStream = tempOut;
    }

    /* Ejecución del proceso mientras se reciba un mensaje */
    public void run() {
        byte[] buffer = new byte[1024]; // tamaño del bufer de mensajes
        int bytes;
        while (true) {
            try {
                bytes = inputStream.read(buffer); // contiene el mensaje y esta como recibido
                handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Metodo write para escribir el mensaje de salida */
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}