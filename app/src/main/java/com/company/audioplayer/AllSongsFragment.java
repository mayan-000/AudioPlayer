package com.company.audioplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AllSongsFragment extends Fragment {

    public AllSongsFragment() {}

    private RecyclerView recyclerViewSongs;
    private allSongsAdapterClass adapterClass;
    private ArrayList<SongsModelClass> songsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);

        recyclerViewSongs = view.findViewById(R.id.list_allSongsFragment);
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getContext()));

        String folderName = null;
        if(getArguments()!=null)
            folderName = getArguments().getString("Folder");

        if(folderName==null)
            setSongsList();
        else
            setSongsList(folderName);

        adapterClass = new allSongsAdapterClass(getContext(), songsList);

        recyclerViewSongs.setAdapter(adapterClass);

        return view;
    }



    public void setSongsList(){

        ContentResolver contentResolver = getActivity().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null,null,null,null);


        if (cursor != null && cursor.moveToFirst()) {
            songsList = new ArrayList<>();

            do{

                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));


                songsList.add(new SongsModelClass(data, title, album, artist));
            }
            while (cursor.moveToNext());

            cursor.close();

        }
    }

    public void setSongsList(String folderName){
//        Log.e("msg",""+folderName);

        ContentResolver contentResolver = getActivity().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null,
                null,null,null);


        if (cursor != null && cursor.moveToFirst()) {
            songsList = new ArrayList<>();
//            Log.e("msg",""+folderName);

            do{

                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                Log.e("msg",""+data);

                if(data.indexOf(folderName)!=-1)
                    songsList.add(new SongsModelClass(data, title, album, artist));
            }
            while (cursor.moveToNext());

            cursor.close();

        }
    }

    public static AllSongsFragment getInstance(){
        return new AllSongsFragment();
    }


}