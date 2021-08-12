package com.company.audioplayer;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = favouriteSongEntity.class, exportSchema = false, version = 1)
public abstract class Database extends RoomDatabase {
    private static final String DATABASE_NAME = "SongsDatabase";
    private static Database instance;

    public static synchronized Database getInstance(Context context){
        if(instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    public abstract favouriteSongDao getFavouriteSongDao();

}
