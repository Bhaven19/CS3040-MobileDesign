package com.example.memori.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.VisitedPlace;

@Database(entities = {Holiday.class, VisitedPlace.class}, version = 14, exportSchema = false)
public abstract class HolidayRoomDatabase extends RoomDatabase {

    public abstract HolidayDAO holidayDAO();
    private static HolidayRoomDatabase INSTANCE;

    static HolidayRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HolidayRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), HolidayRoomDatabase.class, "holiday_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final HolidayDAO mHolidayDao;

        PopulateDbAsync(HolidayRoomDatabase db) {
            mHolidayDao = db.holidayDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            populateHolidays();
            populateVisitedPlaces();

            return null;
        }

        public void populateHolidays(){
            String [] holidayNames = {"Devon", "Cornwall", "New York", "Munich", "Berlin"};
            String startingLoc = "Birmingham";
            String destination = "Paris";
            String [] dates = {"02/01/2020", "10/02/2020"};
            String travellerNames = "John, Mark, Sophie";
            String travelNotes = "Here are some notes";
            String imagePath = Environment.getExternalStorageDirectory() + "/memori/1581444893270.jpg";
            String imageTag = "summer";

            mHolidayDao.deleteAllHolidays();

            for (int i = 0; i <= holidayNames.length - 1; i++) {
                Holiday holiday = new Holiday(holidayNames[i], startingLoc, destination, dates[0], dates[1], travellerNames, travelNotes, imagePath, imageTag);
                mHolidayDao.insertHoliday(holiday);
            }
        }

        public void populateVisitedPlaces(){
            int holidayID = 1;

            String [] visitedPlaceNames = {"Eiffel Tower", "Ghetto Golf", "Buckingham Palace", "Great Wall of China"};
            String [] dates = {"02/01/2020", "10/02/2020"};
            String location = "Birmingham";
            String travellerNames = "John, Mark, Sophie";
            String travelNotes = "Here are some notes";
            String imagePath = Environment.getExternalStorageDirectory() + "/memori/1581444893270.jpg";
            String imageDate = "23/01/2020";
            String imageTag = "summer";

            mHolidayDao.deleteAllVisitedPlaces();

            for (int i = 0; i <= visitedPlaceNames.length - 1; i++) {
                VisitedPlace visitedPlace = new VisitedPlace(holidayID, visitedPlaceNames[i], dates[0], location, travellerNames, travelNotes, imagePath, imageDate, imageTag);
                mHolidayDao.insertVisitedPlace(visitedPlace);
            }
        }
    }

}
