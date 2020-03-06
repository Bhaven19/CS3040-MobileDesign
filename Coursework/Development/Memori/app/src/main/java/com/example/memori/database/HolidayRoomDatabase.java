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
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

@Database(entities = {Holiday.class, VisitedPlace.class, Images.class}, version = 18, exportSchema = false)
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

        private final boolean populate = false;

        PopulateDbAsync(HolidayRoomDatabase db) {
            mHolidayDao = db.holidayDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            populateImages();
            populateHolidays();
            populateVisitedPlaces();

            return null;
        }

        public void populateHolidays(){
            String [] holidayNames = {"Devon", "Cornwall", "New York", "Munich", "Berlin"};
            String [] dates = {"02/01/2020", "10/02/2020"};
            String travellerNames = "John, Mark, Sophie";
            String travelNotes = "Here are some notes";

            int imageID = mHolidayDao.getLatestImage().get_id();
            int j = 0;

            if (populate || mHolidayDao.isHolidayEmpty() == 0) {

                mHolidayDao.deleteAllHolidays();

                for (int i = 0; i <= holidayNames.length - 1; i++) {
                    Holiday holiday = new Holiday(holidayNames[i], dates[0], dates[1], travellerNames, travelNotes, imageID - j);
                    mHolidayDao.insertHoliday(holiday);

                    j++;
                }
            }
        }

        public void populateVisitedPlaces(){
            int holidayID = mHolidayDao.getLatestHoliday().get_id();

            String [] visitedPlaceNames = {"Eiffel Tower", "Ghetto Golf", "Buckingham Palace", "Great Wall of China"};
            String [] dates = {"02/01/2020", "10/02/2020"};
            String location = "Birmingham";
            String travellerNames = "John, Mark, Sophie";
            String travelNotes = "Here are some notes";

            int imageID = mHolidayDao.getLatestImage().get_id();
            int j = 5;

            if (populate || mHolidayDao.isVPlaceEmpty() == 0){

                mHolidayDao.deleteAllVisitedPlaces();

                for (int i = 0; i <= visitedPlaceNames.length - 1; i++) {
                    VisitedPlace visitedPlace = new VisitedPlace(holidayID, visitedPlaceNames[i], dates[0], location, travellerNames, travelNotes, imageID - j);
                    mHolidayDao.insertVisitedPlace(visitedPlace);

                    j++;
                }
            }
        }

        public void populateImages(){
            String imagePath = Environment.getExternalStorageDirectory() + "/memori/1581444893270.jpg";
            String imageDate = "23/01/2020";
            String imageTag = "summer";

            if (populate || mHolidayDao.isImagesEmpty() == 0){

                mHolidayDao.deleteAllImages();

                for (int i = 0; i < 9; i++) {
                    Images newImages = new Images(imagePath, imageDate, imageTag);
                    mHolidayDao.insertImage(newImages);
                }
            }
        }

    }

}
