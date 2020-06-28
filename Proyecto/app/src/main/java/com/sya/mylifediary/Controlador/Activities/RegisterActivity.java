package com.sya.mylifediary.Controlador.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sya.mylifediary.R;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    Button btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRegister = findViewById(R.id.buttonSave);
        btnCancel = findViewById(R.id.buttonCancel);
        final Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Usuario Registrado exitosamente, Inicie Sesión", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}
