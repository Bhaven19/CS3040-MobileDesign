package com.example.memori.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.memori.database.entities.Holiday;

import java.util.List;

@Dao
public interface HolidayDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Holiday holiday);


    @Query("DELETE FROM holiday_table")
    void deleteAll();

    @Query("SELECT * from holiday_table")
    LiveData<List<Holiday>> getAllHolidays();

}
