package com.example.memori.database;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

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

    private static Images image;

    public HolidayRepository(Application application) {
        HolidayRoomDatabase db = HolidayRoomDatabase.getDatabase(application);
        mHolidayDAO = db.holidayDAO();
        mAllHoliday = mHolidayDAO.getAllHolidays();
        mAllVisitedPlaces = mHolidayDAO.getAllVisitedPlaces();
        mAllImages = mHolidayDAO.getAllImages();
    }

    //----------------------------------

    public LiveData<List<Holiday>> getAllHolidays() { return mAllHoliday;
    }
    public LiveData<List<VisitedPlace>> getAllVisitedPlaces() { return mAllVisitedPlaces;
    }
    public LiveData<List<Images>> getAllImages() { return mAllImages;
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
            Log.d("EditVPlaceSave", "-------------------------------------------");
            Log.d("EditVPlaceSave", "HolidayRepo: Updating VPlace: " + visitedPlaces[0].get_id());
            Log.d("EditVPlaceSave", "HolidayRepo: impVisitedPlace.getHolidayID: " + visitedPlaces[0].getHolidayID());

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
            Log.d("EditVPlaceSave", "-------------------------------------------");
            Log.d("EditVPlaceSave", "HolidayRepo: Updating Image: " + images[0]);

            mDao.updateImage(images[0]);

            return null;
        }
    }

    //----------------------------------

    public void deleteHoliday (Holiday impHoliday){
        new deleteHolidayAsyncTask(mHolidayDAO).execute(impHoliday);
    }

    private static class deleteHolidayAsyncTask extends AsyncTask<Holiday, Void, Void> {
        private final HolidayDAO mDao;

        deleteHolidayAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(Holiday... holidays) {
            mDao.deleteHoliday(holidays[0]);

            return null;
        }
    }

    public void deleteVisitedPlace (VisitedPlace impVisitedPlace){
        new deleteVisitedPlaceAsyncTask(mHolidayDAO).execute(impVisitedPlace);

    }

    private static class deleteVisitedPlaceAsyncTask extends AsyncTask<VisitedPlace, Void, Void> {
        private final HolidayDAO mDao;

        deleteVisitedPlaceAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(final VisitedPlace... visitedPlaces) {
            mDao.deleteVisitedPlace(visitedPlaces[0]);

            return null;
        }
    }

    public void deleteImage (Images impImages){
        new deleteImageAsyncTask(mHolidayDAO).execute(impImages);

    }

    private static class deleteImageAsyncTask extends AsyncTask<Images, Void, Void> {
        private final HolidayDAO mDao;

        deleteImageAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(final Images... images) {
            mDao.deleteImage(images[0]);

            return null;
        }
    }



}
