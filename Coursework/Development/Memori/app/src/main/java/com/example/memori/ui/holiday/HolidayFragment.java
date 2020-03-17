package com.example.memori.ui.holiday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HolidayFragment extends Fragment implements MenuItem.OnMenuItemClickListener{

    private NavController navController;
    private HolidayViewModel mHolidayViewModel;
    private HolidayListAdapter holidayListAdapter;
    private VPlaceListAdapter vPlaceListAdapter;
    private Toolbar currentToolbar;

    public List<Holiday> allHolidays;
    public List<VisitedPlace> allVPlaces;
    public List<Images> allImages;

    private Boolean editClicked = false, deleteClicked = false;

    public static final int NEW_HOLIDAY_ACTIVITY_REQUEST_CODE = 1;
    public static final int VIEW_ALL_HOLIDAYS_ACTIVITY_REQUEST_CODE = 2;
    public static final int SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE = 3;

    public static final int NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 4;
    public static final int VIEW_ALL_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 5;
    public static final int SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 6;

    public int chosenHolidayID = -1;
    public int chosenVPlaceID = -1;

    public ArrayList<Integer> vPlaceIDPerHoliday;

    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onDown(MotionEvent e) {
            displayToast("Don't hold me for too long...");
            Log.d("GestureDetect", "onDown...");

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Intent createIntent = new Intent(getActivity(), CreateHolidayActivity.class);

            startActivityForResult(createIntent, NEW_HOLIDAY_ACTIVITY_REQUEST_CODE);

            super.onLongPress(e);

        }

    });

    //----------------EVERYTHING BELOW THIS LINE DOES NOT NEED REDEVELOPING FOR HOLIDAY & VISITEDPLACE FUNCTIONALITY

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

        View root = inflater.inflate(R.layout.fragment_holiday, container, false);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        retrieveTables();

        vPlaceIDPerHoliday = new ArrayList<>();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        createToolbar(view);

        setupCreateButton(view);

        setupRecyclerView();

    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveTables();

        vPlaceIDPerHoliday = new ArrayList<>();

        navController = Navigation.findNavController(getView());

        createToolbar(getView());

        setupCreateButton(getView());

        setupRecyclerView();
    }

    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    //----------------EVERYTHING BELOW THIS LINE NEEDS REDEVELOPING FOR HOLIDAY & VISITEDPLACE FUNCTIONALITY

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == NEW_HOLIDAY_ACTIVITY_REQUEST_CODE){
            Images image =  (Images) data.getSerializableExtra("newImage");

            mHolidayViewModel.insertImage(image);

            HolidayViewModel newHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);
            int imageID = newHolidayViewModel.getLatestImage().get_id() + 1;

            if (image !=null) {
                Holiday holiday = new Holiday(data.getStringExtra("hName"),
                        data.getStringExtra("hStartDate"),
                        data.getStringExtra("hEndDate"),
                        data.getStringExtra("hCompanions"),
                        data.getStringExtra("hNotes"),
                        imageID);

                mHolidayViewModel.insertHoliday(holiday);

            } else {
                Holiday holiday = new Holiday(data.getStringExtra("hName"),
                        data.getStringExtra("hStartDate"),
                        data.getStringExtra("hEndDate"),
                        data.getStringExtra("hCompanions"),
                        data.getStringExtra("hNotes"),
                        0);

                mHolidayViewModel.insertHoliday(holiday);

            }

            Log.d("HolidayList", "Holiday Saved");


        } else if (resultCode == SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE) {
            Holiday holiday = (Holiday) data.getSerializableExtra("editedHoliday");
            Images image = (Images) data.getSerializableExtra("editedImage");

            mHolidayViewModel.updateHoliday(holiday);
            mHolidayViewModel.updateImage(image);

            Log.d("HolidayList", "List of all Holidays: " + mHolidayViewModel.holidayNamesToString());

        } else if (resultCode == NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE) {
            Images image = (Images) data.getSerializableExtra("vImage");
            mHolidayViewModel.insertImage(image);

            HolidayViewModel newHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);
            int imageID = newHolidayViewModel.getLatestImage().get_id() + 1;

            if (image !=null) {
                VisitedPlace visitedPlace = new VisitedPlace(getHoliday(getHolidayID(data.getStringExtra("vPlaceHolidayName"))).get_id(),
                        data.getStringExtra("vPlaceName"),
                        data.getStringExtra("vPlaceDate"),
                        data.getStringExtra("vPlaceLocation"),
                        data.getStringExtra("vPlaceCompanions"),
                        data.getStringExtra("vPlaceNotes"),
                        imageID);

                mHolidayViewModel.insertVisitedPlace(visitedPlace);

            } else {
                VisitedPlace visitedPlace = new VisitedPlace(getHoliday(getHolidayID(data.getStringExtra("vPlaceHolidayName"))).get_id(),
                        data.getStringExtra("vPlaceName"),
                        data.getStringExtra("vPlaceDate"),
                        data.getStringExtra("vPlaceLocation"),
                        data.getStringExtra("vPlaceCompanions"),
                        data.getStringExtra("vPlaceNotes"),
                        0);

                mHolidayViewModel.insertVisitedPlace(visitedPlace);

            }

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
                displayToast("Select the Holiday you would like to edit");

                editClicked = true;
                deleteClicked = false;

                return true;

            case R.id.holiday_action_delete:
                displayToast("Select the Holiday you would like to delete");

                editClicked = false;
                deleteClicked = true;

                return true;

            case R.id.vplace_action_edit:
                displayToast("Select the Visited Place you would like to edit");

                editClicked = true;
                deleteClicked = false;

                return true;

            case R.id.vplace_action_delete:
                displayToast("Select the Visited Place you would like to delete");

                editClicked = false;
                deleteClicked = true;

                return true;

        }

        editClicked = false;
        deleteClicked = false;

        return super.onOptionsItemSelected(item);
    }

    public void setupRecyclerView(){
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerview_holiday);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            holidayListAdapter = new HolidayListAdapter(getActivity().getApplicationContext());

            Log.d("HolidayClick", "HolidayFragment, setting up recycler view");

            holidayListAdapter.setClickListener(new HolidayListAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Holiday myHoliday = holidayListAdapter.getWordAtPosition(position);
                    chosenHolidayID = myHoliday.get_id();

                    Log.d("HolidayClick", "HolidayFragment, onItemClicked");

                    if (editClicked == true){
                        Intent editIntent = new Intent(getActivity(), EditHolidayActivity.class);
                        editIntent.putExtra("chosenHoliday", myHoliday);
                        editIntent.putExtra("chosenHolidayImage", getImage(myHoliday.getImageID()));

                        startActivityForResult(editIntent, SUCCESSFULY_EDITED_HOLIDAY_ACTIVITY_REQUEST_CODE);

                    } else if (deleteClicked == true){
                        String name = myHoliday.getName();

                        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
                        pictureDialog.setTitle("Are you sure you would like to delete " + name + "?");
                        String[] pictureDialogItems = {
                                "Yes",
                                "No"};
                        pictureDialog.setItems(pictureDialogItems,
                                (dialog, which) -> {
                                    switch (which) {
                                        case 0:
                                            Images vPlaceImage = getImage(myHoliday.getImageID());
                                            mHolidayViewModel.deleteImage(vPlaceImage);

                                            mHolidayViewModel.deleteHoliday(myHoliday);

                                            displayToast(name + " has been deleted");

                                            break;
                                        case 1:

                                            break;
                                    }
                                });
                        pictureDialog.show();

                        editClicked = false;
                        deleteClicked = false;

                    } else {
                        Intent viewIntent = new Intent(getActivity(), ViewHolidayActivity.class);
                        viewIntent.putExtra("chosenHoliday", myHoliday);

                        viewIntent.putExtra("chosenImage", getImage(myHoliday.getImageID()));
                        viewIntent.putIntegerArrayListExtra("vPlaceArrayID", vPlaceIDPerHoliday);

                        Bundle bundle = getAllVPlacesForHoliday(myHoliday);
                        viewIntent.putExtra("bundle", bundle);


                        viewIntent.putExtra("chosenVisitedPlaceImage", getImage(myHoliday.getImageID()));

                        startActivity(viewIntent);

                    }
                }

            });

            mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

            mHolidayViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
                // Update the cached copy of the words in the adapter.
                allHolidays = holidays;

                recyclerView.setAdapter(holidayListAdapter);

                holidayListAdapter.setWords(holidays);

            });

            mHolidayViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
                // Update the cached copy of the words in the adapter.
                allImages = images;

            });

            mHolidayViewModel.getAllVisitedPlaces().observe(getViewLifecycleOwner(), vplaces -> {
                // Update the cached copy of the words in the adapter.
                allVPlaces = vplaces;

            });



    }

    private Bundle getAllVPlacesForHoliday(Holiday myHoliday) {
        Bundle allVPlacesBundle = new Bundle();

        Log.d("PassingList", "(HolidayFragment) allVPlaces size: " + allVPlaces.size());

        for (int i = 0; i < allVPlaces.size(); i++){
            Log.d("PassingList", "(HolidayFragment) myHoliday.get_id(): " + myHoliday.get_id());
            Log.d("PassingList", "(HolidayFragment) allVPlaces.get(" + i + ").getHolidayID(): " + allVPlaces.get(i).getHolidayID());

            if (myHoliday.get_id() == allVPlaces.get(i).getHolidayID()){
                VisitedPlace currentVPlace = allVPlaces.get(i);

                allVPlacesBundle.putSerializable("VPlaceObj" + i, currentVPlace);

                vPlaceIDPerHoliday.add(i);

                Log.d("PassingList", "(HolidayFragment) Adding VPlace to Bundle Object: VPlaceObj" + i + ", " + currentVPlace.get_id());
            }

        }

        return allVPlacesBundle;

    }

    public void createToolbar(View view){
        setHasOptionsMenu(true);

            currentToolbar = view.findViewById(R.id.toolbar_holidays);

            currentToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);
            currentToolbar.getMenu().getItem(1).setOnMenuItemClickListener(this);


    }

    public void setupCreateButton(View view){
        Button createHoliday_btn = view.findViewById(R.id.create_holiday_btn);

        createHoliday_btn.setOnClickListener(v -> {
            Intent createIntent = new Intent(getActivity(), CreateHolidayActivity.class);

            startActivityForResult(createIntent, NEW_HOLIDAY_ACTIVITY_REQUEST_CODE);

        });

    }

    //----------------DATABASE FUNCTIONS

    public void retrieveTables(){
        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

        mHolidayViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
            // Update the cached copy of the words in the adapter.
            allHolidays = holidays;

        });

        mHolidayViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            // Update the cached copy of the words in the adapter.
            allImages = images;

        });

        mHolidayViewModel.getAllVisitedPlaces().observe(getViewLifecycleOwner(), vplaces -> {
            // Update the cached copy of the words in the adapter.
            allVPlaces = vplaces;

        });
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

    //----------------IMAGE FUNCTIONS

    public ArrayList getAllImageIDs(){
        ArrayList<Integer> imageIDs = new ArrayList<>();

        for (Images currentImage : allImages) {
            Log.d("ChoosingHoliday", "HolidayFragment: currentImage.get_id(): " + currentImage.get_id());

            imageIDs.add(currentImage.get_id());
        }

        return imageIDs;
    }

    public Images getLatestImage(){
        ArrayList<Integer> imageIDs = getAllImageIDs();
        int biggestImageID = 0;

        for (Integer currentImageID : imageIDs){
            if (currentImageID > biggestImageID){
                biggestImageID = currentImageID;
            }
        }

        return getImage(biggestImageID);
    }

    public Images getImage(int id){
        Images chosenImage = null;

        Log.d("FindImage", "allImages.size(): " + allImages.size());

        for(Images currentImage : allImages){

            Log.d("FindImage", "Comparison: " + currentImage.get_id() + " == " + id);

            if (currentImage.get_id() == id){
                chosenImage = currentImage;

                Log.d("FindImage", "ImageFound");

            }
        }


        return chosenImage;
    }

}