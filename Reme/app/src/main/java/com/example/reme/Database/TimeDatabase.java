package com.example.reme.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TimeEntity.class}, version = 1)
public abstract class TimeDatabase extends RoomDatabase {
    private static TimeDatabase instance;
    public abstract TimeDao timeDao();

    public static synchronized TimeDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TimeDatabase.class, "time_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
