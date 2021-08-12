package com.company.audioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MusicPlayer extends AppCompatActivity {

    private ImageButton next, previous, playPause, queue, favourite;
    private ImageView songImage, shuffleButton;
    private TextView song, totalTime, currentTime;
    private SeekBar seekBar;
    private SharedPreferences preferences;
    private ConstraintLayout constraintLayout;
    private MessageEvent mEvent;
    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        setTitle("MUSIC");
        EventBus.getDefault().register(this);


        next = findViewById(R.id.nextButton);
        previous = findViewById(R.id.previousButton);
        playPause = findViewById(R.id.PlayPauseButton);
        songImage = findViewById(R.id.albumImage);
        song = findViewById(R.id.SongName);
        currentTime = findViewById(R.id.startTimeStamp);
        totalTime = findViewById(R.id.endTimeStamp);
        seekBar = findViewById(R.id.seekBar);
        shuffleButton = findViewById(R.id.shuffleButton);
        constraintLayout = findViewById(R.id.musicPlayerLayout);
        queue = findViewById(R.id.queueButton);
        favourite = findViewById(R.id.favButton);

        preferences = getSharedPreferences("saveData",MODE_PRIVATE);

        shuffleButton.setOnClickListener(v -> {
            int flag = preferences.getInt("shuffle",0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("shuffle",flag^1);
            editor.apply();
        });

        next.setOnClickListener(v -> {
            Intent i = new Intent(this, musicPlayerService.class);
            i.setAction("NEXT");
            startService(i);
        });

        previous.setOnClickListener(v -> {
            Intent i = new Intent(this, musicPlayerService.class);
            i.setAction("PREVIOUS");
            startService(i);
        });

        playPause.setOnClickListener(v -> {
            Intent i = new Intent(this, musicPlayerService.class);
            i.setAction("PLAY/PAUSE");
            startService(i);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    progressChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent i = new Intent(MusicPlayer.this, musicPlayerService.class);
                i.setAction("NEXT");
                startService(i);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Intent i = new Intent(MusicPlayer.this, musicPlayerService.class);
                i.setAction("PREVIOUS");
                startService(i);
            }
        });


        queue.setOnClickListener(v -> {
            startActivity(new Intent(this, queueActivity.class));
        });

        favourite.setOnClickListener(v -> {
            String Path = mEvent.getPath();
            Intent i = new Intent(this, musicPlayerService.class);
            i.setAction("INSERT/DELETE FAVOURITE").putExtra("Path",Path);
            startService(i);
        });

    }

    @Subscribe
    public void receiveMessage(MessageEvent event){
        int flag = event.getPlayPause();
        if (flag == 0) {
            playPause.setImageResource(R.drawable.play_symbol);
        } else {
            playPause.setImageResource(R.drawable.pause_symbol);
        }

        String songName = event.getName();
        String album = event.getAlbum();

        song.setText(songName+'\n'+event.getArtist());
        totalTime.setText(timeStamp(event.getTotalTimeStamp()));
        seekBar.setMax(event.getTotalTimeStamp());
        seekBar.setProgress(event.getCurrentTimeStamp());
        seekBar.setProgress(event.getCurrentTimeStamp());
        currentTime.setText(timeStamp(event.getCurrentTimeStamp()));


        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album);
        Picasso.get().load(uri).error(R.drawable.ic_baseline_music_note_24).into(songImage);

        favourite.setImageResource(event.getFavourite()==0?R.drawable.heart_symbol_empty:
                R.drawable.heart_symbol_filled);

        mEvent = event;


        

    }

    private void progressChanged(int progress){
        Intent i = new Intent(this, musicPlayerService.class);
        i.setAction("SEEK TO");
        i.putExtra("seek_to",progress);
        startService(i);
    }


    private String timeStamp(int time){
        String timeLabel;
        int minute, second;

        minute = time/1000/60;
        second = time/1000%60;

        if (second<10){
            timeLabel = minute+":0"+second;
        }
        else{
            timeLabel = minute+":"+second;
        }

        return timeLabel;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}