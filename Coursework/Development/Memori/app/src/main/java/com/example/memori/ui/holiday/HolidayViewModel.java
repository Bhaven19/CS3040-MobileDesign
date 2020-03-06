package com.example.memori.ui.holiday;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;

    public HolidayViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
    }

    LiveData<List<Holiday>> getAllHolidays() { return mRepository.getAllHolidays(); }
    LiveData<List<VisitedPlace>> getAllVisitedPlaces() { return mRepository.getAllVisitedPlaces(); }
    LiveData<List<Images>> getAllImages() { return mRepository.getAllImages(); }

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

    public void insertVisitedPlace(VisitedPlace impVPlace) { mRepository.insertVisitedPlace(impVPlace); }
    public void updateVisitedPlace(VisitedPlace impVPlace) { mRepository.updateVisitedPlace(impVPlace); }

    public void insertImage(Images impImage) { mRepository.insertImage(impImage); }
    public void updateImage(Images impImage) { mRepository.updateImage(impImage); }

    public Images getLatestImage(){
        List<Images> mAllImagesValue = getAllImages().getValue();
        HashMap<Integer, Images> imageIDToImage = new HashMap<>();

        for (Images currentImage: mAllImagesValue){
            imageIDToImage.put(currentImage.get_id(), currentImage);
        }

        int maxImageID = 1;

        for (Map.Entry<Integer, Images> currentPair : imageIDToImage.entrySet()){
            if (currentPair.getKey() > maxImageID){
                maxImageID = currentPair.getKey();
            }
        }

        Log.d("GetLatestImage", "HolidayViewModel, imageIDToImage.get(maxImageID).get_id(): " + imageIDToImage.get(maxImageID).get_id());

        return imageIDToImage.get(maxImageID);
    }

    //public void deleteAllHolidays() { mRepository.insertHoliday(mRepository.deleteAllHolidays()); }
    //public void delete(Holiday impHolidayName) { mRepository.deleteHoliday(impHolidayName); }


}