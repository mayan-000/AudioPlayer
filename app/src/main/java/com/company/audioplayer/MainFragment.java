package com.company.audioplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainFragment extends Fragment{

    public MainFragment() {}

    private CardView allSongsCardView, foldersCardView, favouriteCardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }


        allSongsCardView = view.findViewById(R.id.allSongsCardView);
        foldersCardView = view.findViewById(R.id.foldersCardView);
        favouriteCardView = view.findViewById(R.id.favouriteCardView);


        allSongsCardView.setOnClickListener(v -> {
            MainActivity.viewPager2.setCurrentItem(1);
        });

        foldersCardView.setOnClickListener(v -> {
            MainActivity.viewPager2.setCurrentItem(2);
        });

        favouriteCardView.setOnClickListener(v -> {
            MainActivity.viewPager2.setCurrentItem(3);
        });


        return view;
    }


    public static MainFragment getInstance(){
        return new MainFragment();
    }


}