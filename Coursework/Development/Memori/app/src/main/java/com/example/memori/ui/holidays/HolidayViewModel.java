package com.example.memori.ui.holidays;

import android.app.Application;
import android.os.Bundle;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.HolidayName;

import java.util.List;

public class HolidayViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;

    private LiveData<List<HolidayName>> mAllHolidayNames;

    public HolidayViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllHolidayNames = mRepository.getAllHolidayNames();
    }

    LiveData<List<HolidayName>> getAllHolidayNames() { return mAllHolidayNames; }

    public void insert(HolidayName impHolidayName) { mRepository.insert(impHolidayName); }
    //public void deleteAll() { mRepository.insert(mRepository.deleteAll()); }
    //public void delete(HolidayName impHolidayName) { mRepository.deleteHoliday(impHolidayName); }


}