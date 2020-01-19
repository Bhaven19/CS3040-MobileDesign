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

@Database(entities = {Holiday.class}, version = 4, exportSchema = false)
@TypeConverters({Converters.class})
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

        Date date1 = new Date(19,10,11);
        Date date2 = new Date(18,3,25);
        Date date3 = new Date(19,1,4);

        Date[] holidayDates = {date1, date2, date3};

        PopulateDbAsync(HolidayRoomDatabase db) {
            mDao = db.holidayDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mDao.deleteAll();

            for (int i = 0; i <= holidayNames.length - 1; i++) {
                Holiday holiday = new Holiday(holidayNames[i], holidayDates[i]);
                mDao.insert(holiday);
            }


            return null;
        }
    }
}
