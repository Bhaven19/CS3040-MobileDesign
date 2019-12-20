package com.example.memori.ui.holidays;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.memori.R;

public class HolidaysFragment extends Fragment {

    private HolidaysViewModel holidaysViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        holidaysViewModel =
                ViewModelProviders.of(this).get(HolidaysViewModel.class);
        View root = inflater.inflate(R.layout.fragment_navigation_holidays, container, false);
        final TextView textView = root.findViewById(R.id.text_holidays);
        holidaysViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}