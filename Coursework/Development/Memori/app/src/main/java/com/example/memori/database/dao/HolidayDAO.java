package com.example.memori.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

@Dao
public interface HolidayDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertHoliday(Holiday holiday);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertVisitedPlace(VisitedPlace visitedPlace);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertImage(Images images);

    @Query("DELETE FROM holiday_table")
    void deleteAllHolidays();

    @Query("DELETE FROM visited_places_table")
    void deleteAllVisitedPlaces();

    @Query("DELETE FROM images_table")
    void deleteAllImages();

    @Query("SELECT * from holiday_table")
    LiveData<List<Holiday>> getAllHolidays();

    @Query("SELECT * from visited_places_table")
    LiveData<List<VisitedPlace>> getAllVisitedPlaces();

    @Query("SELECT * from images_table")
    LiveData<List<Images>> getAllImages();

    @Query("SELECT * from holiday_table WHERE ID=(Select max(ID) from holiday_table)")
    Holiday getLatestHoliday();

    @Query("SELECT * from visited_places_table WHERE ID=(Select max(ID) from visited_places_table)")
    Holiday getLatestVPlace();

    @Query("SELECT COUNT(*) FROM holiday_table")
    int isHolidayEmpty();

    @Query("SELECT COUNT(*) FROM visited_places_table")
    int isVPlaceEmpty();

    @Query("SELECT COUNT(*) FROM images_table")
    int isImagesEmpty();

    @Update()
    void updateHoliday(Holiday... holiday);

    @Update()
    void updateVisitedPlace(VisitedPlace... vPlace);

    @Update()
    void updateImage(Images... images);

}
