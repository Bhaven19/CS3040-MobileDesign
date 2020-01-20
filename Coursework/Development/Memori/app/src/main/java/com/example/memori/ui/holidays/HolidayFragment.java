package com.example.memori.ui.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.HolidayListAdapter;
import com.example.memori.database.entities.Holiday;

import java.sql.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HolidayFragment extends Fragment{

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


        Button createHoliday_btn = view.findViewById(R.id.create_holiday_btn);
        createHoliday_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent createIntent = new Intent(getActivity(), CreateHolidayActivity.class);
                startActivityForResult(createIntent, 1);

            }
        });

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        final HolidayListAdapter adapter = new HolidayListAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

        mHolidayViewModel.getAllHolidayNames().observe(this, words -> {
            // Update the cached copy of the words in the adapter.
            adapter.setWords(words);
        });

        // Add the functionality to swipe items in the recycler view to delete that item
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Holiday myHoliday = adapter.getWordAtPosition(position);

                        Intent viewIntent = new Intent(getActivity(), ViewHolidayActivity.class);
                        viewIntent.putExtra("chosenHoliday", myHoliday);

                        startActivityForResult(viewIntent, 1);

                    }
                });

        helper.attachToRecyclerView(recyclerView);

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String myHolidayName = data.getStringExtra("holidayName");

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && myHolidayName.isEmpty()){
            Holiday holiday = new Holiday(data.getStringExtra("holidayName"),
                    data.getStringExtra("holidayTravellers"),
                    data.getStringExtra("holidayNotes"));

            mHolidayViewModel.insert(holiday);

        } else {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Not Saved as it is empty",
                    Toast.LENGTH_LONG).show();
        }
    }

}