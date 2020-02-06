package com.example.memori.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.dao.HolidayImageDAO;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.HolidayImage;

@Database(entities = {Holiday.class, HolidayImage.class}, version = 10, exportSchema = false)
public abstract class HolidayRoomDatabase extends RoomDatabase {

    public abstract HolidayDAO holidayDAO();
    public abstract HolidayImageDAO holidayImageDAO();
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
        private final HolidayImageDAO mHolidayImageDao;

        String [] holidayNames = {"Devon", "Cornwall", "New York"};
        String startingLoc = "Birmingham";
        String destination = "Paris";
        String travellerNames = "John, Mark, Sophie";
        String travelNotes = "Here are some notes";

        PopulateDbAsync(HolidayRoomDatabase db) {
            mHolidayDao = db.holidayDAO();
            mHolidayImageDao = db.holidayImageDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mHolidayDao.deleteAll();

            for (int i = 0; i <= holidayNames.length - 1; i++) {
                Holiday holiday = new Holiday(holidayNames[i], startingLoc, destination, travellerNames, travelNotes);
                mHolidayDao.insert(holiday);
            }

            return null;
        }
    }
}
