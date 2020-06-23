package com.sya.mylifediary.Controlador.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.sya.mylifediary.Controlador.Adapter.StoryAdapter;
import com.sya.mylifediary.Model.Story;
import com.sya.mylifediary.R;
import java.util.ArrayList;
import java.util.List;

public class ListStories extends AppCompatActivity {

    HorizontalInfiniteCycleViewPager viewpager;
    List<Story> stories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stories);

        Story myStory = (Story) getIntent().getSerializableExtra("story");
        myStory.setPhoto(R.drawable.img3);

        initData();
        stories.add(myStory);

        viewpager = (HorizontalInfiniteCycleViewPager) findViewById(R.id.view);
        StoryAdapter adapter = new StoryAdapter(stories, this);
        viewpager.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initData() {
        stories.add(new Story("Cuba", "El gran lugar de mis vacaciones soñadas.", R.drawable.img1));
        stories.add(new Story("Puerto Rico", "Buenos momentos en familia.", R.drawable.img2));
    }
}
