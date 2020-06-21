package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.sya.mylifediary.Controlador.Utils.Util;
import com.sya.mylifediary.R;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //flechita para atras
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);  //poner el icono en la primera vista

        /*<activity android:name=".SecondActivity" android:parentActivityName=".MainActivity" >
            <meta-data android:name="android.support.PARENT_ACTIVITY" android:value=".MainActivity"></meta-data>
        </activity>*/

        // busca el ya creado en el login x el mismo nombre string
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
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
                Util.removeSharedPreferences(sharedPreferences);
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
