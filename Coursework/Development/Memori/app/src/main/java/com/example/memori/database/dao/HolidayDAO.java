package com.example.memori.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

@Dao
public interface HolidayDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertHoliday(Holiday holiday);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertVisitedPlace(VisitedPlace visitedPlace);

    @Query("DELETE FROM holiday_table")
    void deleteAllHolidays();

    @Query("DELETE FROM visited_places_table")
    void deleteAllVisitedPlaces();

    @Query("SELECT * from holiday_table")
    LiveData<List<Holiday>> getAllHolidays();

    @Query("SELECT * from visited_places_table")
    LiveData<List<VisitedPlace>> getAllVisitedPlaces();

    @Query("SELECT * from holiday_table WHERE ID=(Select max(ID) from holiday_table)")
    Holiday getLatestHoliday();

    @Query("SELECT * from holiday_table WHERE ID = :hID")
    Holiday getHoliday(int hID);

    @Query("SELECT COUNT(*) FROM holiday_table")
    int isHolidayEmpty();

    @Query("SELECT COUNT(*) FROM visited_places_table")
    int isVPlaceEmpty();

    @Update()
    void updateHoliday(Holiday... holiday);

    @Update()
    void updateVisitedPlace(VisitedPlace... vPlace);

}
