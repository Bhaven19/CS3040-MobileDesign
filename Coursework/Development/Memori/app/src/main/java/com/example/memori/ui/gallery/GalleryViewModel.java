package com.example.memori.ui.gallery;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;

    private LiveData<List<Holiday>> mAllHolidays;

    public GalleryViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllHolidays = mRepository.getAllHolidays();
    }

    LiveData<List<Holiday>> getAllHolidays() { return mAllHolidays; }

    public ArrayList getAllHolidayImagePaths(){
        ArrayList<String> allImagePaths = new ArrayList<>();

        for (Holiday currentHoliday : mRepository.getAllHolidays().getValue()){
            String currentImgPath = currentHoliday.getImagePath();

            allImagePaths.add(currentImgPath);
        }

        return allImagePaths;
    }



}