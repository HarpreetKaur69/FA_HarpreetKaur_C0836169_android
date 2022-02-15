package com.example.fa_harpreetkaur_c0836169_android.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FavPlace.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavPlacesDao placesDao();
}
