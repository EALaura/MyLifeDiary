package com.sya.mylifediary.Controlador.Utils;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import com.sya.mylifediary.R;

// Esta clase permite mostrar un mensaje con los datos de los desarrolladores y de contacto
public class PopupInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_info);

        DisplayMetrics metrics = new DisplayMetrics(); // obtiene tamaño de la ventana
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // Obtener ancho y alto
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.5));
    }
}
