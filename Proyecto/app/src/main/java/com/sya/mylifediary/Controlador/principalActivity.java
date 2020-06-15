package com.sya.mylifediary.Controlador;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sya.mylifediary.R;

public class principalActivity extends AppCompatActivity {
    //
    ImageView imagen_;
    Button camara_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Inicializar
        imagen_ = (ImageView)findViewById(R.id.view_img);
        camara_ = (Button)findViewById(R.id.btn_cam);

        //Camara
        camara_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Captura la imagen obtenida de la camara
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        imagen_.setImageBitmap(bitmap);
    }
}
