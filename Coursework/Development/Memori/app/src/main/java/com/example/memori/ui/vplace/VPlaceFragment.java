package com.example.memori.ui.vplace;

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
import com.example.memori.ui.holiday.HolidayViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class VPlaceFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private NavController navController;
    private HolidayViewModel mHolidayViewModel;
    private HolidayListAdapter holidayListAdapter;
    private VPlaceListAdapter vPlaceListAdapter;
    private Toolbar currentToolbar;

    public List<Holiday> allHolidays;
    public List<VisitedPlace> allVPlaces;
    public List<Images> allImages;

    private Boolean editClicked = false, deleteClicked = false;

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
            Intent createIntent = new Intent(getActivity(), CreateVPlaceActivity.class);

            createIntent.putExtra("holidayList", getAllHolidayNames());

            startActivityForResult(createIntent, NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE);

            super.onLongPress(e);

        }

    });

    //----------------EVERYTHING BELOW THIS LINE DOES NOT NEED REDEVELOPING FOR HOLIDAY & VISITEDPLACE FUNCTIONALITY

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHolidayViewModel = ViewModelProviders.of(this).get(HolidayViewModel.class);

        View root = inflater.inflate(R.layout.fragment_vplace, container, false);
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

        if (resultCode == NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE) {
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

            RecyclerView recyclerView = getView().findViewById(R.id.recyclerview_vPlaces);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            vPlaceListAdapter = new VPlaceListAdapter(getActivity().getApplicationContext());

            Log.d("VPlaceClick", "HolidayFragment, setting up recycler view");

            vPlaceListAdapter.setClickListener(new VPlaceListAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    VisitedPlace myVisitedPlace = vPlaceListAdapter.getWordAtPosition(position);
                    chosenVPlaceID = myVisitedPlace.get_id();


                    if (editClicked == true){
                        Intent editIntent = new Intent(getActivity(), EditVPlace.class);

                        editIntent.putExtra("chosenVisitedPlace", myVisitedPlace);
                        editIntent.putExtra("vPlaceImage", getImage(myVisitedPlace.getImageID()));
                        editIntent.putExtra("chosenHoliday", getHoliday(myVisitedPlace.getHolidayID()));
                        editIntent.putExtra("holidayNameList", getAllHolidayNames());

                        startActivityForResult(editIntent, SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE);

                    } else if (deleteClicked == true){
                        String name = myVisitedPlace.getName();

                        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
                        pictureDialog.setTitle("Are you sure you would like to delete " + name + "?");
                        String[] pictureDialogItems = {
                                "Yes",
                                "No"};
                        pictureDialog.setItems(pictureDialogItems,
                                (dialog, which) -> {
                                    switch (which) {
                                        case 0:
                                            Images vPlaceImage = getImage(myVisitedPlace.getImageID());
                                            mHolidayViewModel.deleteImage(vPlaceImage);

                                            mHolidayViewModel.deleteVisitedPlace(myVisitedPlace);

                                            displayToast(name + " has been deleted");

                                            break;
                                        case 1:

                                            break;
                                    }
                                });
                        pictureDialog.show();

                    } else {
                        Intent viewIntent = new Intent(getActivity(), ViewVPlace.class);
                        viewIntent.putExtra("chosenVisitedPlace", myVisitedPlace);

                        Log.d("FindImage", "1 myVisitedPlace.getImageID(): "+ myVisitedPlace.getImageID());
                        Log.d("FindImage", "1 getImage(myVisitedPlace.getImageID()): " + getImage(myVisitedPlace.getImageID()));
                        viewIntent.putExtra("chosenImage", getImage(myVisitedPlace.getImageID()));

                        startActivity(viewIntent);

                    }
                }

            });

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

                recyclerView.setAdapter(vPlaceListAdapter);
                vPlaceListAdapter.setVPlaces(vplaces);

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
        currentToolbar = view.findViewById(R.id.toolbar_vplaces);

        currentToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);
        currentToolbar.getMenu().getItem(1).setOnMenuItemClickListener(this);

    }

    public void setupCreateButton(View view){
        Button createVPlace_btn = view.findViewById(R.id.create_visitedplaces_btn);

        createVPlace_btn.setOnClickListener(v -> {

            Intent createIntent = new Intent(getActivity(), CreateVPlaceActivity.class);

            createIntent.putExtra("holidayList", getAllHolidayNames());

            startActivityForResult(createIntent, NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE);

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