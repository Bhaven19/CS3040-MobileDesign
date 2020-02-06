package com.example.memori.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.dao.HolidayImageDAO;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.HolidayImage;

import java.util.List;

public class HolidayImageRepository {

    private HolidayImageDAO mHolidayImageDAO;
    private LiveData<List<HolidayImage>> mAllHolidayImages;
    public static HolidayImage currentHolidayImage;

    public HolidayImageRepository(Application application) {
        HolidayImageRoomDatabase db = HolidayImageRoomDatabase.getDatabase(application);
        mHolidayImageDAO = db.holidayImageDAO();
        mAllHolidayImages = mHolidayImageDAO.getAllHolidayImages();
    }

    public LiveData<List<HolidayImage>> getAllHolidayImages() {
        return mAllHolidayImages;
    }

    public void insert (HolidayImage impHolidayImage) {
        new insertAsyncTask(mHolidayImageDAO).execute(impHolidayImage);
    }

    private static class insertAsyncTask extends AsyncTask<HolidayImage, Void, Void> {
        private HolidayImageDAO mAsyncTaskDao;

        insertAsyncTask(HolidayImageDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final HolidayImage... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }



}
