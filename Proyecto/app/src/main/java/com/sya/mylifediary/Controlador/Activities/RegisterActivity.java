package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sya.mylifediary.Model.User;
import com.sya.mylifediary.R;

/* Es la Activity para el registro del usuario */
public class RegisterActivity extends AppCompatActivity {
    public Button btnRegister;
    public Button btnCancel;
    private EditText username, name, email, password1, password2;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewItems();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("User");
        user = new User();
        implementListeners();
    }

    //Enlazar con la interfaz
    private void findViewItems() {
        btnRegister = findViewById(R.id.buttonSave);
        btnCancel = findViewById(R.id.buttonCancel);
        username = findViewById(R.id.view_reg_user);
        name = findViewById(R.id.view_reg_nom);
        email = findViewById(R.id.view_reg_corr);
        password1 = findViewById(R.id.view_reg_pass);
        password2 = findViewById(R.id.view_reg_rpass);
    }

    // Obtener valores
    private void getValues(){
        user.setUsername(username.getText().toString());
        user.setName(name.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword1(password1.getText().toString());
        user.setPassword2(password2.getText().toString());
    }

    private void implementListeners() {
        // Cuando se registre se le redirige al login de la aplicación
        final Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        getValues();
                        ref.child(user.getUsername()).setValue(user);
                        Toast.makeText(RegisterActivity.this, "Usuario Registrado exitosamente, Inicie Sesión", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
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
