package com.company.audioplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class songsFolderAdapterClass extends RecyclerView.Adapter<songsFolderAdapterClass.songsFolderViewHolder>{
    private ArrayList<String> foldersList;
    private Context context;

    public songsFolderAdapterClass(ArrayList<String> foldersList, Context context) {
        this.foldersList = foldersList;
        this.context = context;
    }


    @Override
    public songsFolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_design_folders,parent,false);

        songsFolderViewHolder holder = new songsFolderViewHolder(view);

        view.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment myFragment = new AllSongsFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Folder",foldersList.get(holder.getAdapterPosition()));
            myFragment.setArguments(bundle);


            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    myFragment).addToBackStack("songs").setTransition(FragmentTransaction.
                    TRANSIT_FRAGMENT_FADE).commit();

        });

        return holder;
    }

    @Override
    public void onBindViewHolder(songsFolderAdapterClass.songsFolderViewHolder holder, int position) {
        holder.bind(foldersList.get(position));
    }

    @Override
    public int getItemCount() {
        return foldersList.size();
    }


    public class songsFolderViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public songsFolderViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.folderNameCardDesign);
        }

        public void bind(String path){
            String [] s = path.split("/");
            textView.setText(s[s.length-1]);
        }
    }



}

