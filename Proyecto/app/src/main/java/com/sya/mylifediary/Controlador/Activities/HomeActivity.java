package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Controlador.Services.Firebase.FirebaseService;
import com.sya.mylifediary.Controlador.Services.LightSensor.LightSensor;
import com.sya.mylifediary.Controlador.Utils.PopupInfoActivity;
import com.sya.mylifediary.Controlador.Utils.Util;
import com.sya.mylifediary.R;

/* Es la primera actividad que se muestra despues del logeo,
 *  Muestra los botones para las demás funciones de la aplicación,
 *  usa sharedPreferences para datos de sesion y un acelerometro */
public class HomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Button btnAdd, btnList, btnReceive, btnChat;
    private Acelerometro acelerometro;
    private FirebaseService service;
    //Interfaz
    private LinearLayout homeView;
    private LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);  //poner el icono en la primera vista

        // Busca los datos existentes de sesion
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        findViewItems();
        service = new FirebaseService();    // instancia para servicio de Firebase
        //Light Sensor
        lightSensor = new LightSensor(this, homeView);
        implementListeners();
    }

    //Funcionalidades de los botones
    private void implementListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // Agregar nueva historia
                Intent intent = new Intent(HomeActivity.this, StoryActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // Agregar nueva historia
                Intent intent = new Intent(HomeActivity.this, ListStories.class);
                startActivity(intent);
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // Recibir una historia
                Intent intent = new Intent(HomeActivity.this, ReceiveActivity.class);
                startActivity(intent);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // Iniciar Chat
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    // Enlazar con la interfaz
    private void findViewItems() {
        btnAdd = findViewById(R.id.buttonAdd);
        btnList = findViewById(R.id.buttonList);
        btnReceive = findViewById(R.id.buttonReceive);
        btnChat = findViewById(R.id.buttonChat);
        homeView = findViewById(R.id.homeView);
    }

    // Cuando la activity esta en background se detienen las lecturas del acelerometro
    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        lightSensor.getSensorManager().unregisterListener(lightSensor);
        super.onPause();
    }

    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        lightSensor.iniciarSensor();
        super.onRestart();
    }

    // Es la unica vista con el menu activado para Cerrar sesión
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // La opción de cerrar sesión:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_email:
                showUser();
                return true;
            case R.id.menu_about:
                showInfo();
                return true;
            case R.id.menu_logout:
                logOut();
                return true;
            case R.id.menu_ayuda:
                ayuda();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Metodo para Cerrar Sesion del Usuario, borra las sharedPreferences existentes
    private void logOut() {
        Util.removeSharedPreferences(sharedPreferences);
        service.getReferenceAuth().signOut();
        Toast.makeText(this, "Sesión cerrada Exitosamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Mostrar el correo del usuario actual logeado
    private void showUser() {
        String email = service.getReferenceAuth().getCurrentUser().getEmail();
        SuperActivityToast.create(this, new Style(), Style.TYPE_STANDARD)
                .setText("Logeado como: " + email) // MENSAJE MORADO
                .setDuration(Style.DURATION_VERY_LONG)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PINK))
                .setAnimations(Style.ANIMATIONS_POP).show();
    }

    // Mostrar un Popup de la información de la Aplicación y desarrolladores
    private void showInfo() {
        startActivity(new Intent(HomeActivity.this, PopupInfoActivity.class));
    }

    // Envia al activity de Ayuda.
    private void ayuda() {
        Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
        startActivity(intent);
    }
}
