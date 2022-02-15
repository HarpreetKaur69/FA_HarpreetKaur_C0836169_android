package com.example.fa_harpreetkaur_c0836169_android.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavPlacesDao {
    @Query("SELECT * FROM favPlace")
    List<FavPlace> getAll();

    @Query("SELECT * FROM favplace WHERE name LIKE :query")
    List<FavPlace> search(String query);

    @Insert
    void insert(FavPlace favPlace);

    @Delete
    void delete(FavPlace favPlace);

    @Update
    void update(FavPlace favPlace);
}
