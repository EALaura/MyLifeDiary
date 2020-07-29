package com.sya.mylifediary.Controlador.Services.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.Model.User;
import java.util.Date;

// Esta clase permite guardar la historia en Firebase y recuperar información
public class FirebaseService {
    // Variables de Firebase
    private FirebaseDatabase database;
    private DatabaseReference refStory;
    private DatabaseReference rootRef;
    private DatabaseReference yourRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference refUser;

    // Constructor
    public FirebaseService(){
        // inicializa el servicio de Firebase
        database = FirebaseDatabase.getInstance();
        refStory = database.getReference("Story");
        rootRef = FirebaseDatabase.getInstance().getReference();
        yourRef = rootRef.child("Story");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        refUser = firebaseDatabase.getReference("User");
    }

    // Inicializa las referencias para guardar la información
    public StorageReference initReferences(){
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference file = storageRef.child("Storys_img");
        final StorageReference photoRef = file.child(new Date().toString());
        return photoRef;
    }

    // La historia se guardará con el nombre del titulo de la historia
    public void saveStory(Story story){
        refStory.child("Story " + story.getTitle()).setValue(story);
    }

    // Devuelve la referencia de las historias guardadas en Firebase
    public DatabaseReference getReference(){
        return yourRef;
    }

    // Devuelve la referencia para autenticacion
    public FirebaseAuth getReferenceAuth(){
        return firebaseAuth;
    }

    // El usuario se guardará con el nombre del username
    public void saveUser(User user){
        refUser.child("User " + user.getUsername()).setValue(user);
    }
}
