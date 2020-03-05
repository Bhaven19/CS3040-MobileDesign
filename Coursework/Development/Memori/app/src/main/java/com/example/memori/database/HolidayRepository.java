package com.example.memori.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

public class HolidayRepository {

    private HolidayDAO mHolidayDAO;

    private LiveData<List<Holiday>> mAllHoliday;
    private LiveData<List<VisitedPlace>> mAllVisitedPlaces;
    private LiveData<List<Images>> mAllImages;

    private static int impID;

    public HolidayRepository(Application application) {
        HolidayRoomDatabase db = HolidayRoomDatabase.getDatabase(application);
        mHolidayDAO = db.holidayDAO();
        mAllHoliday = mHolidayDAO.getAllHolidays();
        mAllVisitedPlaces = mHolidayDAO.getAllVisitedPlaces();
        mAllImages = mHolidayDAO.getAllImages();
    }

    //----------------------------------

    public LiveData<List<Holiday>> getAllHolidays() {
        return mAllHoliday;
    }
    public LiveData<List<VisitedPlace>> getAllVisitedPlaces() {
        return mAllVisitedPlaces;
    }
    public LiveData<List<Images>> getAllImages() {
        return mAllImages;
    }

    //----------------------------------

    public void insertHoliday (Holiday impHoliday) {
        new insertHolidayAsyncTask(mHolidayDAO).execute(impHoliday);
    }

    private static class insertHolidayAsyncTask extends AsyncTask<Holiday, Void, Void> {
        private HolidayDAO mAsyncTaskDao;

        insertHolidayAsyncTask(HolidayDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Holiday... params) {
            mAsyncTaskDao.insertHoliday(params[0]);
            return null;
        }
    }

    public void insertVisitedPlace (VisitedPlace visitedPlace) {
        new insertVisitedPlaceAsyncTask(mHolidayDAO).execute(visitedPlace);
    }

    private static class insertVisitedPlaceAsyncTask extends AsyncTask<VisitedPlace, Void, Void> {
        private HolidayDAO mAsyncTaskDao;

        insertVisitedPlaceAsyncTask(HolidayDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final VisitedPlace... params) {
            mAsyncTaskDao.insertVisitedPlace(params[0]);
            return null;
        }
    }

    public void insertImage (Images impImages) {
        new insertImageAsyncTask(mHolidayDAO).execute(impImages);
    }

    private static class insertImageAsyncTask extends AsyncTask<Images, Void, Void> {
        private HolidayDAO mAsyncTaskDao;

        insertImageAsyncTask(HolidayDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Images... params) {
            mAsyncTaskDao.insertImage(params[0]);
            return null;
        }
    }

    //----------------------------------

    public void updateHoliday (Holiday impHoliday){
        new updateHolidayAsyncTask(mHolidayDAO).execute(impHoliday);
    }

    private static class updateHolidayAsyncTask extends AsyncTask<Holiday, Void, Void> {
        private final HolidayDAO mDao;

        updateHolidayAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(Holiday... holidays) {
            mDao.updateHoliday(holidays[0]);

            return null;
        }
    }

    public void updateVisitedPlace (VisitedPlace impVisitedPlace){
        new updateVisitedPlaceAsyncTask(mHolidayDAO).execute(impVisitedPlace);

    }

    private static class updateVisitedPlaceAsyncTask extends AsyncTask<VisitedPlace, Void, Void> {
        private final HolidayDAO mDao;

        updateVisitedPlaceAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(final VisitedPlace... visitedPlaces) {
            mDao.updateVisitedPlace(visitedPlaces[0]);

            return null;
        }
    }

    public void updateImage (Images impImages){
        new updateImageAsyncTask(mHolidayDAO).execute(impImages);

    }

    private static class updateImageAsyncTask extends AsyncTask<Images, Void, Void> {
        private final HolidayDAO mDao;

        updateImageAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(final Images... images) {
            mDao.updateImage(images[0]);

            return null;
        }
    }



}
