package com.sya.mylifediary.Controlador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sya.mylifediary.R;

public class MainActivity extends AppCompatActivity {

    //Botones
    Button btn_ini_ses;
    Button btn_salir;
    Button btn_regi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializar
        btn_ini_ses = (Button)findViewById(R.id.btn_Ini);
        btn_salir = (Button)findViewById(R.id.btn_Sal);
        btn_regi = (Button)findViewById(R.id.btn_Reg);

        //Ir al activity principal
        btn_ini_ses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent principal = new Intent(MainActivity.this, principalActivity.class);
                startActivity(principal);
            }
        });

        //Salir de la aplicacion
        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Ir a registro
        btn_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registro = new Intent(MainActivity.this, registroActivity.class);
                startActivity(registro);
            }
        });

    }
}
