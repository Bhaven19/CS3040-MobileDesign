package com.example.memori.ui.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.HolidayListAdapter;
import com.example.memori.database.entities.Holiday;

/**
 * A simple {@link Fragment} subclass.
 */
public class HolidayFragment extends Fragment implements View.OnClickListener{

    private HolidayViewModel holidayViewModel;
    private NavController navController;
    private HolidayViewModel mHolidayViewModel;

    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        holidayViewModel =
                ViewModelProviders.of(this).get(HolidayViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        view.findViewById(R.id.create_holiday_btn).setOnClickListener(this);

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        final HolidayListAdapter adapter = new HolidayListAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

        mHolidayViewModel.getAllHolidayNames().observe(this, words -> {
            // Update the cached copy of the words in the adapter.
            adapter.setWords(words);
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), CreateHolidayActivity.class);
        startActivityForResult(intent, 1);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE) {
            Holiday hName = new Holiday(data.getStringExtra(CreateHolidayActivity.EXTRA_REPLY));
            mHolidayViewModel.insert(hName);
        } else {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Not Saved as it is empty",
                    Toast.LENGTH_LONG).show();
        }
    }

}