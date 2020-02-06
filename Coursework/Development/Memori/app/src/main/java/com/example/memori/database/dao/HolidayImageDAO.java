package com.example.memori.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.HolidayImage;

import java.util.List;

@Dao
public interface HolidayImageDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HolidayImage holidayImage);

    @Query("DELETE FROM image_table WHERE ID = :iID")
    void delete(int iID);

    @Query("SELECT * from image_table")
    LiveData<List<HolidayImage>> getAllHolidayImages();


}
