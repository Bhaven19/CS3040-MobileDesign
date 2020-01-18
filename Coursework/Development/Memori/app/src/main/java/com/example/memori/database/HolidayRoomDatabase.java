package com.example.memori.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.HolidayName;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {HolidayName.class}, version = 1, exportSchema = false)
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
        String[] words = {"dolphin", "crocodile", "cobra"};

        PopulateDbAsync(HolidayRoomDatabase db) {
            mDao = db.holidayDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            // If we have no words, then create the initial list of words
            for (int i = 0; i <= words.length - 1; i++) {
                    HolidayName hName = new HolidayName(words[i]);
                    mDao.insertName(hName);
            }

            return null;
        }
    }
}
