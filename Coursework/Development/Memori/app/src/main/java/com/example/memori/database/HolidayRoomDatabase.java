package com.example.memori.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;

import java.util.Date;

@Database(entities = {Holiday.class}, version = 6, exportSchema = false)
public abstract class HolidayRoomDatabase extends RoomDatabase {

    public abstract HolidayDAO holidayDAO();
    private static HolidayRoomDatabase INSTANCE;

    static HolidayRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HolidayRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HolidayRoomDatabase.class, "holiday_database")
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

    /**
     * Populate the database in the background.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final HolidayDAO mDao;
        String [] holidayNames = {"Devon", "Cornwall", "New York"};
        String travellerNames = "John, Mark, Sophie";
        String travelNotes = "Here are some notes";

        PopulateDbAsync(HolidayRoomDatabase db) {
            mDao = db.holidayDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mDao.deleteAll();

            for (int i = 0; i <= holidayNames.length - 1; i++) {
                Holiday holiday = new Holiday(holidayNames[i], travellerNames, travelNotes);
                mDao.insert(holiday);
            }

            return null;
        }
    }
}
