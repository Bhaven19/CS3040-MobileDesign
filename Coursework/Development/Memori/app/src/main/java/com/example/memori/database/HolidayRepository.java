package com.example.memori.database;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.memori.database.dao.HolidayDAO;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

public class HolidayRepository {

    private HolidayDAO mHolidayDAO;

    private LiveData<List<Holiday>> mAllHoliday;
    private LiveData<List<VisitedPlace>> mAllVisitedPlaces;
    private Holiday mChosenHoliday;

    private static Holiday mLatestHoliday;
    public static Holiday currentEditHoliday;
    public static VisitedPlace currentEditVisitedPlace;

    public static int holidayID;
    public static int vPlaceID;

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
    public Holiday getHoliday(int choice){ return mHolidayDAO.getHoliday(choice); }

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

    public void updateHoliday (Holiday impHoliday, int holidayID){
        new updateHolidayAsyncTask(mHolidayDAO);

        currentEditHoliday = impHoliday;
        this.holidayID = holidayID;
    }

    private static class updateHolidayAsyncTask extends AsyncTask<Void, Void, Void> {
        private final HolidayDAO mDao;

        updateHolidayAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.updateHoliday(holidayID,
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

    public void updateVisitedPlace (VisitedPlace impVisitedPlace, int vPlaceID){
        new updateVisitedPlaceAsyncTask(mHolidayDAO);

        Log.d("VPlaceStorage", "-------------------------------------------");
        Log.d("VPlaceStorage", "HolidayRepo: Updating VPlace: " + vPlaceID);
        Log.d("VPlaceStorage", "HolidayRepo: impVisitedPlace.getName: " + impVisitedPlace.getName());
        Log.d("VPlaceStorage", "HolidayRepo: impVisitedPlace.getDate: " + impVisitedPlace.getDate());
        Log.d("VPlaceStorage", "HolidayRepo: impVisitedPlace.getHolidayID: " + impVisitedPlace.getHolidayID());

        currentEditVisitedPlace = impVisitedPlace;
        this.vPlaceID = vPlaceID;

    }

    private static class updateVisitedPlaceAsyncTask extends AsyncTask<Void, Void, Void> {
        private final HolidayDAO mDao;

        updateVisitedPlaceAsyncTask(HolidayDAO dao) {mDao = dao;}

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.updateVisitedPlace(vPlaceID,
                    currentEditVisitedPlace.getHolidayID(),
                    currentEditVisitedPlace.getName(),
                    currentEditVisitedPlace.getDate(),
                    currentEditVisitedPlace.getLocation(),
                    currentEditVisitedPlace.getTravellers(),
                    currentEditVisitedPlace.getNotes(),
                    currentEditVisitedPlace.getImagePath(),
                    currentEditVisitedPlace.getImageDate(),
                    currentEditVisitedPlace.getImageTag());

            return null;
        }
    }



}
