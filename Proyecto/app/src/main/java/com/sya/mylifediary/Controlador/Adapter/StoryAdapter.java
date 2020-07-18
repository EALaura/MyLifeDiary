package com.sya.mylifediary.Controlador.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sya.mylifediary.Controlador.Activities.ShareActivity;
import com.sya.mylifediary.Model.Story;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import com.sya.mylifediary.R;

/* Es el adaptador necesario para la presentación de los
*  item de la lista circular de historias */
public class StoryAdapter extends PagerAdapter {
    public List<Story> listStories;
    public Context context;

    // constructor, recibe la lista de historias y el contexto del activity
    public StoryAdapter(List<Story> storyList, Context context) {
        this.listStories = storyList;
        this.context = context;
    }
    // retorna el tamaño de la lista
    @Override
    public int getCount() {
        return listStories.size();
    }
    // verificar si la vista es un object
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
    // para remover un item
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    // instancia el item actual a la vista del container
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflate view, carga el layout de la vista de la lista
        final View view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false);
        // Referencias del layout card_item
        ImageView imageView = view.findViewById(R.id.vista);
        TextView title = view.findViewById(R.id.title);
        TextView location = view.findViewById(R.id.location);
        TextView description = view.findViewById(R.id.description);
        FloatingActionButton share = view.findViewById(R.id.btn);

        // se fija el contenido del item actual de la lista en el View
        //imageView.setImageBitmap(listStories.get(position).getPhoto());
        title.setText(listStories.get(position).getTitle());
        location.setText(listStories.get(position).getLocation());
        description.setText(listStories.get(position).getDescription());

        // set event click, devuelve un mensaje con la posicion
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + listStories.get(position).getLocation(), Toast.LENGTH_SHORT).show();
            }
        });
        // Al hacer click en el icono de Bluettoth se prepara el envio de la imagen
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap captura = getBitmapFromView(view);
                createImageFromBitmap(captura);
                Intent intent = new Intent(context, ShareActivity.class);
                context.startActivity(intent);
            }
        });
        container.addView(view);
        return view;
    }

    /* Comprime la imagen con un nombre, esto permite el envio de la imagen
       al siguiente activity ya que si se envia por un intent se exede el tamaño maximo*/
    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    // Antes de enviar, se debe convertir la vista en un bitmap (imagen)
    public static Bitmap getBitmapFromView(View view) {
        //Define un bitmap con el mismo tamaño que la vista
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Lo envia a un canvas
        Canvas canvas = new Canvas(returnedBitmap);
        //Se obtiene el background de la vista
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        // Dibuja la vista en el canvas
        view.draw(canvas);
        //Devuelve el bitmap
        return returnedBitmap;
    }
}