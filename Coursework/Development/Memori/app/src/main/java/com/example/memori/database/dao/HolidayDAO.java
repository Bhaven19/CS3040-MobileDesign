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

    @Query("SELECT * from holiday_table WHERE ID = (SELECT MAX(ID) FROM holiday_table)")
    Holiday getLatestHoliday();

    @Query("UPDATE holiday_table SET NAME = :hName," +
            "STARTING_LOC = :hStartLoc," +
            "DESTINATION = :hEndLoc," +
            "START_DATE = :hStartDate," +
            "END_DATE = :hEndDate," +
            "TRAVELLERS = :hTravellers," +
            "NOTES = :hNotes," +
            "IMAGE_PATH = :hImagePath," +
            "IMAGE_TAG = :hImageTag" +
            " WHERE id = :hID")
    void updateHoliday(int hID,
                       String hName,
                       String hStartLoc,
                       String hEndLoc,
                       String hStartDate,
                       String hEndDate,
                       String hTravellers,
                       String hNotes,
                       String hImagePath,
                       String hImageTag);

}
