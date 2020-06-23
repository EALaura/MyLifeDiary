package com.sya.mylifediary.Controlador.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sya.mylifediary.Model.Story;
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
        FloatingActionButton boton = view.findViewById(R.id.btn);

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

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Buton clicked", Toast.LENGTH_SHORT).show();
            }
        });
        container.addView(view);
        return view;
    }
}