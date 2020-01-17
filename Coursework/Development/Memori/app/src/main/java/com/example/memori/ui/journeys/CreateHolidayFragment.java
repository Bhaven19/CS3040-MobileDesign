package com.example.memori.ui.journeys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.memori.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateHolidayFragment extends Fragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstantState){
        super.onCreate(savedInstantState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_holiday, container, false);
    }

}
