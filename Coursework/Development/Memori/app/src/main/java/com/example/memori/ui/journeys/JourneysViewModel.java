package com.example.memori.ui.journeys;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class JourneysViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public JourneysViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is journeys fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}