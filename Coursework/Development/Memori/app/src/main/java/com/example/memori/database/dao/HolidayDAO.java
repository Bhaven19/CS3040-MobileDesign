package com.example.memori.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

    @Query("UPDATE visited_places_table SET " +
            "HOLIDAY_ID = :hID," +
            "NAME = :vpName," +
            "DATE = :vpDate," +
            "LOCATION = :vpLoc," +
            "TRAVELLERS = :vpTravellers," +
            "NOTES = :vpNotes," +
            "IMAGE_PATH = :vpImagePath," +
            "IMAGE_DATE = :vpImageDate," +
            "IMAGE_TAG = :vpImageTag" +
            " WHERE id = :vPlaceID")
    void updateVisitedPlace(int vPlaceID,
                       int hID,
                       String vpName,
                       String vpDate,
                       String vpLoc,
                       String vpTravellers,
                       String vpNotes,
                       String vpImagePath,
                       String vpImageDate,
                       String vpImageTag);

}
