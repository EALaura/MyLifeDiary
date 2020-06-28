package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Utils.Util;
import com.sya.mylifediary.R;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    Button btnAdd, btnList, btnReceive, btnChat;
    Acelerometro acelerometro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //flechita para atras
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);  //poner el icono en la primera vista

        // busca el ya creado en el login x el mismo nombre string
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        btnAdd = findViewById(R.id.buttonAdd);
        btnReceive = findViewById(R.id.buttonReceive);
        btnChat = findViewById(R.id.buttonChat);
        btnList = findViewById(R.id.buttonList);

        //Se agrega el acelerometro
        acelerometro = new Acelerometro(this, sharedPreferences);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StoryActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(HomeActivity.this, ListStories.class);
                startActivity(intent);*/
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReceiveActivity.class);
                startActivity(intent);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut(){
        Util.removeSharedPreferences(sharedPreferences);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
