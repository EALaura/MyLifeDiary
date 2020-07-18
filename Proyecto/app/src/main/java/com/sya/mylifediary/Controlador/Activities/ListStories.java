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
/* Es la clase que muestra la lista circular de historias y se muestra la mas actual primero*/
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
        // Verifica los datos de sesion y del aceleracion:
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        // recibe el objeto Story desde StoryActivity
        Story myStory = (Story) getIntent().getSerializableExtra("story");
        try {   // Como la foto es muy grande para ser enviada entre intent se recibe aquí
            bitmap = BitmapFactory.decodeStream(this.openFileInput("photo"));
            //myStory.setPhoto(bitmap);   // y se le coloca el bitmap a la historia
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Se añade la historia a la lista
        stories.add(myStory);
        //initData();
        viewpager = findViewById(R.id.view);
        StoryAdapter adapter = new StoryAdapter(stories, this); // creacion del adapter
        viewpager.setAdapter(adapter);
    }
    // Cuando la activity esta en background se detienen las lecturas del acelerometro
    @Override
    protected void onPause() {
        acelerometro.getSensorManager().unregisterListener(acelerometro);
        super.onPause();
    }
    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        acelerometro.iniciarSensor();
        super.onRestart();
    }

    // Inicializa 3 historias precargadas en la aplicacion, para que la lista no se vea vacia
    /*private void initData() {
        Bitmap icon1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.img1);
        Bitmap icon2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.img2);
        Bitmap icon3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.img3);
        stories.add(new Story("Playa", "Cuba", "El gran lugar de mis vacaciones soñadas.", icon1));
        stories.add(new Story("Hotel", "Puerto Rico", "Buenos momentos en familia.", icon2));
        stories.add(new Story("Aeropuerto", "Peru", "Regreso el siguiente mes.", icon3));
    }*/
}
