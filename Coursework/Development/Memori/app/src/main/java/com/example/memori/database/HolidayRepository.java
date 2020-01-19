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

    public HolidayRepository(Application application) {
        HolidayRoomDatabase db = HolidayRoomDatabase.getDatabase(application);
        mHolidayDAO = db.holidayDAO();
        mAllHoliday = mHolidayDAO.getAllHolidays();
    }

    public LiveData<List<Holiday>> getAllHolidayNames() {
        return mAllHoliday;
    }

    public void insert (Holiday impHoliday) {
        new insertAsyncTask(mHolidayDAO).execute(impHoliday);
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

}
