package com.company.audioplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class Favourites extends Fragment {

    public Favourites() {}

    private RecyclerView recyclerView;
    private allSongsAdapterClass adapter;
    private ArrayList<SongsModelClass> list = new ArrayList<>();
    private SwipeRefreshLayout swipe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        recyclerView = view.findViewById(R.id.favouriteList);
        swipe = view.findViewById(R.id.swipeFavourite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final Intent[] i = {new Intent(getContext(), musicPlayerService.class)};
        i[0].setAction("SEND FAVOURITE LIST");
        getActivity().startService(i[0]);

        swipe.setOnRefreshListener(() -> {
            i[0] = new Intent(getContext(), musicPlayerService.class);
            i[0].setAction("SEND FAVOURITE LIST");
            getActivity().startService(i[0]);
            swipe.setRefreshing(false);
        });

        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }


    public static Favourites getInstance(){
        return new Favourites();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveSongs(ArrayList<SongsModelClass> songs){
        list = songs;
        adapter = new allSongsAdapterClass(getActivity(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}