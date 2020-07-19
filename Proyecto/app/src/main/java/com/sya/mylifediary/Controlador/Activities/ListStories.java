package com.sya.mylifediary.Controlador.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sya.mylifediary.Controlador.Adapter.StoryAdapter;
import com.sya.mylifediary.Controlador.Services.Acelerometro.Acelerometro;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;
import java.util.ArrayList;
import java.util.List;

/* Es la clase que muestra la lista circular de historias */
public class ListStories extends AppCompatActivity {
    private HorizontalInfiniteCycleViewPager viewpager;
    private List<Story> stories = new ArrayList<>();
    private Acelerometro acelerometro;
    private SharedPreferences sharedPreferences;
    // Variables de firebase
    private DatabaseReference rootRef;
    private DatabaseReference yourRef;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Verifica los datos de sesion y del aceleracion:
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        acelerometro = new Acelerometro(this, sharedPreferences);   //Se agrega el acelerometro
        viewpager = findViewById(R.id.view);
        retrieveData();
    }

    // crear nuevos object storys desde la bd de Firebase y mandarlos al Adapter
    private void retrieveData() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        yourRef = rootRef.child("Story");
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Story story = new Story();
                    story.setTitle(ds.child("title").getValue(String.class));
                    story.setLocation(ds.child("location").getValue(String.class));
                    story.setDescription(ds.child("description").getValue(String.class));
                    story.setImageAddress( ds.child("imageAddress").getValue(String.class));
                    stories.add(story);
                }
                StoryAdapter adapter = new StoryAdapter(stories, ListStories.this); // creacion del adapter
                viewpager.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        yourRef.addListenerForSingleValueEvent(eventListener);
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
}
