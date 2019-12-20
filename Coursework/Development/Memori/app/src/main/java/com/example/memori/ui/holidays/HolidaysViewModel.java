package com.example.memori.ui.holidays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HolidaysViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HolidaysViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is holidays fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}