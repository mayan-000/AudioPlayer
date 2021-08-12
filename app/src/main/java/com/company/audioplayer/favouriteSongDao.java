package com.company.audioplayer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface favouriteSongDao {

    @Query("select * from favouriteSongs order by id")
     List<favouriteSongEntity> queryAll();

    @Query("select * from favouriteSongs where path = :Path")
    List<favouriteSongEntity> queryOne(String Path);


    @Insert
    void insert(favouriteSongEntity song);

    @Update
    void update(favouriteSongEntity song);

    @Delete
    void delete(favouriteSongEntity song);

}
