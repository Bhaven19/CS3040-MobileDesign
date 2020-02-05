package com.example.memori.ui.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
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
public class HolidayFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private HolidayViewModel holidayViewModel;
    private NavController navController;
    private HolidayViewModel mHolidayViewModel;
    private Toolbar hToolbar;

    private Boolean editClicked = false, deleteClicked = false;

    public static final int NEW_HOLIDAY_ACTIVITY_REQUEST_CODE = 1;
    public static final int VIEW_ALL_HOLIDAYS_ACTIVITY_REQUEST_CODE = 2;
    public static final int SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE = 3;

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            displayToast("Edit Selected");

            editClicked = true;
            deleteClicked = false;

            return true;

        } else if (id == R.id.action_delete) {
            displayToast("Delete Selected");

            editClicked = false;
            deleteClicked = true;

            return true;

        } else if (id == R.id.action_deleteAll) {
            displayToast("DeleteAll Selected");

            editClicked = false;
            deleteClicked = false;

            return true;

        }

        editClicked = false;
        deleteClicked = false;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        createToolbar(view);

        Button createHoliday_btn = view.findViewById(R.id.create_holiday_btn);
        createHoliday_btn.setOnClickListener(v -> {
            Intent createIntent = new Intent(getActivity(), CreateHolidayActivity.class);
            startActivityForResult(createIntent, VIEW_ALL_HOLIDAYS_ACTIVITY_REQUEST_CODE);

        });

        setupRecyclerView();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        holidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday, container, false);

        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == NEW_HOLIDAY_ACTIVITY_REQUEST_CODE){
            Holiday holiday = new Holiday(data.getStringExtra("holidayName"),
                    data.getStringExtra("holidayStartingLoc"),
                    data.getStringExtra("holidayDestination"),
                    data.getStringExtra("holidayTravellers"),
                    data.getStringExtra("holidayNotes"));

            mHolidayViewModel.insert(holiday);

        } else if (resultCode == VIEW_ALL_HOLIDAYS_ACTIVITY_REQUEST_CODE) {
            Log.d("HolidayList", "List of all Holidays: " + mHolidayViewModel.holidayNamesToString());

        } else if (resultCode == SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE) {
            Holiday holiday = new Holiday(data.getStringExtra("holidayName"),
                    data.getStringExtra("holidayStartingLoc"),
                    data.getStringExtra("holidayDestination"),
                    data.getStringExtra("holidayTravellers"),
                    data.getStringExtra("holidayNotes"));

            mHolidayViewModel.update(holiday);

            Log.d("HolidayList", "List of all Holidays: " + mHolidayViewModel.holidayNamesToString());

        } else if (resultCode == 0) {
            displayToast("Not Saved as it is empty");
        }
    }

    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    public void createToolbar(View view){
        setHasOptionsMenu(true);
        hToolbar = view.findViewById(R.id.toolbar_holidays);

        hToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);
        hToolbar.getMenu().getItem(1).setOnMenuItemClickListener(this);
        hToolbar.getMenu().getItem(2).setOnMenuItemClickListener(this);
    }

    public void setupRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        final HolidayListAdapter adapter = new HolidayListAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

        mHolidayViewModel.getAllHolidays().observe(this, holidays -> {
            // Update the cached copy of the words in the adapter.
            adapter.setWords(holidays);

        });

        // Add the functionality to swipe items in the recycler view to delete that item
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Holiday myHoliday = adapter.getWordAtPosition(position);

                        if (editClicked == true){
                            Intent editIntent = new Intent(getActivity(), EditHolidayActivity.class);
                            editIntent.putExtra("chosenHoliday", myHoliday);

                            startActivityForResult(editIntent, SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE);


                        } else if (deleteClicked == true){
                            displayToast(myHoliday.getName() + " has been deleted");


                        } else {
                            Intent viewIntent = new Intent(getActivity(), ViewHolidayActivity.class);
                            viewIntent.putExtra("chosenHoliday", myHoliday);

                            startActivityForResult(viewIntent, 2);

                        }



                    }

                });

        helper.attachToRecyclerView(recyclerView);
    }

}