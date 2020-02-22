package com.example.memori.fragments.gallery;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;

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

        LiveData<List<Holiday>> holidayLiveList = getAllHolidays();
        List<Holiday> holidayList = holidayLiveList.getValue();

        Log.d("GalleryImages", "GalleryViewModel, mRepoHolidays: " + holidayLiveList);
        Log.d("GalleryImages", "GalleryViewModel, holidayList: " + holidayList);

        for (Holiday currentHoliday : holidayList){
            String currentImgPath = currentHoliday.getImagePath();

            allImagePaths.add(currentImgPath);
        }

        return allImagePaths;
    }



}