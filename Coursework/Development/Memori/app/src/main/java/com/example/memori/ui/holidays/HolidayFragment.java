package com.example.memori.ui.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.HolidayListAdapter;
import com.example.memori.database.VPlaceListAdapter;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.VisitedPlace;

/**
 * A simple {@link Fragment} subclass.
 */
public class HolidayFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private NavController navController;
    private HolidayViewModel mHolidayViewModel;
    private HolidayListAdapter holidayListAdapter;
    private VPlaceListAdapter vPlaceListAdapter;
    private Toolbar currentToolbar;

    private RadioGroup mToggle;
    private ConstraintLayout constLay_Holidays, constLay_vPlaces;

    private Boolean editClicked = false, deleteClicked = false;

    public static final String HOLIDAY_ACTIVE = "holiday_active", VPLACE_ACTIVE = "vplace_active";
    public String currentActive = HOLIDAY_ACTIVE;

    public static final int NEW_HOLIDAY_ACTIVITY_REQUEST_CODE = 1;
    public static final int VIEW_ALL_HOLIDAYS_ACTIVITY_REQUEST_CODE = 2;
    public static final int SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE = 3;
    public static final int NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 4;

    //----------------EVERYTHING BELOW THIS LINE DOES NOT NEED REDEVELOPING FOR HOLIDAY & VISITEDPLACE FUNCTIONALITY

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        createToolbar(view);

        setupCreateButton(view);

        setupToggle(view);

        setupRecyclerView();

    }

    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    //----------------EVERYTHING BELOW THIS LINE NEEDS REDEVELOPING FOR HOLIDAY & VISITEDPLACE FUNCTIONALITY

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == NEW_HOLIDAY_ACTIVITY_REQUEST_CODE){
            Holiday holiday = new Holiday(data.getStringExtra("holidayName"),
                    data.getStringExtra("holidayStartingLoc"),
                    data.getStringExtra("holidayDestination"),
                    data.getStringExtra("holidayStartDate"),
                    data.getStringExtra("holidayEndDate"),
                    data.getStringExtra("holidayTravellers"),
                    data.getStringExtra("holidayNotes"),
                    data.getStringExtra("holidayImagePath"),
                    data.getStringExtra("holidayImageTag"));

            Log.d("HolidayList", "HolidayFragment, imagePath: " + data.getStringExtra("holidayImagePath"));

            mHolidayViewModel.insert(holiday);

            Log.d("HolidayList", "Holiday Saved");


        } else if (resultCode == VIEW_ALL_HOLIDAYS_ACTIVITY_REQUEST_CODE) {
            Log.d("HolidayList", "List of all Holidays: " + mHolidayViewModel.holidayNamesToString());

        } else if (resultCode == SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE) {
            Holiday holiday = new Holiday(data.getStringExtra("holidayName"),
                    data.getStringExtra("holidayStartingLoc"),
                    data.getStringExtra("holidayDestination"),
                    data.getStringExtra("holidayStartDate"),
                    data.getStringExtra("holidayEndDate"),
                    data.getStringExtra("holidayTravellers"),
                    data.getStringExtra("holidayNotes"),
                    data.getStringExtra("holidayImagePath"),
                    data.getStringExtra("holidayImageTag"));

            mHolidayViewModel.update(holiday);

            Log.d("HolidayList", "List of all Holidays: " + mHolidayViewModel.holidayNamesToString());

        } else if (resultCode == 0) {
            Log.d("HolidayList", "Result Code is 0: " + resultCode);

        } else {
            Log.d("HolidayList", "Unregistered Result Code: " + resultCode);

        }
    }

    //----------------EVERYTHING BELOW THIS LINE HAS BEEN REDEVELOPED FOR HOLIDAY & VISITEDPLACE FUNCTIONALITY

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.holiday_action_edit:
                displayToast("Holiday Edit Selected");

                editClicked = true;
                deleteClicked = false;

                return true;

            case R.id.holiday_action_delete:
                displayToast("Holiday Delete Selected");

                editClicked = false;
                deleteClicked = true;

                return true;

            case R.id.holiday_action_deleteAll:
                displayToast("Holiday DeleteAll Selected");

                editClicked = false;
                deleteClicked = false;

                return true;

            case R.id.vplace_action_edit:
                displayToast("Vplace Edit Selected");

                editClicked = true;
                deleteClicked = false;

                return true;

            case R.id.vplace_action_delete:
                displayToast("Vplace Delete Selected");

                editClicked = false;
                deleteClicked = true;

                return true;

            case R.id.vplace_action_deleteAll:
                displayToast("Vplace DeleteAll Selected");

                editClicked = false;
                deleteClicked = false;

                return true;

        }

        editClicked = false;
        deleteClicked = false;

        return super.onOptionsItemSelected(item);
    }

    public void setupRecyclerView(){
        if (currentActive == HOLIDAY_ACTIVE) {
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerview_holiday);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            holidayListAdapter = new HolidayListAdapter(getActivity().getApplicationContext());

            Log.d("HolidayClick", "HolidayFragment, setting up recycler view");

            holidayListAdapter.setClickListener(new HolidayListAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Holiday myHoliday = holidayListAdapter.getWordAtPosition(position);

                    displayToast("You clicked an image, which is at cell position " + position);

                    Log.d("HolidayClick", "HolidayFragment, onItemClicked");

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

            recyclerView.setAdapter(holidayListAdapter);

            mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

            mHolidayViewModel.getAllHolidays().observe(this, holidays -> {
                // Update the cached copy of the words in the adapter.
                holidayListAdapter.setWords(holidays);

            });

        } else if (currentActive == VPLACE_ACTIVE) {
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerview_vPlaces);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            vPlaceListAdapter = new VPlaceListAdapter(getActivity().getApplicationContext());

            Log.d("VPlaceClick", "HolidayFragment, setting up recycler view");

            vPlaceListAdapter.setClickListener(new VPlaceListAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    VisitedPlace myVisitedPlace = vPlaceListAdapter.getWordAtPosition(position);

                    displayToast("You clicked an image, which is at cell position " + position);

                    Log.d("VPlaceClick", "HolidayFragment, onItemClicked");

                    if (editClicked == true){
//                        Intent editIntent = new Intent(getActivity(), EditHolidayActivity.class);
//                        editIntent.putExtra("chosenHoliday", myHoliday);
//
//                        startActivityForResult(editIntent, SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE);

                    } else if (deleteClicked == true){
//                        displayToast(myHoliday.getName() + " has been deleted");


                    } else {
//                        Intent viewIntent = new Intent(getActivity(), ViewHolidayActivity.class);
//                        viewIntent.putExtra("chosenHoliday", myVisitedPlace);
//
//                        startActivityForResult(viewIntent, 2);

                    }
                }

            });

            recyclerView.setAdapter(vPlaceListAdapter);

            mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

            mHolidayViewModel.getAllVisitedPlaces().observe(this, visitedplaces -> {
                // Update the cached copy of the words in the adapter.
                vPlaceListAdapter.setVPlaces(visitedplaces);

            });


        }


    }

    public void createToolbar(View view){
        setHasOptionsMenu(true);

        if (currentActive == HOLIDAY_ACTIVE) {
            currentToolbar = view.findViewById(R.id.toolbar_holidays);

            currentToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);
            currentToolbar.getMenu().getItem(1).setOnMenuItemClickListener(this);
            currentToolbar.getMenu().getItem(2).setOnMenuItemClickListener(this);

        } else if (currentActive == VPLACE_ACTIVE) {
            currentToolbar = view.findViewById(R.id.toolbar_vPlaces);

            currentToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);
            currentToolbar.getMenu().getItem(1).setOnMenuItemClickListener(this);
            currentToolbar.getMenu().getItem(2).setOnMenuItemClickListener(this);
        }


    }

    public void setupCreateButton(View view){
        if (currentActive == HOLIDAY_ACTIVE) {
            Button createHoliday_btn = view.findViewById(R.id.create_holiday_btn);

            createHoliday_btn.setOnClickListener(v -> {
                Intent createIntent = new Intent(getActivity(), CreateHolidayActivity.class);
                startActivityForResult(createIntent, NEW_HOLIDAY_ACTIVITY_REQUEST_CODE);

            });
        } else if (currentActive == VPLACE_ACTIVE) {
            Button createVPlace_btn = view.findViewById(R.id.create_visitedplaces_btn);

            createVPlace_btn.setOnClickListener(v -> {

                Intent createIntent = new Intent(getActivity(), CreateVPlaceActivity.class);
                startActivityForResult(createIntent, NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE);

            });
        }
    }

    public void setupToggle(View view){
            mToggle = view.findViewById(R.id.toggle);
            constLay_Holidays = view.findViewById(R.id.constLay_holidays);
            constLay_vPlaces = view.findViewById(R.id.constLay_vPlaces);

            mToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.toggle_holidays:
                            constLay_vPlaces.setVisibility(View.INVISIBLE);
                            constLay_Holidays.setVisibility(View.VISIBLE);

                            currentActive = HOLIDAY_ACTIVE;

                            displayToast("Holidays Toggled");

                            break;
                        case R.id.toggle_visitedPlaces:
                            constLay_Holidays.setVisibility(View.INVISIBLE);
                            constLay_vPlaces.setVisibility(View.VISIBLE);

                            currentActive = VPLACE_ACTIVE;

                            displayToast("VPlace Toggled");

                            break;
                    }

                    setupCreateButton(view); //REDEVELOPED
                    setupToggle(view); //REDEVELOPED

                    createToolbar(view);
                    setupRecyclerView();
                }

            });
        }



}