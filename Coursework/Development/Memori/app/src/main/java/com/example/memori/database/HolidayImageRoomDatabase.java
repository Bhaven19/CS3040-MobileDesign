package com.example.memori.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.memori.database.dao.HolidayImageDAO;
import com.example.memori.database.entities.HolidayImage;

@Database(entities = {HolidayImage.class}, version = 8, exportSchema = false)
public abstract class HolidayImageRoomDatabase extends RoomDatabase {

    public abstract HolidayImageDAO holidayImageDAO();
    private static HolidayImageRoomDatabase INSTANCE;

    static HolidayImageRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HolidayImageRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), HolidayImageRoomDatabase.class, "holidayImage_database")
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

        private final HolidayImageDAO mDao;

        PopulateDbAsync(HolidayImageRoomDatabase db) {
            mDao = db.holidayImageDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

//            mDao.deleteAll();

//            for (int i = 0; i <= holidayNames.length - 1; i++) {
//                Holiday holiday = new Holiday(holidayNames[i], startingLoc, destination, travellerNames, travelNotes);
//                mDao.insert(holiday);
//            }

            return null;
        }
    }
}

