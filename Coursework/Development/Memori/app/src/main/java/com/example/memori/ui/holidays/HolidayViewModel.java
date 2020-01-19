package com.example.memori.ui.holidays;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;

import java.util.List;

public class HolidayViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;

    private LiveData<List<Holiday>> mAllHolidayNames;

    public HolidayViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllHolidayNames = mRepository.getAllHolidayNames();
    }

    LiveData<List<Holiday>> getAllHolidayNames() { return mAllHolidayNames; }

    public void insert(Holiday impHoliday) { mRepository.insert(impHoliday); }
    //public void deleteAll() { mRepository.insert(mRepository.deleteAll()); }
    //public void delete(Holiday impHolidayName) { mRepository.deleteHoliday(impHolidayName); }


}