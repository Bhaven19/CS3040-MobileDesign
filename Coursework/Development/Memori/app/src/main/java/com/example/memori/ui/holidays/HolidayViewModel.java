package com.example.memori.ui.holidays;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;

import java.util.List;

public class HolidayViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;
    private LiveData<List<Holiday>> mAllHolidays;

    public HolidayViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllHolidays = mRepository.getAllHolidays();
    }

    LiveData<List<Holiday>> getAllHolidays() { return mAllHolidays; }

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