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
import com.sya.mylifediary.Controlador.Activities.HomeActivity;
import com.sya.mylifediary.Controlador.Activities.ShareActivity;
import com.sya.mylifediary.Controlador.Activities.StoryActivity;
import com.sya.mylifediary.Model.Story;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import com.sya.mylifediary.R;

public class StoryAdapter extends PagerAdapter {

    List<Story> listStories;
    Context context;

    public StoryAdapter(List<Story> movieList, Context context) {
        this.listStories = movieList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listStories.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflate view
        final View view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false);
        // View
        ImageView imageView = (ImageView) view.findViewById(R.id.vista);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView location = (TextView) view.findViewById(R.id.location);
        TextView description = (TextView) view.findViewById(R.id.description);
        FloatingActionButton share = view.findViewById(R.id.btn);

        // set data
        imageView.setImageResource(listStories.get(position).getPhoto());
        title.setText(listStories.get(position).getLocation());
        location.setText(listStories.get(position).getLocation());
        description.setText(listStories.get(position).getDescription());

        // set event click
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Click to view, aqui puedes agregar startactivity
                // yo solo uso toast
                Toast.makeText(context, "" + listStories.get(position).getLocation(), Toast.LENGTH_SHORT).show();
            }
        });

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

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}