package com.example.memori.fragments.holiday;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

public class HolidayViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;
    private LiveData<List<Holiday>> mAllHolidays;
    private LiveData<List<VisitedPlace>> mAllVisitedPlaces;

    public HolidayViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllHolidays = mRepository.getAllHolidays();
        mAllVisitedPlaces = mRepository.getAllVisitedPlaces();
    }

    LiveData<List<Holiday>> getAllHolidays() { return mAllHolidays; }
    LiveData<List<VisitedPlace>> getAllVisitedPlaces() { return mAllVisitedPlaces; }

    public String holidayNamesToString(){
        String list = "";

        LiveData<List<Holiday>> holidayLiveList = getAllHolidays();
        List<Holiday> holidayList = holidayLiveList.getValue();

        for (Holiday currentH : holidayList) {
            list += currentH.getName() + ", ";
        }

        return list;
    }

    public void insert(Holiday impHoliday) { mRepository.insertHoliday(impHoliday); }
    public void update(Holiday impHoliday) { mRepository.updateHoliday(impHoliday); }

    //public void deleteAllHolidays() { mRepository.insertHoliday(mRepository.deleteAllHolidays()); }
    //public void delete(Holiday impHolidayName) { mRepository.deleteHoliday(impHolidayName); }


}