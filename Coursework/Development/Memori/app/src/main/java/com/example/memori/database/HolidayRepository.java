package com.example.memori.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;

import java.util.List;

public class HolidayRepository {

    private HolidayDAO mHolidayDAO;
    private LiveData<List<Holiday>> mAllHoliday;
    public static Holiday currentEditHoliday;

    public HolidayRepository(Application application) {
        HolidayRoomDatabase db = HolidayRoomDatabase.getDatabase(application);
        mHolidayDAO = db.holidayDAO();
        mAllHoliday = mHolidayDAO.getAllHolidays();
    }

    public LiveData<List<Holiday>> getAllHolidays() {
        return mAllHoliday;
    }

    public void insert (Holiday impHoliday) {
        new insertAsyncTask(mHolidayDAO).execute(impHoliday);
    }

    public void update (Holiday impHoliday){
        new updateAsyncTask(mHolidayDAO);

        currentEditHoliday = impHoliday;
    }

    private static class insertAsyncTask extends AsyncTask<Holiday, Void, Void> {
        private HolidayDAO mAsyncTaskDao;

        insertAsyncTask(HolidayDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Holiday... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final HolidayDAO mDao;

        updateAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.updateHoliday(currentEditHoliday.get_id(),
                    currentEditHoliday.getName(),
                    currentEditHoliday.getStartingLoc(),
                    currentEditHoliday.getDestination(),
                    currentEditHoliday.getTravellers(),
                    currentEditHoliday.getNotes());

            return null;
        }
    }


}
