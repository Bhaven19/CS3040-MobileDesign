package com.example.memori.ui.map;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

public class MapViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;

    private MutableLiveData<String> mText;

    public MapViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mText = new MutableLiveData<>();
    }

    public void setText(String text){
        mText.setValue(text);
    }

    public LiveData<String> getText(){
        return mText;
    }

    LiveData<List<Holiday>> getAllHolidays() { return mRepository.getAllHolidays(); }
    LiveData<List<VisitedPlace>> getAllVisitedPlaces() { return mRepository.getAllVisitedPlaces(); }
    LiveData<List<Images>> getAllImages() { return mRepository.getAllImages(); }

}