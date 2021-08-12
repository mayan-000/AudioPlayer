package com.company.audioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class queueActivity extends AppCompatActivity {

    private ImageButton playPause;
    private ConstraintLayout openPlayer;
    private ImageView songImage;
    private TextView song;
    private RecyclerView recyclerView;
    private allSongsAdapterClass adapter = null;
    private ArrayList<SongsModelClass> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        setTitle("Queue");
        EventBus.getDefault().register(this);


        playPause = findViewById(R.id.playPauseButtonQueue);
        openPlayer = findViewById(R.id.openPlayerQueue);
        songImage = findViewById(R.id.songImageQueue);
        song = findViewById(R.id.songNameQueue);
        recyclerView = findViewById(R.id.queueList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        startService(new Intent(this, musicPlayerService.class).setAction("SEND LIST"));


        playPause.setOnClickListener(v -> {
            Intent i = new Intent(this, musicPlayerService.class);
            i.setAction("PLAY/PAUSE");
            startService(i);
        });


    }


    @Subscribe
    public void receiveMessageEvent(MessageEvent event){
        int flag = event.getPlayPause();

        if(flag==-1){}
        else {
            if (flag == 0) {
                playPause.setImageResource(R.drawable.play_symbol);
            } else {
                playPause.setImageResource(R.drawable.pause_symbol);
            }

            String songName = event.getName();
            String album = event.getAlbum();

            song.setText(songName+'\n'+event.getArtist());

            Uri uri = Uri.parse("content://media/external/audio/albumart/" + album);
            Picasso.get().load(uri).error(R.drawable.ic_baseline_music_note_24).into(songImage);
        }
    }

    @Subscribe
    public void receiveSongs(ArrayList<SongsModelClass> songs){
        this.songs = songs;
        adapter = new allSongsAdapterClass(this,songs);

        recyclerView.setAdapter(adapter);
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
        EventBus.getDefault().unregister(this);
    }

}