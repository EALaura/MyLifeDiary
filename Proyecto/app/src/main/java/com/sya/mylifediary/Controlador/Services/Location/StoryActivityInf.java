package com.sya.mylifediary.Controlador.Services.Location;

import java.io.Serializable;

/* Esta interfaz define el método que el StoryActicity usará para
*  obtener la ubicación del broadcasteReceiver por medio de un
*  objeto creado en el activity y enviado al broadcasteReceiver */
public interface StoryActivityInf extends Serializable {
    void DisplayLocationChange(String address, double latitude, double longitude);
}
