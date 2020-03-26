package com.example.memori.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Database(entities = {Holiday.class, VisitedPlace.class, Images.class}, version = 20, exportSchema = false)
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

        private ArrayList<Integer> allImageIDs = new ArrayList<>();
        private ArrayList<Integer> allHolidayIDs = new ArrayList<>();

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
            String [] holidayNames = {
                    "Las Vegas",
                    "Paris",
                    "New York",
                    "Moscow",
                    "Mumbai"};

            String [] startingDates = {
                    "02/01/2020",
                    "03/07/2019",
                    "08/05/2018",
                    "12/09/2017",
                    "14/02/2016"};

            String [] endingDates = {
                    "17/03/2020",
                    "19/11/2019",
                    "22/08/2018",
                    "24/11/2017",
                    "28/05/2016"};

            String travellerNames = "John, Mark, Sophie";

            String travelNotes = "Here are some notes";

            boolean check = false;
            int holidayID = 0;

            if (populate) {

                mHolidayDao.deleteAllHolidays();

                for (int i = 0; i <= holidayNames.length - 1; i++) {
                    Holiday holiday = new Holiday(holidayNames[i], startingDates[i], endingDates[i], travellerNames, travelNotes, allImageIDs.get(i));
                    mHolidayDao.insertHoliday(holiday);

                    if (!check){
                        holidayID = mHolidayDao.getLatestHoliday().get_id();
                        check = true;
                    }

                    allHolidayIDs.add(holidayID + i + 1);

                }
            }
        }

        public void populateVisitedPlaces(){
            String[] visitedPlaceNames = {
                    "Bellagio Hotel",
                    "Eiffel Tower",
                    "Empire State Building",
                    "The Moscow Kremlin",
                    "Gateway of India"};

            String[] date = {
                    "10/03/2020",
                    "01/12/2019",
                    "24/07/2018",
                    "17/04/2017",
                    "26/02/2016"};

            String[] location = {
                    "ChIJvUdRyzDEyIARhA3R2cXH8oI",
                    "ChIJtTeDfh9w5kcRJEWRKN1Yy6I",
                    "ChIJaXQRs6lZwokRY6EFpJnhNNE",
                    "ChIJc-UVs1BKtUYRaC6bPVq_hqg",
                    "ChIJrVwNOsfR5zsRPHOcIKclCsc"};

            String[] travellerNames = {
                    "John,      Sophie,     Chelsea",
                    "Dave,      John,       Steve",
                    "Sophie,    Jasmine,    John",
                    "Bob,       Sophie,     Mark",
                    "Chelsea,   Mark,       Dave"};

            String travelNotes = "Here are some notes";

            int j = 5;

            if (populate){

                mHolidayDao.deleteAllVisitedPlaces();

                for (int i = 0; i <= visitedPlaceNames.length - 1; i++) {
                    VisitedPlace visitedPlace = new VisitedPlace(allHolidayIDs.get(i)-1,
                            visitedPlaceNames[i],
                            date[i],
                            location[i],
                            travellerNames[i],
                            travelNotes,
                            allImageIDs.get(j));


                    mHolidayDao.insertVisitedPlace(visitedPlace);

                    j++;
                }
            }
        }

        public void populateImages(){
            String[] imagePath = {
                    Environment.getExternalStorageDirectory() + "/memori/1581444893270.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image2.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image3.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image4.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image5.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image6.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image7.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image8.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image9.jpg",
                    Environment.getExternalStorageDirectory() + "/memori/image10.jpg"};

            String[] imageDate = {
                    "14/03/2020",
                    "15/02/2020",
                    "16/01/2019",
                    "17/12/2019",
                    "18/11/2018",
                    "19/10/2018",
                    "20/19/2017",
                    "21/08/2017",
                    "22/07/2016",
                    "18/06/2016"};

            String[] imageTag = {
                    "winter",
                    "winter",
                    "summer",
                    "summer",
                    "happy",
                    "happy",
                    "fun",
                    "fun",
                    "new",
                    "new"};

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
                    //Las Vegas - City Center
                    "EiNXIEFyaWEgUGwsIExhcyBWZWdhcywgTlYgODkxNTgsIFVTQSIuKiwKFAoSCR_m0SgyxMiAESNKQUbHcgJVEhQKEgnRffWkgre-gBGjebPV5tXMOg",
                    //Paris - Pantheon
                    "ChIJc8mX0udx5kcRWKcjTwDr5QA",
                    //New York - Wall Street
                    "EhpXYWxsIFN0LCBOZXcgWW9yaywgTlksIFVTQSIuKiwKFAoSCavM7VsWWsKJEQGutQPw3bIsEhQKEgk7CD_TpU_CiRFi_nfhBo8LyA",
                    //Moscow -  State Tretyakov Gallery
                    "ChIJV4MHMv5KtUYRjwJ7motuDaU",
                    //Mumbai - Maharashtra Nature Park
                    "ChIJMQw-GtvI5zsRwERQp-CzzbQ"};

            boolean check = false;
            int imageID = 0;

            List<String> dateList = Arrays.asList(imageDate);
            List<String> tagList = Arrays.asList(imageTag);

            Collections.shuffle(dateList);
            Collections.shuffle(tagList);

            dateList.toArray(imageDate);
            tagList.toArray(imageTag);


            if (populate){

                mHolidayDao.deleteAllImages();

                for (int i = 0; i < 10; i++) {
                    Images newImages = new Images(imagePath[i], imageDate[i], imageTag[i], imageLocation[i]);

                    mHolidayDao.insertImage(newImages);

                    if (!check){
                        imageID = mHolidayDao.getLatestImage().get_id();
                        check = true;
                    }

                    int newID = imageID + i;

                    Log.d("ImagePathError", "imageID + i + 1: " + newID);
                    allImageIDs.add(newID);

                }
            }

        }

    }

}
