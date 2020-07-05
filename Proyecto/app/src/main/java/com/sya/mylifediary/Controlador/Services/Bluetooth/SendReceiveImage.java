package com.sya.mylifediary.Controlador.Services.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* Es la clase que maneja el intercambio de historias como imagen entre dos
 *  moviles, recibe informacion por InputStream y manda la historia por OutputStream */
public class SendReceiveImage extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private Handler handler;
    static final int STATE_MESSAGE_RECEIVED = 5;

    // Constructor, recibe el socket de conexion y el handler para el status
    public SendReceiveImage(BluetoothSocket socket, Handler handler) {
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

    /* Ejecución del proceso mientras se reciba la imagen, se maneja el envio con un flag,
    *  como se tranfiere una imagen la imagen se fracciona en subarrays hasta recibir la imagen completa */
    public void run() {
        byte[] buffer = null;
        int numerOfBytes = 0;
        int index = 0;
        boolean flag = true;

        while (true) {
            if (flag) { // si aun quedan datos por recibir
                try {
                    byte[] temp = new byte[inputStream.available()];    // se recibe el array
                    if (inputStream.read(temp) > 0) {
                        numerOfBytes = Integer.parseInt(new String(temp, "UTF-8"));
                        buffer = new byte[numerOfBytes];
                        flag = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {    // ya se recibio la imagen completa
                try {
                    byte[] data = new byte[inputStream.available()];
                    int numbers = inputStream.read(data);
                    // se copiara toda la informacion en el array
                    System.arraycopy(data, 0, buffer, index, numbers);
                    index = index + numbers;
                    // el status será recibido
                    if (index == numerOfBytes) {
                        handler.obtainMessage(STATE_MESSAGE_RECEIVED, numerOfBytes, -1, buffer).sendToTarget();
                        flag = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* El metodo write para enviar la imagen de salida */
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}