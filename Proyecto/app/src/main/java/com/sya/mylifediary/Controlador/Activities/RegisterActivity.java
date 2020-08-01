package com.sya.mylifediary.Controlador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.sya.mylifediary.Controlador.Services.Firebase.FirebaseService;
import com.sya.mylifediary.Controlador.Services.LightSensor.LightSensor;
import com.sya.mylifediary.Model.User;
import com.sya.mylifediary.R;

/* Es la Activity para el registro del usuario */
public class RegisterActivity extends AppCompatActivity {
    private Button btnRegister;
    private Button btnCancel;
    private LinearLayout regiView;
    private EditText username, name, email, password1, password2;
    private String username_, name_, email_, password1_, password2_;
    private FirebaseService service;
    private ProgressDialog loading;
    private Intent intent;
    private User user;
    //Sensor de Luz
    private LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewItems();
        lightSensor = new LightSensor(this, regiView);
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        service = new FirebaseService();    // instancia para servicio de Firebase
        intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
        regiView = findViewById(R.id.regiView);
    }

    private void implementListeners() {
        // Cuando se registre o se cancele se le redirige al login de la aplicación
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();
                if (validateFields())
                    saveUser();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    // Obtener valores
    private void getValues() {
        username_ = username.getText().toString();
        name_ = name.getText().toString();
        email_ = email.getText().toString();
        password1_ = password1.getText().toString();
        password2_ = password2.getText().toString();
    }

    // Valida todos los datos
    private boolean validateFields() {
        if (username_.isEmpty() || name_.isEmpty() || email_.isEmpty() ||
                password1_.isEmpty() || password2_.isEmpty()) {
            Toast.makeText(this, "No pueden haber campos vacíos", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password1_.length() < 6 || password2_.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener 6 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password1_.equals(password2_)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    // Guarda al nuevo usuario
    private void saveUser() {
        loading.setMessage("Guardando Usuario");
        loading.show();
        service.getReferenceAuth().createUserWithEmailAndPassword(email_, password1_)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User();
                            setData(user);
                            service.saveUser(user);
                            loading.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registrado Exitosamente!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                loading.dismiss();
                            Toast.makeText(RegisterActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Llenar el modelo User con los datos validados
    public void setData(User user) {
        user.setUsername(username_);
        user.setName(name_);
        user.setEmail(email_);
        user.setPassword(password1_);
    }

    // Cuando la activity esta en background se detienen las lecturas de los sensores
    @Override
    protected void onPause() {
        lightSensor.getSensorManager().unregisterListener(lightSensor);
        super.onPause();
    }

    // Cuando el activity se retoma se retoman las lecturas
    @Override
    protected void onRestart() {
        lightSensor.iniciarSensor();
        super.onRestart();
    }
}
