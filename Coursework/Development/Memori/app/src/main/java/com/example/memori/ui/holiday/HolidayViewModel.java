package com.example.memori.ui.holiday;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

public class HolidayViewModel extends AndroidViewModel {

    private HolidayRepository mRepository;
    private LiveData<List<Holiday>> mAllHolidays;
    private LiveData<List<VisitedPlace>> mAllVisitedPlaces;
    private LiveData<List<Images>> mAllImages;

    public HolidayViewModel(Application application) {
        super(application);
        mRepository = new HolidayRepository(application);
        mAllHolidays = mRepository.getAllHolidays();
        mAllVisitedPlaces = mRepository.getAllVisitedPlaces();
        mAllImages = mRepository.getAllImages();
    }

    LiveData<List<Holiday>> getAllHolidays() { return mAllHolidays; }
    LiveData<List<VisitedPlace>> getAllVisitedPlaces() { return mAllVisitedPlaces; }
    LiveData<List<Images>> getAllImages() { return mAllImages; }

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
    public void updateVisitedPlace(VisitedPlace impVPlace) {
        Log.d("EditVPlaceSave", "-------------------------------------------");
        Log.d("EditVPlaceSave", "HolidayViewModel: Updating VPlace: " + impVPlace.get_id());
        Log.d("EditVPlaceSave", "HolidayViewModel: impVisitedPlace.getName: " + impVPlace.getName());
        Log.d("EditVPlaceSave", "HolidayViewModel: impVisitedPlace.getDate: " + impVPlace.getDate());
        Log.d("EditVPlaceSave", "HolidayViewModel: impVisitedPlace.getHolidayID: " + impVPlace.getHolidayID());

        mRepository.updateVisitedPlace(impVPlace);

    }

    public void insertImage(Images impImage) { mRepository.insertImage(impImage); }
    public void updateImage(Images impImage) {
        Log.d("EditVPlaceSave", "HolidayViewModel- impImage: " + impImage);

        mRepository.updateImage(impImage);

    }

    //public void deleteAllHolidays() { mRepository.insertHoliday(mRepository.deleteAllHolidays()); }
    //public void delete(Holiday impHolidayName) { mRepository.deleteHoliday(impHolidayName); }


}