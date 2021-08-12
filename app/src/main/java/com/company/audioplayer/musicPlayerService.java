package com.company.audioplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class musicPlayerService extends Service {

    private MediaPlayer mediaPlayer = null;
    private ArrayList<String> Paths, Names, Albums, Artists;
    private int Position = 0;
    private Runnable runnable;
    private Handler handler = new Handler();
    private Notification notification = null;
    private RemoteViews expanded, collapsed;
    private AudioManager audioManager;
    private TelephonyManager telephonyManager;
    private Executor executor = new mExecutor();
    private int currentFav = 0;

    public musicPlayerService() {}

    @Override
    public IBinder onBind(Intent intent) {
//         TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String cmd = intent.getAction();
        if(cmd.equals("INIT")){
            if(mediaPlayer!=null){
                if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
            }
            try {
                initMediaPlayer(intent);
                notification = new customNotification().createNotification(this);
                stopForeground(true);

                startForeground(1,(Notification) notification);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(cmd.equals("PLAY/PAUSE")){
            playPause();
            notification = new customNotification().createNotification(this);
            stopForeground(true);

            startForeground(1,(Notification) notification);
        }
        else if(cmd.equals("NEXT")){
            try {
                nextSong();
                notification = new customNotification().createNotification(this);
                stopForeground(true);

                startForeground(1,(Notification) notification);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(cmd.equals("PREVIOUS")){
            try {
                previousSong();
                notification = new customNotification().createNotification(this);
                stopForeground(true);

                startForeground(1,(Notification) notification);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(cmd.equals("SEND BROADCAST")){
            try {
                sendBroadcast();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(cmd.equals("SEEK TO")){
            int progress = intent.getIntExtra("seek_to",0);
            mediaPlayer.seekTo(progress);
        }
        else if(cmd.equals("SEND LIST")){
            sendSongs();
        }
        else if(cmd.equals("INSERT/DELETE FAVOURITE")){
            String path = intent.getStringExtra("Path");
            insertDeleteFavourite(path);
        }
        else if(cmd.equals("SEND FAVOURITE LIST")){
            getAllSongs();
        }
        else if(cmd.equals("STOP")){
            if(runnable!=null)
            handler.removeCallbacks(runnable);
            if(mediaPlayer!=null)
            mediaPlayer.stop();
            mediaPlayer = null;
            stopForeground(true);
            stopSelf();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaPlayer(Intent intent) throws IOException {
        mediaPlayer = new MediaPlayer();

        Paths = intent.getStringArrayListExtra("Paths");
        Names = intent.getStringArrayListExtra("Names");
        Albums = intent.getStringArrayListExtra("Albums");
        Artists = intent.getStringArrayListExtra("Artists");
        Position = intent.getIntExtra("Position",0);

        mediaPlayer.setDataSource(Paths.get(Position));
        mediaPlayer.prepare();
        mediaPlayer.start();

        checkFavourite(Position, true);
    }

    private void playPause(){
        if(mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
                handler.post(runnable);
            }
        }

    }

    private void nextSong() throws IOException {
        if(mediaPlayer!=null){
            Position++;
            Position %= Paths.size();

            int flag = getSharedPreferences("saveData", MODE_PRIVATE).getInt("shuffle", 0);

            if (flag == 1) {
                Position = new Random().nextInt(Paths.size());
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Paths.get(Position));
            mediaPlayer.prepare();
            mediaPlayer.start();
            checkFavourite(Position, false);
        }

    }

    private void previousSong() throws IOException {
        if(mediaPlayer!=null){
            Position = (Position - 1 + Paths.size()) % Paths.size();

            mediaPlayer.reset();
            mediaPlayer.setDataSource(Paths.get(Position));
            mediaPlayer.prepare();
            mediaPlayer.start();
            checkFavourite(Position, false);
        }
    }

    private void sendBroadcast() throws IOException {
        MessageEvent messageEvent;
        if (mediaPlayer != null) {
            messageEvent = new MessageEvent((mediaPlayer.isPlaying() ? 1 : 0),
                    Names.get(Position), Albums.get(Position), mediaPlayer.getDuration(),
                    mediaPlayer.getCurrentPosition(), Artists.get(Position), Paths.get(Position),
                    currentFav);

            if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()){
                nextSong();
            }

            if(!mediaPlayer.isPlaying()){
                handler.removeCallbacks(runnable);
            }
        } else {
            messageEvent = new MessageEvent(-1, "", "", 0,
                    0, "","",-1);
        }

        EventBus.getDefault().post(messageEvent);


        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

        if(mediaPlayer!=null && mediaPlayer.isPlaying())
        audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);


        telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

        if(mediaPlayer!=null && mediaPlayer.isPlaying())
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private void sendSongs(){
        ArrayList<SongsModelClass> songs = new ArrayList<>();
        for (int i = 0; i < Paths.size(); i++) {
            songs.add(new SongsModelClass(Paths.get(i), Names.get(i), Albums.get(i), Artists.get(i)));
        }
        EventBus.getDefault().post(songs);
    }

    private void insertDeleteFavourite(String Path){
        int pos = Paths.indexOf(Path);
        favouriteSongEntity song = new favouriteSongEntity(Paths.get(pos), Names.get(pos),
                Albums.get(pos), Artists.get(pos));

        Database database = Database.getInstance(this);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<favouriteSongEntity> queryOne = database.getFavouriteSongDao().queryOne(Path);

                if(queryOne.size()==0){
                    database.getFavouriteSongDao().insert(song);
                    currentFav = 1;
                }
                else{
                    database.getFavouriteSongDao().delete(queryOne.get(0));
                    currentFav = 0;
                }
            }
        });
    }

    private void checkFavourite(int pos, boolean init){
        String Path = Paths.get(pos);
        Database database = Database.getInstance(this);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<favouriteSongEntity> queryOne = database.getFavouriteSongDao().queryOne(Path);

                if(queryOne.size()==0){
                    currentFav = 0;
                }
                else{
                    currentFav = 1;
                }
            }
        });

        if(init){
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
            runnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(runnable, 1000);
                    try {
                        sendBroadcast();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.post(runnable);
        }

    }

    private void getAllSongs(){
        mExecutor executor = new mExecutor();

        Database database = Database.getInstance(this);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<favouriteSongEntity> entityList = database.getFavouriteSongDao().queryAll();
                ArrayList<SongsModelClass> list = new ArrayList<>();
                for (favouriteSongEntity f:entityList) {
                    list.add(new SongsModelClass(f));
                }
                EventBus.getDefault().post(list);
            }
        });
    }

    public class customNotification extends Notification{

        public Notification createNotification(Context context){
            expanded = new RemoteViews(getPackageName(), R.layout.expanded_notification_layout);
            collapsed = new RemoteViews(getPackageName(), R.layout.collapsed_notification_layout);

            expanded.setImageViewResource(R.id.nextSongNotificationExpanded,R.drawable.next_button_symbol);
            expanded.setImageViewResource(R.id.previousButtonNotificationExpanded,R.drawable.previous_button_symbol);


            Intent intent = new Intent(context, musicPlayerService.class);
            intent.setAction("PLAY/PAUSE");

            expanded.setOnClickPendingIntent(R.id.playPauseNotificationExpanded, PendingIntent
            .getService(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

            collapsed.setOnClickPendingIntent(R.id.playPauseCollapsed, PendingIntent
                    .getService(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT));


            intent = new Intent(context, musicPlayerService.class);
            intent.setAction("NEXT");

            expanded.setOnClickPendingIntent(R.id.nextSongNotificationExpanded,PendingIntent
            .getService(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT));


            intent = new Intent(context, musicPlayerService.class);
            intent.setAction("PREVIOUS");

            expanded.setOnClickPendingIntent(R.id.nextSongNotificationExpanded,PendingIntent
                    .getService(context,2,intent,PendingIntent.FLAG_UPDATE_CURRENT));

            this.update();

            final String CHANNEL_ID = "1";
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "1", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setVibrationPattern(new long[]{0L});
            notificationChannel.enableVibration(true);

            manager.createNotificationChannel(notificationChannel);


            Notification notification = new Notification.Builder(context,CHANNEL_ID)
                    .setContentIntent(PendingIntent.getActivity(context,3,
                            new Intent(context, MusicPlayer.class),PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle("AudioPlayer")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                    .setCustomContentView(expanded)
                    .setCustomBigContentView(expanded)
                    .setVibrate(new long[]{0L})
                    .build();

            return notification;
        }

        public void update(){
            Uri uri = Uri.parse("content://media/external/audio/albumart/" + Albums.get(Position));
            expanded.setImageViewUri(R.id.songImageNotificationExpanded,
                    uri);

            collapsed.setImageViewUri(R.id.songImageCollapsed, uri);

            expanded.setTextViewText(R.id.songNameNotificationExpanded, Names.get(Position));
            collapsed.setTextViewText(R.id.songNameCollapsed, Names.get(Position));

            if(mediaPlayer!=null)
            expanded.setImageViewResource(R.id.playPauseNotificationExpanded,
                    (mediaPlayer.isPlaying()?R.drawable.pause_symbol:R.drawable.play_symbol));
            else{
                expanded.setImageViewResource(R.id.playPauseNotificationExpanded,
                        (R.drawable.play_symbol));
            }


        }
    }

    private AudioManager.OnAudioFocusChangeListener focusChangeListener = focusChange -> {
        switch (focusChange) {
            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):

            case (AudioManager.AUDIOFOCUS_LOSS):
                playPause();
                notification = new customNotification().createNotification(musicPlayerService.this);
                stopForeground(true);

                startForeground(1,(Notification) notification);
                break;
            default:
                break;
        }
    };

    private PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if(mediaPlayer.isPlaying()) playPause();
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                if(!mediaPlayer.isPlaying()) playPause();
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                if(mediaPlayer.isPlaying()) playPause();
            }
            super.onCallStateChanged(state, phoneNumber);
        }
    };
}