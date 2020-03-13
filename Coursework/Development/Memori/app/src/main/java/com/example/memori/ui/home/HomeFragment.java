package com.example.memori.ui.home;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.memori.R;
import com.example.memori.components.ShakeEventListener;
import com.example.memori.database.entities.Holiday;

public class HomeFragment extends Fragment implements View.OnClickListener, View.OnTouchListener{

    private SensorManager mSensorManager;

    private ShakeEventListener mSensorListener;

    private HomeViewModel homeViewModel;

    private Holiday latestHoliday;

    private TextView mHolidayName, mHolidayStartDate, mHolidayEndDate;
    private Button btnPhoto, btnLocation, btnPeople, btnNotes, btnTag;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        assignFields(root);

        return root;
    }


    public void extractLatestHoliday(){
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        homeViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
            // Update the cached copy of the words in the adapter.

            while (holidays.size() == 0){

            }

            latestHoliday = holidays.get(holidays.size() - 1);

            Log.d("HomeFunctions", "HomeFragment: latestHoliday.getName()" + latestHoliday.getName());

            loadLatestHoliday();

        });

    }

    public void assignFields(View view){
        mHolidayName = view.findViewById(R.id.label_HolidayName);
        mHolidayStartDate = view.findViewById(R.id.text_VPlaceDate);
        mHolidayEndDate = view.findViewById(R.id.label_HolidayEndDate);

        btnPhoto = view.findViewById(R.id.btn_addPhoto);
        btnLocation = view.findViewById(R.id.btn_addVLocation);
        btnPeople = view.findViewById(R.id.btn_addPeople);
        btnNotes = view.findViewById(R.id.btn_addNotes);
        btnTag = view.findViewById(R.id.btn_addImageTag);

        btnPhoto.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        btnPeople.setOnClickListener(this);
        btnNotes.setOnClickListener(this);
        btnTag.setOnClickListener(this);

        extractLatestHoliday();

    }

    public void loadLatestHoliday(){
        mHolidayName.setText(latestHoliday.getName());
        mHolidayStartDate.setText(latestHoliday.getStartDate());
        mHolidayEndDate.setText(latestHoliday.getEndDate());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addPhoto:
                displayToast("Photo Clicked");

                break;
            case R.id.btn_addVLocation:
                displayToast("Location Clicked");

                break;
            case R.id.btn_addPeople:
                displayToast("People Clicked");

                break;
            case R.id.btn_addNotes:
                displayToast("Notes Clicked");

                break;
            case R.id.btn_addImageTag:
                displayToast("Tag Clicked");

                break;

        }
    }

    public void displayToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d("DetectGesture","Action was DOWN");
                return true;

            case (MotionEvent.ACTION_MOVE) :
                Log.d("DetectGesture","Action was MOVE");
                return true;

            case (MotionEvent.ACTION_UP) :
                Log.d("DetectGesture","Action was UP");
                return true;

            case (MotionEvent.ACTION_CANCEL) :
                Log.d("DetectGesture","Action was CANCEL");
                return true;

            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d("DetectGesture","Movement occurred outside bounds of current screen element");
                return true;

            default :
                return false;

        }
    }
}