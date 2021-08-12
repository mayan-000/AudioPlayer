package com.company.audioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    public static ViewPager2 viewPager2;
    private ImageButton playPause, addFavourite;
    private ConstraintLayout openPlayer;
    private ImageView songImage;
    private TextView song;
    private CardView playingSong;
    private MessageEvent mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);


        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        playPause = findViewById(R.id.playPauseButton);
        openPlayer = findViewById(R.id.openPlayer);
        songImage = findViewById(R.id.songImage);
        song = findViewById(R.id.songName);
        playingSong = findViewById(R.id.playingCardView);
        addFavourite = findViewById(R.id.addToFavouriteButton);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());


        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager()
                ,getLifecycle());

        viewPager2.setAdapter(adapter);



        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2,
                true, true, (tab, position) -> {
            if(position==0){
                tab.setText("PLAY");
            }
            else if(position==1){
                tab.setText("ALL SONGS");
            }
            else if(position==2){
                tab.setText("FOLDERS");
            }
            else if(position==3){
                tab.setText("FAVOURITE");
            }
        });

        mediator.attach();


        playPause.setOnClickListener(v -> {

            Intent i = new Intent(this, musicPlayerService.class);
            i.setAction("PLAY/PAUSE");
            startService(i);
        });


        openPlayer.setOnClickListener(v -> {
            Intent i = new Intent(this, MusicPlayer.class);
            startActivity(i);
        });


        playingSong.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent i = new Intent(MainActivity.this, musicPlayerService.class);
                i.setAction("NEXT");
                startService(i);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Intent i = new Intent(MainActivity.this, musicPlayerService.class);
                i.setAction("PREVIOUS");
                startService(i);
            }
        });


        addFavourite.setOnClickListener(v -> {
            String Path = mEvent.getPath();
            Intent i = new Intent(this, musicPlayerService.class);
            i.setAction("INSERT/DELETE FAVOURITE").putExtra("Path",Path);
            startService(i);
        });


    }


    @Subscribe
    public void receiveMessageEvent(MessageEvent event){
        int flag = event.getPlayPause();

        if(flag==-1){
            CardView cardView = findViewById(R.id.playingCardView);
            cardView.setVisibility(View.INVISIBLE);
        }
        else {
            CardView cardView = findViewById(R.id.playingCardView);
            cardView.setVisibility(View.VISIBLE);

            if (flag == 0) {
                playPause.setImageResource(R.drawable.play_symbol);
            } else {
                playPause.setImageResource(R.drawable.pause_symbol);
            }


            String songName = event.getName();
            String album = event.getAlbum();

            song.setText(songName+'\n'+event.getArtist());

            {
                Uri uri = Uri.parse("content://media/external/audio/albumart/" + album);
                Picasso.get().load(uri).error(R.drawable.ic_baseline_music_note_24).into(songImage);
            }

            {
                addFavourite.setImageResource(event.getFavourite()==0?R.drawable.heart_symbol_empty:
                        R.drawable.heart_symbol_filled);
            }

            mEvent = event;

        }
    }





    @Override
    protected void onResume() {
        super.onResume();
        Intent i = new Intent(this, musicPlayerService.class);
        i.setAction("SEND BROADCAST");
        startService(i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, musicPlayerService.class).setAction("STOP"));
        EventBus.getDefault().unregister(this);
    }

}