package com.sya.mylifediary.Controlador.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.sya.mylifediary.R;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText textEmail;
    private EditText textPass;
    private Switch switchRemember;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        bindUI();
        // las preferencias privadas no se comparte con otras aplicaciones
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textEmail.getText().toString();
                String password = textPass.getText().toString();
                if(logIn(email, password)) {
                    goToHome();
                    saveOnPreferences(email, password);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindUI(){
        textEmail = findViewById(R.id.textEmail);
        textPass = findViewById(R.id.textPass);
        switchRemember = findViewById(R.id.switchRemember);
        btnLogin = findViewById(R.id.buttonLogin);
        btnRegister = findViewById(R.id.buttonRegister);
        btnOut = findViewById(R.id.buttonOut);
    }

    private void saveOnPreferences(String email, String password){
        if(switchRemember.isChecked()){
            // se crea un editor para añadir info, sharedPreferences declarado es solo de lectura
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.commit();
            editor.apply();
        }
    }

    private boolean logIn(String email, String password){
        if (!isValidEmail(email)){
            Toast.makeText(this, "Email invalido, por favor intenta otra vez", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidPassword(password)){
            Toast.makeText(this, "Password invalido, ingrese al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean isValidEmail(String email){
        // Que no este vacio y tenga formato de email
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password){
        // Que la longitud sea mayor a 4
        return password.length() >= 4;
    }

    private void goToHome(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        // Para que no permita regresar a esta vista una vez que se ha logeado, sino cerrar la app
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
