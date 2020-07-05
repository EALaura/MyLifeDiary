package com.sya.mylifediary.Controlador.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.sya.mylifediary.Controlador.Adapter.StoryAdapter;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ListStories extends AppCompatActivity {
    public HorizontalInfiniteCycleViewPager viewpager;
    public List<Story> stories = new ArrayList<>();
    public Acelerometro acelerometro;
    public Bitmap bitmap;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro

        Story myStory = (Story) getIntent().getSerializableExtra("story");
        try {
            bitmap = BitmapFactory.decodeStream(this.openFileInput("photo"));
            myStory.setPhoto(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        stories.add(myStory);
        initData();
        viewpager = findViewById(R.id.view);
        StoryAdapter adapter = new StoryAdapter(stories, this);
        viewpager.setAdapter(adapter);
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

    // Inicializa 3 historias precargadas en la aplicacion
    private void initData() {
        Bitmap icon1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.img1);
        Bitmap icon2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.img2);
        Bitmap icon3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.img3);
        stories.add(new Story("Playa", "Cuba", "El gran lugar de mis vacaciones soñadas.", icon1));
        stories.add(new Story("Hotel", "Puerto Rico", "Buenos momentos en familia.", icon2));
        stories.add(new Story("Aeropuerto", "Peru", "Regreso el siguiente mes.", icon3));
    }
}
