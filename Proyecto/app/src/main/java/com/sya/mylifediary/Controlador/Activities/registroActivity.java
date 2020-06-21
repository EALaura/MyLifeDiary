package com.sya.mylifediary.Controlador.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sya.mylifediary.R;

public class registroActivity extends AppCompatActivity {
    //Botones
    Button btn_regis;
    Button btn_can;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        //Inicializar
        btn_regis = (Button)findViewById(R.id.btn_regi);
        btn_can = (Button)findViewById(R.id.btn_can);

        //Registrarse en la base de datos
        btn_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText("Su Usuario esta registrandose, vuelva a iniciar Sesión",);
            }
        });

        //Boton Cancelar redirige a la pantalla de Inicio
        btn_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(registroActivity.this, MainActivity.class);
                startActivity(login);
            }
        });
    }
}
