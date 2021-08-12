package com.company.audioplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class allSongsAdapterClass extends RecyclerView.Adapter<allSongsAdapterClass.allSongsViewHolderClass> {
    private Context context;
    private ArrayList<SongsModelClass> songsList;

    public allSongsAdapterClass(Context context, ArrayList<SongsModelClass> songsList) {
        this.context = context;
        this.songsList = songsList;
    }


    @Override
    public allSongsViewHolderClass onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_design_all_songs,
                parent, false);

        allSongsViewHolderClass holder = new allSongsViewHolderClass(view);

        view.setOnClickListener(v -> {
            Intent i = new Intent(context, musicPlayerService.class);
            ArrayList<String> Paths, Names, Albums, Artists;
            Paths = new ArrayList<>();
            Names = new ArrayList<>();
            Albums = new ArrayList<>();
            Artists = new ArrayList<>();

            for (SongsModelClass s : songsList) {
                Paths.add(s.getPath());
                Names.add(s.getName());
                Albums.add(s.getAlbum());
                Artists.add(s.getArtist());
            }

            i.putStringArrayListExtra("Paths",Paths);
            i.putStringArrayListExtra("Names",Names);
            i.putStringArrayListExtra("Albums",Albums);
            i.putStringArrayListExtra("Artists",Artists);
            i.putExtra("Position",holder.getAdapterPosition());

            i.setAction("INIT");
            context.startService(i);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(allSongsAdapterClass.allSongsViewHolderClass holder, int position) {
        holder.bind(songsList.get(position));
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }



    public class allSongsViewHolderClass extends RecyclerView.ViewHolder {
        private ImageView songImage;
        private TextView songName;
        private CardView songCardView;

        public allSongsViewHolderClass(View itemView) {
            super(itemView);

            songImage = itemView.findViewById(R.id.songImageCardDesignAllSongs);
            songName = itemView.findViewById(R.id.songNameCardDesignAllSongs);
            songCardView = itemView.findViewById(R.id.songCardViewAllSongs);
        }

        public void bind(SongsModelClass song){
            songName.setText(song.getName()+"\n"+song.getArtist());


            Uri uri = Uri.parse("content://media/external/audio/albumart/"+song.getAlbum());

//            Log.e("msg",song.getArtist());
            Picasso.get().load(uri).error(R.drawable.music_symbol).into(songImage);

        }

    }
}

