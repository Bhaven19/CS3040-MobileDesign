package com.example.memori.ui.holiday;

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
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.example.memori.database.listadapters.HolidayListAdapter;
import com.example.memori.database.listadapters.VPlaceListAdapter;
import com.example.memori.ui.holiday.holidays.CreateHolidayActivity;
import com.example.memori.ui.holiday.holidays.EditHolidayActivity;
import com.example.memori.ui.holiday.holidays.ViewHolidayActivity;
import com.example.memori.ui.holiday.vplaces.CreateVPlaceActivity;
import com.example.memori.ui.holiday.vplaces.EditVPlace;
import com.example.memori.ui.holiday.vplaces.ViewVPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HolidayFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private NavController navController;
    private HolidayViewModel mHolidayViewModel;
    private HolidayListAdapter holidayListAdapter;
    private VPlaceListAdapter vPlaceListAdapter;
    private Toolbar currentToolbar;

    public List<Holiday> allHolidays;
    public List<VisitedPlace> allVPlaces;
    public List<Images> allImages;

    private RadioGroup mToggle;
    private ConstraintLayout constLay_Holidays, constLay_vPlaces;

    private Boolean editClicked = false, deleteClicked = false;

    public static final String HOLIDAY_ACTIVE = "holiday_active", VPLACE_ACTIVE = "vplace_active";
    public String currentActive = HOLIDAY_ACTIVE;

    public static final int NEW_HOLIDAY_ACTIVITY_REQUEST_CODE = 1;
    public static final int VIEW_ALL_HOLIDAYS_ACTIVITY_REQUEST_CODE = 2;
    public static final int SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE = 3;

    public static final int NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 4;
    public static final int VIEW_ALL_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 5;
    public static final int SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 6;

    public int chosenHolidayID = -1;
    public int chosenVPlaceID = -1;

    //----------------EVERYTHING BELOW THIS LINE DOES NOT NEED REDEVELOPING FOR HOLIDAY & VISITEDPLACE FUNCTIONALITY

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);
        View root = inflater.inflate(R.layout.fragment_holiday, container, false);

        retrieveTables();

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
            Holiday holiday =  (Holiday) data.getSerializableExtra("newHoliday");
            Images image =  (Images) data.getSerializableExtra("newImage");

            Log.d("HolidayList", "HolidayFragment, imagePath: " + data.getStringExtra("holidayImagePath"));

            mHolidayViewModel.insert(holiday);
            mHolidayViewModel.insertImage(image);

            Log.d("HolidayList", "Holiday Saved");


        } else if (resultCode == SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE) {
            Holiday holiday = (Holiday) data.getSerializableExtra("editedHoliday");
            Images image = (Images) data.getSerializableExtra("editedImage");

            mHolidayViewModel.update(holiday);
            mHolidayViewModel.updateImage(image);

            Log.d("HolidayList", "List of all Holidays: " + mHolidayViewModel.holidayNamesToString());

        } else if (resultCode == NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE) {
            Images image = (Images) data.getSerializableExtra("vImage");

            VisitedPlace visitedPlace = new VisitedPlace( getHoliday( getHolidayID( data.getStringExtra("vPlaceHoliday") ) ).get_id(),
                    data.getStringExtra("vPlaceName"),
                    data.getStringExtra("vPlaceDate"),
                    data.getStringExtra("vPlaceLocation"),
                    data.getStringExtra("vPlaceCompanions"),
                    data.getStringExtra("vPlaceNotes"),
                    image.get_id());

            mHolidayViewModel.insertVisitedPlace(visitedPlace);
            mHolidayViewModel.insertImage(image);

            Log.d("VisitedPlaceStorage", "HolidayFragment: visitedPlace Stored");

        } else if (resultCode == SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE) {
            VisitedPlace visitedPlace = (VisitedPlace) data.getSerializableExtra("editedVPlace");
            visitedPlace.setHolidayID(getHolidayID(data.getStringExtra("editedVPlaceHolidayName")));

            Images newImage = (Images)  data.getSerializableExtra("editedVPlaceImage");

            Log.d("EditVPlaceSave", "HolidayFragment- vPlaceHolidayName: " + data.getStringExtra("editedVPlaceHolidayName"));
            Log.d("EditVPlaceSave", "HolidayFragment- vPlaceHoliday: " + visitedPlace);
            Log.d("EditVPlaceSave", "HolidayFragment- vPlaceImage: " + newImage);

            mHolidayViewModel.updateVisitedPlace(visitedPlace);
            mHolidayViewModel.updateImage(newImage);

        } else {
            Log.d("HolidayList", "Unregistered Result Code: " + resultCode);

        }

        editClicked = false;
        deleteClicked = false;
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
                    chosenHolidayID = myHoliday.get_id();

                    displayToast("You clicked an image, which is at cell position " + position);

                    Log.d("HolidayClick", "HolidayFragment, onItemClicked");

                    if (editClicked == true){
                        Intent editIntent = new Intent(getActivity(), EditHolidayActivity.class);
                        editIntent.putExtra("chosenHoliday", myHoliday);
                        editIntent.putExtra("chosenHolidayImage", getImage(myHoliday.getImageID()));

                        startActivityForResult(editIntent, SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE);

                    } else if (deleteClicked == true){
                        displayToast(myHoliday.getName() + " has been deleted");


                    } else {
                        Intent viewIntent = new Intent(getActivity(), ViewHolidayActivity.class);
                        viewIntent.putExtra("chosenHoliday", myHoliday);
                        viewIntent.putExtra("chosenImage", getImage(myHoliday.getImageID()));

                        startActivity(viewIntent);

                    }
                }

            });

            mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

            mHolidayViewModel.getAllHolidays().observe(this, holidays -> {
                // Update the cached copy of the words in the adapter.
                allHolidays = holidays;

                recyclerView.setAdapter(holidayListAdapter);

                holidayListAdapter.setWords(holidays);

            });

            mHolidayViewModel.getAllImages().observe(this, images -> {
                // Update the cached copy of the words in the adapter.
                allImages = images;

            });

            mHolidayViewModel.getAllVisitedPlaces().observe(this, vplaces -> {
                // Update the cached copy of the words in the adapter.
                allVPlaces = vplaces;

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
                    chosenVPlaceID = myVisitedPlace.get_id();

                    displayToast("You clicked an image, which is VPlace:" + myVisitedPlace.get_id());

                    if (editClicked == true){
                        Intent editIntent = new Intent(getActivity(), EditVPlace.class);

                        editIntent.putExtra("chosenVisitedPlace", myVisitedPlace);
                        editIntent.putExtra("vPlaceImage", getImage(myVisitedPlace.getImageID()));
                        editIntent.putExtra("chosenHoliday", getHoliday(myVisitedPlace.getHolidayID()));
                        editIntent.putExtra("holidayNameList", getAllHolidayNames());

                        startActivityForResult(editIntent, SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE);

                    } else if (deleteClicked == true){
//                        displayToast(myHoliday.getName() + " has been deleted");


                    } else {
                        Intent viewIntent = new Intent(getActivity(), ViewVPlace.class);
                        viewIntent.putExtra("chosenVisitedPlace", myVisitedPlace);
                        viewIntent.putExtra("chosenImage", getImage(myVisitedPlace.getImageID()));

                        startActivity(viewIntent);

                    }
                }

            });

            mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

            mHolidayViewModel.getAllHolidays().observe(this, holidays -> {
                // Update the cached copy of the words in the adapter.
                allHolidays = holidays;

            });

            mHolidayViewModel.getAllImages().observe(this, images -> {
                // Update the cached copy of the words in the adapter.
                allImages = images;

            });

            mHolidayViewModel.getAllVisitedPlaces().observe(this, vplaces -> {
                // Update the cached copy of the words in the adapter.
                allVPlaces = vplaces;

                recyclerView.setAdapter(vPlaceListAdapter);
                vPlaceListAdapter.setVPlaces(vplaces);

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

                createIntent.putExtra("holidayList", getAllHolidayNames());

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

    //----------------DATABASE FUNCTIONS

    public void retrieveTables(){
        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

        mHolidayViewModel.getAllHolidays().observe(this, holidays -> {
            // Update the cached copy of the words in the adapter.
            allHolidays = holidays;

        });

        mHolidayViewModel.getAllImages().observe(this, images -> {
            // Update the cached copy of the words in the adapter.
            allImages = images;

        });

        mHolidayViewModel.getAllVisitedPlaces().observe(this, vplaces -> {
            // Update the cached copy of the words in the adapter.
            allVPlaces = vplaces;

        });
    }

    public void updateAdapterViews(){

    }

    //----------------HOLIDAY FUNCTIONS

    public ArrayList getAllHolidayNames(){

        ArrayList<String> holidayNames = new ArrayList<>();

        for (Holiday currentHoliday : allHolidays) {
            Log.d("ChoosingHoliday", "HolidayFragment: currentHoliday.getName(): " + currentHoliday.getName());

            holidayNames.add(currentHoliday.getName());
        }
        return holidayNames;
    }

    public ArrayList getAllHolidays(){
        ArrayList<Holiday> holidays = new ArrayList<>();

        for (Holiday currentHoliday : allHolidays) {

            holidays.add(currentHoliday);
        }
        return holidays;
    }

    public int getHolidayID(String holidayName){
        int currentID = -1;

        for (Holiday currentHoliday : allHolidays){
            if (holidayName.equals(currentHoliday.getName())){
                currentID = currentHoliday.get_id();
            }
        }

        return currentID;
    }

    public Holiday getHoliday(int id){
        Holiday chosenHoliday = null;

        for (Holiday currentHoliday : allHolidays) {
            Log.d("SpinnerHoliday", "currentHoliday.get_id(): " + currentHoliday.get_id());
            Log.d("SpinnerHoliday", "id: " + id);
            if (currentHoliday.get_id() == id){
                chosenHoliday = currentHoliday;

                Log.d("SpinnerHoliday", "HolidayFound");
            } else {
                Log.d("SpinnerHoliday", "HolidayNotFound");

            }
        }
        return chosenHoliday;
    }

    //----------------VPLACE FUNCTIONS

    public ArrayList getAllVPlaces(){
        ArrayList<String> vPlaceNames = new ArrayList<>();

        for (VisitedPlace currentVPlace : allVPlaces) {
            Log.d("ChoosingHoliday", "HolidayFragment: currentHoliday.getName(): " + currentVPlace.getName());

            vPlaceNames.add(currentVPlace.getName());
        }
        return vPlaceNames;
    }

    public int getVPlaceID(String vPlaceName){
        int currentID = -1;

        for (VisitedPlace currentVPlace : allVPlaces){
            if (vPlaceName.equals(currentVPlace.getName())){
                currentID = currentVPlace.get_id();
            }
        }

        return currentID;
    }

    public VisitedPlace getVPlace(int id){
        VisitedPlace chosenVPlace = null;

        for (VisitedPlace currentVPlace : allVPlaces) {
            Log.d("SpinnerHoliday", "currentHoliday.get_id(): " + currentVPlace.get_id());
            Log.d("SpinnerHoliday", "id: " + id);
            if (currentVPlace.get_id() == id){
                chosenVPlace = currentVPlace;

                Log.d("SpinnerHoliday", "HolidayFound");
            } else {
                Log.d("SpinnerHoliday", "HolidayNotFound");

            }
        }
        return chosenVPlace;
    }

    //----------------HOLIDAY FUNCTIONS

    public ArrayList getAllImagess(){
        ArrayList<Integer> imageIDs = new ArrayList<>();

        for (Images currentImage : allImages) {
            Log.d("ChoosingHoliday", "HolidayFragment: currentImage.get_id(): " + currentImage.get_id());

            imageIDs.add(currentImage.get_id());
        }
        return imageIDs;
    }

    public Images getImage(int id){
        Images chosenImage = null;

        for (Images currentImage : allImages) {
            if (currentImage.get_id() == id){
                chosenImage = currentImage;

                Log.d("SpinnerHoliday", "HolidayFound");
            } else {
                Log.d("SpinnerHoliday", "HolidayNotFound");

            }
        }
        return chosenImage;
    }

}