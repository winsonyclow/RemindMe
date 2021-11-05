package com.example.reme.Database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@androidx.room.Dao
public interface TimeDao {
    @Insert
    void insert(TimeEntity timeEntity);

    @Query("SELECT * FROM timeReminder WHERE id = :id")
    List<TimeEntity> getData(String id);

    @Query("SELECT * FROM timeReminder")
    List<TimeEntity> getAllTimeData();

    @Query("DELETE FROM timeReminder WHERE id = :id")
    void deleteById(String id);

}
