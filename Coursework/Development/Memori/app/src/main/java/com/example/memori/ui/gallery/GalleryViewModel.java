package com.example.memori.ui.gallery;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private final LiveData<List<Holiday>> mAllHolidays;
    private final LiveData<List<VisitedPlace>> mAllVPlaces;
    private final LiveData<List<Images>> mAllImages;

    private HolidayRepository mRepository;

    public GalleryViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllImages = mRepository.getAllImages();
        mAllHolidays = mRepository.getAllHolidays();
        mAllVPlaces = mRepository.getAllVisitedPlaces();
    }

    LiveData<List<Images>> getAllImages() { return mAllImages; }
    LiveData<List<Holiday>> getAllHolidays() { return mAllHolidays; }
    LiveData<List<VisitedPlace>> getAllVisitedPlaces() { return mAllVPlaces; }

    public ArrayList getAllImagePaths(){
        ArrayList<String> allImagePaths = new ArrayList<>();

        LiveData<List<Images>> imageLiveList = getAllImages();
        List<Images> imageList = imageLiveList.getValue();

        for (Images currentImage : imageList){
            allImagePaths.add(currentImage.getPath());

        }

        return allImagePaths;
    }



}