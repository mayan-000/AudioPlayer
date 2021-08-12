package com.company.audioplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;

public class SongsFolderFragment extends Fragment {

    public SongsFolderFragment() {}

    private RecyclerView folders;
    private ArrayList<String> foldersList = new ArrayList<>();
    private songsFolderAdapterClass adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_folder, container, false);

        folders = view.findViewById(R.id.FoldersListSongsFolderFragment);
        folders.setLayoutManager(new LinearLayoutManager(getContext()));

        setFolders();

        adapter = new songsFolderAdapterClass(foldersList, getActivity());

        folders.setAdapter(adapter);

        return view;
    }


    private void setFolders(){
        ContentResolver contentResolver = getActivity().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if(cursor!=null && cursor.moveToFirst()){
            HashSet<String> h = new HashSet<>();
            do{
                String Data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String Name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                Data = Data.replace(Name,"");

                h.add(Data);

            }while(cursor.moveToNext());

            foldersList.addAll(h);
            cursor.close();
        }
    }

    public static SongsFolderFragment getInstance(){
        return new SongsFolderFragment();
    }
}