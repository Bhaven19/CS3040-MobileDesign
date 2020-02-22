package com.example.memori.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

public class HolidayRepository {

    private HolidayDAO mHolidayDAO;

    private LiveData<List<Holiday>> mAllHoliday;
    private LiveData<List<VisitedPlace>> mAllVisitedPlaces;

    private static Holiday mLatestHoliday;
    public static Holiday currentEditHoliday;

    public HolidayRepository(Application application) {
        HolidayRoomDatabase db = HolidayRoomDatabase.getDatabase(application);
        mHolidayDAO = db.holidayDAO();
        mAllHoliday = mHolidayDAO.getAllHolidays();
        mAllVisitedPlaces = mHolidayDAO.getAllVisitedPlaces();
    }

    //----------------------------------

    public LiveData<List<Holiday>> getAllHolidays() {
        return mAllHoliday;
    }
    public LiveData<List<VisitedPlace>> getAllVisitedPlaces() {
        return mAllVisitedPlaces;
    }

    //----------------------------------

    public void insertHoliday (Holiday impHoliday) {
        new insertHolidayAsyncTask(mHolidayDAO).execute(impHoliday);
    }

    public void insertVisitedPlace (VisitedPlace visitedPlace) {
        new insertVisitedPlaceAsyncTask(mHolidayDAO).execute(visitedPlace);
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

    //----------------------------------

    public void updateHoliday (Holiday impHoliday){
        new updateHolidayAsyncTask(mHolidayDAO);

        currentEditHoliday = impHoliday;
    }

    private static class updateHolidayAsyncTask extends AsyncTask<Void, Void, Void> {
        private final HolidayDAO mDao;

        updateHolidayAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.updateHoliday(currentEditHoliday.get_id(),
                    currentEditHoliday.getName(),
                    currentEditHoliday.getStartingLoc(),
                    currentEditHoliday.getDestination(),
                    currentEditHoliday.getStartDate(),
                    currentEditHoliday.getEndDate(),
                    currentEditHoliday.getTravellers(),
                    currentEditHoliday.getNotes(),
                    currentEditHoliday.getImagePath(),
                    currentEditHoliday.getImageTag());

            return null;
        }
    }



}
