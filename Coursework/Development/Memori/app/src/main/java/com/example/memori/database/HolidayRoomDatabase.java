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

@Database(entities = {Holiday.class, VisitedPlace.class, Images.class}, version = 19, exportSchema = false)
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

        private final boolean populate = true;

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
            String [] holidayNames = {"Las Vegas", "Paris", "New York", "Moscow", "Mumbai"};
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

            String[] visitedPlaceNames = {"Bellagio Hotel", "Eiffel Tower", "Empire State Building", "The Moscow Kremlin", "Gateway of India"};
            String date = "10/02/2020";
            String[] location = {"ChIJvUdRyzDEyIARhA3R2cXH8oI", "ChIJtTeDfh9w5kcRJEWRKN1Yy6I", "ChIJaXQRs6lZwokRY6EFpJnhNNE", "ChIJc-UVs1BKtUYRaC6bPVq_hqg", "ChIJrVwNOsfR5zsRPHOcIKclCsc"};
            String travellerNames = "John, Mark, Sophie";
            String travelNotes = "Here are some notes";

            int imageID = mHolidayDao.getLatestImage().get_id();
            int j = 5;

            if (populate || mHolidayDao.isVPlaceEmpty() == 0){

                mHolidayDao.deleteAllVisitedPlaces();

                for (int i = 0; i <= visitedPlaceNames.length - 1; i++) {
                    VisitedPlace visitedPlace = new VisitedPlace(holidayID, visitedPlaceNames[i], date, location[i], travellerNames, travelNotes, imageID - j);
                    mHolidayDao.insertVisitedPlace(visitedPlace);

                    j++;
                }
            }
        }

        public void populateImages(){
            String imagePath = Environment.getExternalStorageDirectory() + "/memori/1581444893270.jpg";
            String imageDate = "23/01/2020";
            String imageTag = "summer";
            String[] imageLocation = {
                    //Las Vegas
                    "ChIJ0X31pIK3voARo3mz1ebVzDo",
                    //Paris
                    "ChIJD7fiBh9u5kcRYJSMaMOCCwQ",
                    //New York
                    "ChIJOwg_06VPwokRYv534QaPC8g",
                    //Moscow
                    "ChIJybDUc_xKtUYRTM9XV8zWRD0",
                    //Mumbai
                    "ChIJwe1EZjDG5zsRaYxkjY_tpF0",
                    //Bellagio Hotel
                    "ChIJvUdRyzDEyIARhA3R2cXH8oI",
                    //Eiffel Tower
                    "ChIJtTeDfh9w5kcRJEWRKN1Yy6I",
                    //Empire State Building
                    "ChIJaXQRs6lZwokRY6EFpJnhNNE",
                    //The Moscow Kremlin
                    "ChIJc-UVs1BKtUYRaC6bPVq_hqg",
                    //Gateway of India
                    "ChIJrVwNOsfR5zsRPHOcIKclCsc"};

            if (populate || mHolidayDao.isImagesEmpty() == 0){

                mHolidayDao.deleteAllImages();

                for (int i = 0; i < 9; i++) {
                    Images newImages = new Images(imagePath, imageDate, imageTag, imageLocation[i]);
                    mHolidayDao.insertImage(newImages);
                }
            }
        }

    }

}
