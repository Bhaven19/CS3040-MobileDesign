package com.example.memori.ui.gallery;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.components.HolidayDate;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.example.memori.database.listadapters.GalleryImageListAdapter;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GalleryFragment extends Fragment implements MenuItem.OnMenuItemClickListener, View.OnClickListener {

    private View view;
    private GalleryViewModel galleryViewModel;
    private GalleryImageListAdapter galleryImageListAdapter;
    private Toolbar gToolbar;

    private static final int DO_NOT_SORT = 0;
    private static final int SORT_OPTIONS_HOLIDAY_NAME_A_TO_Z = 1;
    private static final int SORT_OPTIONS_HOLIDAY_NAME_Z_TO_A = 2;
    private static final int SORT_OPTIONS_VPLACE_NAME_A_TO_Z = 3;
    private static final int SORT_OPTIONS_VPLACE_NAME_Z_TO_A = 4;
    private static final int SORT_OPTIONS_LOCATION_A_TO_Z = 5;
    private static final int SORT_OPTIONS_LOCATION_Z_TO_A = 6;
    private static final int SORT_OPTIONS_DATE_OLD_TO_NEW = 7;
    private static final int SORT_OPTIONS_DATE_NEW_TO_OLD = 8;
    private static final int SORT_OPTIONS_TAG_A_TO_Z = 9;
    private static final int SORT_OPTIONS_TAG_Z_TO_A = 10;

    private List<Images> mAllImages;
    private List<Holiday> mAllHolidays;
    private List<VisitedPlace> mAllVPlaces;

    private HashMap<String, Integer> hmNameToID;
    private HashMap<Images, Place> hmImageToPlace;
    private int i;

    private ConstraintLayout constLay_searchDate, constLay_searchTag, constLay_searchHoliday, constLay_searchVPlace, constLay_searchLocation;
    private Button btn_searchGallery, btn_searchByDate, btn_searchGetDate, btn_searchByTag, btn_searchByHoliday, btn_searchByVPlace, btn_searchByLocation;
    private EditText edit_date, edit_tag, edit_holiday, edit_vPlace, edit_location;

    private final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Boolean validDate = false;
    private HolidayDate searchDate;
    private List<Images> originalImages;
    private ArrayList<Images> filteredImages;

    private boolean firstPass = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        retrieveTables();

        getViewIDs();

        i = 0;
        hmImageToPlace = new HashMap<>();

        obtainAll();

        createToolbar(view);


    }

    @Override
    public void onResume() {
        super.onResume();

        firstPass = false;

        retrieveTables();

        getViewIDs();

        i = 0;
        hmImageToPlace = new HashMap<>();

        obtainAll();

        createToolbar(getView());

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_holiday) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
            pictureDialog.setTitle("What order would you like to sort the Holiday Images by?");
            String[] pictureDialogItems = {
                    "A to Z",
                    "Z to A"};
            pictureDialog.setItems(pictureDialogItems,
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Log.d("OrderGallery", "Sort By Name ASC");

                                setUpRecyclerView(SORT_OPTIONS_HOLIDAY_NAME_A_TO_Z, filteredImages);

                                break;
                            case 1:
                                Log.d("OrderGallery", "Sort By Name DESC");

                                setUpRecyclerView(SORT_OPTIONS_HOLIDAY_NAME_Z_TO_A, filteredImages);
                                break;
                        }
                    });
            pictureDialog.show();

            return true;

        } else if (id == R.id.action_sort_vplace) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
            pictureDialog.setTitle("What order would you like to sort the Visited Places images by?");
            String[] pictureDialogItems = {
                    "A to Z",
                    "Z to A"};
            pictureDialog.setItems(pictureDialogItems,
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Log.d("OrderGallery", "Sort By Date Oldest To Newest");

                                setUpRecyclerView(SORT_OPTIONS_VPLACE_NAME_A_TO_Z, filteredImages);

                                break;
                            case 1:
                                Log.d("OrderGallery", "Sort By Date Newest To Oldest");

                                setUpRecyclerView(SORT_OPTIONS_VPLACE_NAME_Z_TO_A, filteredImages);
                                break;
                        }
                    });
            pictureDialog.show();

            return true;

        } else if (id == R.id.action_sort_date) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
            pictureDialog.setTitle("What order would you like to sort the Image dates by?");
            String[] pictureDialogItems = {
                    "Oldest to Newest",
                    "Newest to Oldest"};
            pictureDialog.setItems(pictureDialogItems,
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Log.d("OrderGallery", "Sort By Date Oldest To Newest");

                                setUpRecyclerView(SORT_OPTIONS_DATE_OLD_TO_NEW, filteredImages);

                                break;
                            case 1:
                                Log.d("OrderGallery", "Sort By Date Newest To Oldest");

                                setUpRecyclerView(SORT_OPTIONS_DATE_NEW_TO_OLD, filteredImages);
                                break;
                        }
                    });
            pictureDialog.show();

            return true;

        } else if (id == R.id.action_sort_location) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
            pictureDialog.setTitle("What order would you like to sort the Image locations by?");
            String[] pictureDialogItems = {
                    "A to Z",
                    "Z to A"};
            pictureDialog.setItems(pictureDialogItems,
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Log.d("OrderGallery", "Sort By Location AZ");

                                setUpRecyclerView(SORT_OPTIONS_LOCATION_A_TO_Z, filteredImages);

                                break;
                            case 1:
                                Log.d("OrderGallery", "Sort By Location ZA");

                                setUpRecyclerView(SORT_OPTIONS_LOCATION_Z_TO_A, filteredImages);
                                break;
                        }
                    });
            pictureDialog.show();

            return true;

        } else if (id == R.id.action_sort_tag) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
            pictureDialog.setTitle("What order would you like to sort the Image tags by?");
            String[] pictureDialogItems = {
                    "A to Z",
                    "Z to A"};
            pictureDialog.setItems(pictureDialogItems,
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Log.d("OrderGallery", "Sort By Tag ASC");

                                setUpRecyclerView(SORT_OPTIONS_TAG_A_TO_Z, filteredImages);

                                break;
                            case 1:
                                Log.d("OrderGallery", "Sort By Tag DESC");

                                setUpRecyclerView(SORT_OPTIONS_TAG_Z_TO_A, filteredImages);
                                break;
                        }
                    });
            pictureDialog.show();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        return root;
    }

    public void getViewIDs(){
        constLay_searchDate = view.findViewById(R.id.constLay_searchDate);
        constLay_searchTag = view.findViewById(R.id.constLay_searchTag);
        constLay_searchHoliday = view.findViewById(R.id.constLay_searchHoliday);
        constLay_searchVPlace = view.findViewById(R.id.constLay_searchVPlace);
        constLay_searchLocation = view.findViewById(R.id.constLay_searchLocation);

        btn_searchGallery = view.findViewById(R.id.btn_searchGallery);
        btn_searchByDate = view.findViewById(R.id.btn_searchByDate);
        btn_searchGetDate = view.findViewById(R.id.btn_searchGetDate);
        btn_searchByTag = view.findViewById(R.id.btn_searchByTag);
        btn_searchByHoliday = view.findViewById(R.id.btn_searchByHoliday);
        btn_searchByVPlace = view.findViewById(R.id.btn_searchByVPlace);
        btn_searchByLocation = view.findViewById(R.id.btn_searchByLocation);

        edit_date = view.findViewById(R.id.edit_searchDate);
        edit_date.setEnabled(false);

        edit_tag = view.findViewById(R.id.edit_searchTag);
        edit_holiday = view.findViewById(R.id.edit_searchHoliday);
        edit_vPlace = view.findViewById(R.id.edit_searchVPlace);
        edit_location = view.findViewById(R.id.edit_searchLocation);

        btn_searchGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConstLayView("clear");

                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
                pictureDialog.setTitle("How would you like to search the gallery?");
                String[] pictureDialogItems = {
                        "By Date",
                        "By Tag",
                        "By Holiday",
                        "By Visited Place",
                        "By Location",
                        "Reset Search"};
                pictureDialog.setItems(pictureDialogItems,
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    setConstLayView("date");

                                    break;
                                case 1:
                                    setConstLayView("tag");

                                    break;
                                case 2:
                                    setConstLayView("holiday");

                                    break;
                                case 3:
                                    setConstLayView("vplace");

                                    break;
                                case 4:
                                    setConstLayView("location");

                                    break;
                                case 5:
                                    setConstLayView("reset");

                                    break;
                            }
                        });
                pictureDialog.show();
            }
        });

        btn_searchGetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                validDate = false;

                DatePickerDialog endDatePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                searchDate = new HolidayDate(dayOfMonth, monthOfYear + 1, year);

                                edit_date.setText(searchDate.toString());

                            }
                        }, mYear, mMonth, mDay);

                endDatePickerDialog.show();
            }
        });

        btn_searchByDate.setOnClickListener(this);
        btn_searchByTag.setOnClickListener(this);
        btn_searchByHoliday.setOnClickListener(this);
        btn_searchByVPlace.setOnClickListener(this);
        btn_searchByLocation.setOnClickListener(this);


    }

    private void setUpRecyclerView(int sortOption, List<Images> impImages) {
        // set up the RecyclerView
        RecyclerView recyclerView = getView().findViewById(R.id.gallery_recyclerview);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        galleryImageListAdapter = new GalleryImageListAdapter(getContext());

        galleryImageListAdapter.setClickListener(new GalleryImageListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Images myImage = galleryImageListAdapter.getImageAtPosition(position);

                Log.d("ImageClick", "-------------------");
                Log.d("ImageClick", "GalleryFragment, onItemClick imageID: " + myImage.get_id() );
                Log.d("ImageClick", "GalleryFragment, onItemClick imageDate: " + myImage.getDate() );
                Log.d("ImageClick", "GalleryFragment, onItemClick imageTag: " + myImage.getTag() );
                Log.d("ImageClick", "-------------------");

                displayToast("You clicked an image, which is at cell position " + position);

            }
        });

        recyclerView.setAdapter(galleryImageListAdapter);
        galleryImageListAdapter.setImages(sortBy(sortOption, impImages));


    }

    private void createToolbar(View view){
        setHasOptionsMenu(true);
        gToolbar = view.findViewById(R.id.toolbar_holidays);

        gToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);
        gToolbar.getMenu().getItem(1).setOnMenuItemClickListener(this);
        gToolbar.getMenu().getItem(2).setOnMenuItemClickListener(this);
        gToolbar.getMenu().getItem(3).setOnMenuItemClickListener(this);
        gToolbar.getMenu().getItem(4).setOnMenuItemClickListener(this);

    }

    private List<Images> sortBy(int option, List<Images> mImages){
        Log.d("OrderGallery", "GalleryFragment: Sorting");

        ArrayList<Images> images = new ArrayList<>();
        ArrayList<String> imageSortField = new ArrayList<>();

        obtainAll();

        switch (option) {
            case SORT_OPTIONS_HOLIDAY_NAME_A_TO_Z:
                Log.d("OrderGallery", "GalleryFragment: Sorting Name ASC");

                images.clear();
                imageSortField.clear();

                accessAllHolidayImagesNames();

                for (HashMap.Entry<String, Integer> currentPair : hmNameToID.entrySet()){
                    imageSortField.add(currentPair.getKey());

                    Log.d("OrderGallery", "currentPair.getKey(): " + currentPair.getKey());
                }

                Collections.sort(imageSortField);
                Log.d("OrderGallery", "------SORTED-------");

                for (String currentImageName : imageSortField){
                    Log.d("OrderGallery", "currentImageName: " + currentImageName);

                    Images currentImage = getImage(hmNameToID.get(currentImageName));

                    images.add(currentImage);

                }

                return images;

            case SORT_OPTIONS_HOLIDAY_NAME_Z_TO_A:
                Log.d("OrderGallery", "GalleryFragment: Sorting Name DESC");

                images.clear();
                imageSortField.clear();

                accessAllHolidayImagesNames();

                for (HashMap.Entry<String, Integer> currentPair : hmNameToID.entrySet()){
                    imageSortField.add(currentPair.getKey());

                    Log.d("OrderGallery", "currentPair.getKey(): " + currentPair.getKey());
                }

                Collections.sort(imageSortField);
                Collections.reverse(imageSortField);
                Log.d("OrderGallery", "------SORTED-------");

                for (String currentImageName : imageSortField){
                    Log.d("OrderGallery", "currentImageName: " + currentImageName);

                    Images currentImage = getImage(hmNameToID.get(currentImageName));

                    images.add(currentImage);

                }

                return images;

            case SORT_OPTIONS_VPLACE_NAME_A_TO_Z:
                Log.d("OrderGallery", "GalleryFragment: Sorting Name ASC");

                images.clear();
                imageSortField.clear();

                accessAllVPlaceImagesNames();

                for (HashMap.Entry<String, Integer> currentPair : hmNameToID.entrySet()){
                    imageSortField.add(currentPair.getKey());

                    Log.d("OrderGallery", "currentPair.getKey(): " + currentPair.getKey());
                }

                Collections.sort(imageSortField);
                Log.d("OrderGallery", "------SORTED-------");

                for (String currentImageName : imageSortField){
                    Log.d("OrderGallery", "currentImageName: " + currentImageName);

                    Images currentImage = getImage(hmNameToID.get(currentImageName));

                    images.add(currentImage);

                }

                return images;

            case SORT_OPTIONS_VPLACE_NAME_Z_TO_A:
                Log.d("OrderGallery", "GalleryFragment: Sorting Name DESC");

                images.clear();
                imageSortField.clear();

                accessAllVPlaceImagesNames();

                for (HashMap.Entry<String, Integer> currentPair : hmNameToID.entrySet()){
                    imageSortField.add(currentPair.getKey());

                    Log.d("OrderGallery", "currentPair.getKey(): " + currentPair.getKey());
                }

                Collections.sort(imageSortField);
                Collections.reverse(imageSortField);
                Log.d("OrderGallery", "------SORTED-------");

                for (String currentImageName : imageSortField){
                    Log.d("OrderGallery", "currentImageName: " + currentImageName);

                    Images currentImage = getImage(hmNameToID.get(currentImageName));

                    images.add(currentImage);

                }

                return images;

            case SORT_OPTIONS_DATE_OLD_TO_NEW:
                Log.d("OrderGallery", "GalleryFragment: Sorting Date Old to New");
                images.clear();
                imageSortField.clear();

                ArrayList<Date> imageONDates = new ArrayList<>();

                HashMap<Date, Images> hmONImageToString = new HashMap<>();

                for (Images currentImage : mImages){
                    String currentDateString = currentImage.getDate();

                    Log.d("OrderGallery", "------------------");

                    int currentYear = Integer.parseInt(currentDateString.substring(currentDateString.length() - 4));
                    int currentMonth = Integer.parseInt(currentDateString.substring(3, 5));
                    int currentDay = Integer.parseInt(currentDateString.substring(0, 2));

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(currentYear, currentMonth - 1, currentDay);

                    imageONDates.add(calendar.getTime());
                    hmONImageToString.put(calendar.getTime(), currentImage);

                }

                Collections.sort(imageONDates);
                Log.d("OrderGallery", "------SORTED-------");

                for (Date currentImageDate : imageONDates){
                    Log.d("OrderGallery", "currentImageDate: " + currentImageDate);

                    Images currentImage = hmONImageToString.get(currentImageDate);

                    images.add(currentImage);

                }

                return images;

            case SORT_OPTIONS_DATE_NEW_TO_OLD:
                Log.d("OrderGallery", "GalleryFragment: Sorting Date Old to New");
                images.clear();
                imageSortField.clear();

                ArrayList<Date> imageNODates = new ArrayList<>();

                HashMap<Date, Images> hmNOImageToString = new HashMap<>();

                for (Images currentImage : mImages){
                    String currentDateString = currentImage.getDate();

                    int currentYear = Integer.parseInt(currentDateString.substring(currentDateString.length() - 4));
                    int currentMonth = Integer.parseInt(currentDateString.substring(3, 5));
                    int currentDay = Integer.parseInt(currentDateString.substring(0, 2));

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(currentYear, currentMonth - 1, currentDay);

                    imageNODates.add(calendar.getTime());
                    hmNOImageToString.put(calendar.getTime(), currentImage);

                }

                Collections.sort(imageNODates);
                Collections.reverse(imageNODates);
                Log.d("OrderGallery", "------SORTED-------");

                for (Date currentImageDate : imageNODates){
                    Log.d("OrderGallery", "currentImageDate: " + currentImageDate);

                    Images currentImage = hmNOImageToString.get(currentImageDate);

                    images.add(currentImage);

                }

                return images;

            case SORT_OPTIONS_TAG_A_TO_Z:
                ArrayList<String> hAZTagsNames = new ArrayList<>();

                for (Images currentImage : mImages){
                    hAZTagsNames.add(currentImage.getTag());

                    Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());
                }

                Collections.sort(hAZTagsNames);
                Log.d("OrderGallery", "----------SORTED----------");

                for (String currentHolidayTag : hAZTagsNames) {
                    for (Images currentImage : mImages) {
                        if (currentHolidayTag == currentImage.getTag()) {
                            images.add(currentImage);

                            Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());

                        }

                    }
                }

                return images;

            case SORT_OPTIONS_TAG_Z_TO_A:
                ArrayList<String> hZATagsNames = new ArrayList<>();

                for (Images currentImage : mImages){
                    hZATagsNames.add(currentImage.getTag());

                    Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());
                }

                Collections.sort(hZATagsNames);
                Collections.reverse(hZATagsNames);
                Log.d("OrderGallery", "----------SORTED----------");

                for (String currentHolidayTag : hZATagsNames) {
                    for (Images currentImage : mImages) {
                        if (currentHolidayTag == currentImage.getTag()) {
                            images.add(currentImage);

                            Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());

                        }

                    }
                }

                return images;

            case SORT_OPTIONS_LOCATION_A_TO_Z:
                ArrayList<String> hAZLocationsNames = new ArrayList<>();

                for (Images currentImage : mImages){
                    hAZLocationsNames.add(hmImageToPlace.get(currentImage).getName());

                    Log.d("OrderGallery", "getPlaceName(currentImage.getLocation()): " + hmImageToPlace.get(currentImage).getName());
                }

                Collections.sort(hAZLocationsNames);
                Log.d("OrderGallery", "----------SORTED----------");

                for (String currentPlaceName : hAZLocationsNames) {
                    for (Map.Entry<Images, Place> currentPlace : hmImageToPlace.entrySet()) {
                        if (currentPlace.getValue().getName().equals(currentPlaceName)) {
                            images.add(currentPlace.getKey());

                            Log.d("OrderGallery", "getPlaceName(currentImage.getLocation()): " + currentPlace.getValue().getName());

                        }

                    }
                }

                return images;

            case SORT_OPTIONS_LOCATION_Z_TO_A:
                ArrayList<String> hZALocationsNames = new ArrayList<>();

                for (Images currentImage : mImages){
                    hZALocationsNames.add(hmImageToPlace.get(currentImage).getName());

                    Log.d("OrderGallery", "getPlaceName(currentImage.getLocation()): " + hmImageToPlace.get(currentImage).getName());
                }

                Collections.sort(hZALocationsNames);
                Collections.reverse(hZALocationsNames);
                Log.d("OrderGallery", "----------SORTED----------");

                for (String currentPlaceName : hZALocationsNames) {
                    for (Map.Entry<Images, Place> currentPlace : hmImageToPlace.entrySet()) {
                        if (currentPlace.getValue().getName().equals(currentPlaceName)) {
                            images.add(currentPlace.getKey());

                            Log.d("OrderGallery", "getPlaceName(currentImage.getLocation()): " + currentPlace.getValue().getName());

                        }

                    }
                }

                return images;
            case DO_NOT_SORT:
                return mImages;
        }

        return mImages;

    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void accessAllHolidayImagesNames(){
        hmNameToID = new HashMap<>();

        hmNameToID.clear();

        Log.d("OrderGallery", "mAllHolidays.size(): " + mAllHolidays.size());
        Log.d("OrderGallery", "mAllVPlaces.size(): " + mAllVPlaces.size());

        for (Holiday currentHoliday : mAllHolidays){
            hmNameToID.put(currentHoliday.getName(), currentHoliday.getImageID());
        }

    }

    private void accessAllVPlaceImagesNames(){
        hmNameToID = new HashMap<>();

        hmNameToID.clear();

        Log.d("OrderGallery", "mAllHolidays.size(): " + mAllHolidays.size());
        Log.d("OrderGallery", "mAllVPlaces.size(): " + mAllVPlaces.size());

        for (VisitedPlace currentVPlace : mAllVPlaces){
            hmNameToID.put(currentVPlace.getName(), currentVPlace.getImageID());
        }

    }

    public void retrieveTables(){
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        galleryViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
            // Update the cached copy of the words in the adapter.
            mAllHolidays = holidays;

        });

        galleryViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            // Update the cached copy of the words in the adapter.
            mAllImages = images;
            originalImages = images;


            if (!firstPass) {
                setUpRecyclerView(DO_NOT_SORT, mAllImages);
                firstPass = true;
            }
        });

        galleryViewModel.getAllVisitedPlaces().observe(getViewLifecycleOwner(), vplaces -> {
            // Update the cached copy of the words in the adapter.
            mAllVPlaces = vplaces;


        });
    }

    private Images getImage(int imageID){
        Images returnImg = null;

        for (Images currentImage : mAllImages) {
            if (imageID == currentImage.get_id()) {
                returnImg = currentImage;

            }
        }

        return returnImg;
    }

    private void obtainAll(){
        galleryViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
            mAllHolidays = holidays;

        });

        galleryViewModel.getAllVisitedPlaces().observe(getViewLifecycleOwner(), vplaces -> {
            mAllVPlaces = vplaces;

        });

        galleryViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            mAllImages = images;

            getAllImagePlaces();

        });
    }

    private void getAllImagePlaces(){
        if (i < mAllImages.size()) {
            if (!Places.isInitialized()) {
                Places.initialize(getActivity(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
            }

            Images currentImage = mAllImages.get(i);

            // Define a Place ID.
            String placeId = currentImage.getLocation();
            Log.d("OrderGallery", "placeId: " + placeId);

            // Specify the fields to return.s
            List<Place.Field> placeFields = (Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
            PlacesClient placesClient = Places.createClient(getActivity());
            placesClient.fetchPlace(request).addOnCompleteListener(fetchPlaceResponse -> {
                    Log.d("OrderGallery", "response.getPlace().getName(): " + fetchPlaceResponse.getResult().getPlace().getName());
                    hmImageToPlace.put(currentImage, fetchPlaceResponse.getResult().getPlace());

                    i++;

                    getAllImagePlaces();

            });
        }

    }

    public void setConstLayView(String filterType){
        switch (filterType) {
            case "date":
                constLay_searchDate.setVisibility(View.VISIBLE);
                constLay_searchTag.setVisibility(View.INVISIBLE);
                constLay_searchHoliday.setVisibility(View.INVISIBLE);
                constLay_searchVPlace.setVisibility(View.INVISIBLE);
                constLay_searchLocation.setVisibility(View.INVISIBLE);

                break;

            case "tag":
                constLay_searchDate.setVisibility(View.INVISIBLE);
                constLay_searchTag.setVisibility(View.VISIBLE);
                constLay_searchHoliday.setVisibility(View.INVISIBLE);
                constLay_searchVPlace.setVisibility(View.INVISIBLE);
                constLay_searchLocation.setVisibility(View.INVISIBLE);

                break;

            case "holiday":
                constLay_searchDate.setVisibility(View.INVISIBLE);
                constLay_searchTag.setVisibility(View.INVISIBLE);
                constLay_searchHoliday.setVisibility(View.VISIBLE);
                constLay_searchVPlace.setVisibility(View.INVISIBLE);
                constLay_searchLocation.setVisibility(View.INVISIBLE);

                break;

            case "vplace":
                constLay_searchDate.setVisibility(View.INVISIBLE);
                constLay_searchTag.setVisibility(View.INVISIBLE);
                constLay_searchHoliday.setVisibility(View.INVISIBLE);
                constLay_searchVPlace.setVisibility(View.VISIBLE);
                constLay_searchLocation.setVisibility(View.INVISIBLE);

                break;

            case "location":
                constLay_searchDate.setVisibility(View.INVISIBLE);
                constLay_searchTag.setVisibility(View.INVISIBLE);
                constLay_searchHoliday.setVisibility(View.INVISIBLE);
                constLay_searchVPlace.setVisibility(View.INVISIBLE);
                constLay_searchLocation.setVisibility(View.VISIBLE);

                break;

            case "reset":
                constLay_searchDate.setVisibility(View.INVISIBLE);
                constLay_searchTag.setVisibility(View.INVISIBLE);
                constLay_searchHoliday.setVisibility(View.INVISIBLE);
                constLay_searchVPlace.setVisibility(View.INVISIBLE);
                constLay_searchLocation.setVisibility(View.INVISIBLE);

                setUpRecyclerView(DO_NOT_SORT, mAllImages);

                break;
            case "clear":
                constLay_searchDate.setVisibility(View.INVISIBLE);
                constLay_searchTag.setVisibility(View.INVISIBLE);
                constLay_searchHoliday.setVisibility(View.INVISIBLE);
                constLay_searchVPlace.setVisibility(View.INVISIBLE);
                constLay_searchLocation.setVisibility(View.INVISIBLE);

                break;

        }

    }

    @Override
    public void onClick(View v) {
        filteredImages = new ArrayList<>();

        switch (v.getId()) {
            case R.id.btn_searchByDate:
                filteredImages.clear();

                Log.d("IDError", "edit_date.getText().toString(): " + edit_date.getText().toString());

                if (!edit_date.getText().toString().equals("")) {
                    String currentDate = searchDate.toString();

                    for (Images currentImage : mAllImages){
                        if (currentImage.getDate().equals(currentDate)){
                            filteredImages.add(currentImage);
                        }
                    }

                    setUpRecyclerView(DO_NOT_SORT, filteredImages);
                    setConstLayView("clear");

                } else {
                    displayToast("Please enter a date");

                }

                break;

            case R.id.btn_searchByHoliday:
                filteredImages.clear();

                String holidayName = edit_holiday.getText().toString();

                if (!holidayName.equals("")) {
                    for (Holiday currentHoliday : mAllHolidays){
                        if (currentHoliday.getName().toUpperCase().equals(holidayName.toUpperCase())){
                            for(Images currentImage : mAllImages){
                                if(currentHoliday.getImageID() == currentImage.get_id()){
                                    filteredImages.add(currentImage);
                                }

                            }

                        }
                    }

                    setUpRecyclerView(DO_NOT_SORT, filteredImages);
                    setConstLayView("clear");

                } else {
                    displayToast("Please enter a valid holiday name");

                }

                break;

            case R.id.btn_searchByVPlace:
                filteredImages.clear();

                String vPlaceName = edit_vPlace.getText().toString();

                if (!vPlaceName.equals("")) {
                    for (VisitedPlace currentVPlace : mAllVPlaces){
                        if (currentVPlace.getName().toUpperCase().equals(vPlaceName.toUpperCase())){
                            for(Images currentImage : mAllImages){
                                if(currentVPlace.getImageID() == currentImage.get_id()){
                                    filteredImages.add(currentImage);
                                }

                            }

                        }
                    }

                    setUpRecyclerView(DO_NOT_SORT, filteredImages);
                    setConstLayView("clear");

                } else {
                    displayToast("Please enter a valid visited place name");

                }

                break;
            case R.id.btn_searchByTag:
                filteredImages.clear();

                String chosenTag = edit_tag.getText().toString();

                if (!chosenTag.equals("")) {
                    for(Images currentImage : mAllImages){
                        if(currentImage.getTag().toUpperCase().equals(chosenTag.toUpperCase())){
                            filteredImages.add(currentImage);
                        }

                    }

                    setUpRecyclerView(DO_NOT_SORT, filteredImages);
                    setConstLayView("clear");

                } else {
                    displayToast("Please enter a valid tag");

                }

                break;
            case R.id.btn_searchByLocation:
                filteredImages.clear();

                String locationName = edit_location.getText().toString();

                if (!locationName.equals("")) {
                    for(Images currentImage : mAllImages){
                        Place currentPlace = hmImageToPlace.get(currentImage);

                        if(currentPlace != null){
                            if (currentPlace.getName().toUpperCase().equals(locationName.toUpperCase())){
                                filteredImages.add(currentImage);

                                setUpRecyclerView(DO_NOT_SORT, filteredImages);
                                setConstLayView("clear");

                            } else {
                                displayToast("There were 0 matches for the requested location, please try again");
                            }

                        }

                    }

                } else {
                    displayToast("Please enter a valid location");

                }

                break;

        }
    }

}