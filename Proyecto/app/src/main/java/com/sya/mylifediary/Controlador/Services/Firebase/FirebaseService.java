package com.sya.mylifediary.Controlador.Services.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sya.mylifediary.Model.Story;
import java.util.Date;

// Esta clase permite guardar la historia en Firebase y recuperar información
public class FirebaseService {
    // Variables de Firebase
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference rootRef;
    private DatabaseReference yourRef;

    // Constructor
    public FirebaseService(){
        // inicializa el servicio de Firebase
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Story");
        rootRef = FirebaseDatabase.getInstance().getReference();
        yourRef = rootRef.child("Story");
    }

    // Inicializa las referencias para guardar la información
    public StorageReference initReferences(){
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference file = storageRef.child("Storys_img");
        final StorageReference photoRef = file.child(new Date().toString());
        return photoRef;
    }

    // La historia se guardará con el nombre del titulo de la historia
    public void save(Story story){
        ref.child("Story " + story.getTitle()).setValue(story);
    }

    // Devuelve la referencia de las historias guardadas en Firebase
    public DatabaseReference getReference(){
        return yourRef;
    }
}
