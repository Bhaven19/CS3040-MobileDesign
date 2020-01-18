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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.memori.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HolidayFragment extends Fragment implements View.OnClickListener{

    private HolidayViewModel holidayViewModel;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        holidayViewModel =
                ViewModelProviders.of(this).get(HolidayViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday, container, false);
        final TextView textView = root.findViewById(R.id.text_journeys);
        holidayViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        view.findViewById(R.id.create_holiday_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null && navController != null) {
            if (view.getId() == R.id.create_holiday_btn) {
                navController.navigate(R.id.action_journeysFragment_to_createHolidayFragment);

            }
        }
    }

}