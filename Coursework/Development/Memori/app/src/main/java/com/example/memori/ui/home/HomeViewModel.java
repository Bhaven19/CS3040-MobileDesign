package com.example.memori.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;
    private LiveData<List<Holiday>> mAllHolidays;

    public HomeViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllHolidays = mRepository.getAllHolidays();
    }

    LiveData<List<Holiday>> getAllHolidays() { return mAllHolidays; }

    public void insert(Holiday impHoliday) { mRepository.insertHoliday(impHoliday); }

}