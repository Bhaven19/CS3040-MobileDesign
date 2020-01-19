package com.example.memori.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;

import java.util.List;

public class HolidayRepository {

    private HolidayDAO mHolidayDAO;
    private LiveData<List<Holiday>> mAllHolidayNames;

    public HolidayRepository(Application application) {
        HolidayRoomDatabase db = HolidayRoomDatabase.getDatabase(application);
        mHolidayDAO = db.holidayDAO();
        mAllHolidayNames = mHolidayDAO.getAllHolidayNames();
    }

    public LiveData<List<Holiday>> getAllHolidayNames() {
        return mAllHolidayNames;
    }

    public void insert (Holiday impHName) {
        new insertAsyncTask(mHolidayDAO).execute(impHName);
    }

    private static class insertAsyncTask extends AsyncTask<Holiday, Void, Void> {
        private HolidayDAO mAsyncTaskDao;

        insertAsyncTask(HolidayDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Holiday... params) {
            mAsyncTaskDao.insertName(params[0]);
            return null;
        }
    }

}
