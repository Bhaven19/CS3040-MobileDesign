package com.example.memori.ui.holidays;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memori.database.HolidayImageRepository;
import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.HolidayImage;

import java.util.List;

public class HolidayImageViewModel extends AndroidViewModel {

    private HolidayImageRepository mRepository;

    private LiveData<List<HolidayImage>> mAllHolidayImages;

    public HolidayImageViewModel(Application application) {
        super(application);
        mRepository = new HolidayImageRepository(application);
        mAllHolidayImages = mRepository.getAllHolidayImages();
    }

    LiveData<List<HolidayImage>> getAllHolidays() { return mAllHolidayImages; }

    public String holidayNamesToString(){
        String list = "";

        LiveData<List<HolidayImage>> holidayImageLiveList = getAllHolidays();
        List<HolidayImage> holidayImageList = holidayImageLiveList.getValue();

        for (HolidayImage currentH : holidayImageList) {
            list += currentH.getImagePath() + ", ";
        }

        return list;
    }

    public void insert(HolidayImage impHolidayImage) { mRepository.insert(impHolidayImage); }
    //public void deleteAll() { mRepository.insert(mRepository.deleteAll()); }
    //public void delete(Holiday impHolidayName) { mRepository.deleteHoliday(impHolidayName); }


}