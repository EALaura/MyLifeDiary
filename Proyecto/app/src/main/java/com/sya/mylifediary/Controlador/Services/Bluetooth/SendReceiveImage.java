package com.sya.mylifediary.Controlador.Services.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SendReceiveImage extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private Handler handler;
    static final int STATE_MESSAGE_RECEIVED = 5;

    // Constructor
    public SendReceiveImage(BluetoothSocket socket, Handler handler) {
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
        outputStream = tempOut;
    }

    public void run() {
        byte[] buffer = null;
        int numerOfBytes = 0;
        int index = 0;
        boolean flag = true;

        while (true) {
            if (flag) {
                try {
                    byte[] temp = new byte[inputStream.available()];
                    if (inputStream.read(temp) > 0) {
                        numerOfBytes = Integer.parseInt(new String(temp, "UTF-8"));
                        buffer = new byte[numerOfBytes];
                        flag = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    byte[] data = new byte[inputStream.available()];
                    int numbers = inputStream.read(data);

                    System.arraycopy(data, 0, buffer, index, numbers);
                    index = index + numbers;

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

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}