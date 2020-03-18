package com.example.memori.ui.map;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.memori.R;
import com.example.memori.components.HolidayDate;
import com.example.memori.components.places.GooglePlace;
import com.example.memori.components.places.GsonRequest;
import com.example.memori.components.places.PlaceList;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.example.memori.ui.images.ViewImageActivity;
import com.example.memori.ui.vplace.ViewVPlace;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private MapViewModel mapViewModel;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Boolean permissionsGranted = false;

    private Button btn_getPOI, btn_filterMarkers, btn_filterByDate, btn_filterByHoliday, btn_filterByCompanion, btn_filterStartDate, btn_filterEndDate;
    private ConstraintLayout constLay_filterByDate, constLay_filterByHoliday, constLay_filterByCompanions;
    private ImageView hideBack;

    private EditText edit_StartDate, edit_EndDate, edit_Companions;
    private Spinner spinner_Holiday;
    private String chosenHolidayName;
    private final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Boolean validDate = false;
    private HolidayDate startDate, endDate;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private List<Holiday> allHolidays;
    private List<Images> allImages;
    private List<VisitedPlace> allVPlaces;
    private List<VisitedPlace> allVPlacesOriginal;

    private HashMap<String, LatLng> hmTitleToPos;

    private int i;
    private int j;

    private View view;

    private ArrayList<Marker> allVPlaceMarkers = new ArrayList<>();
    private ArrayList<VisitedPlace> filterVPlaces;

    private boolean filtersActive = false;
    private Double[] currentLatLong;

    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onDown(MotionEvent e) {
            displayToast("Don't hold me for too long...");
            Log.d("GestureDetect", "onDown...");

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            try {
                Thread.sleep(1500);
                displayToast("I told you!");

            } catch (InterruptedException ex) {
                ex.printStackTrace();

            }

            super.onLongPress(e);

        }

    });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        view = inflater.inflate(R.layout.fragment_map, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        requestPermissions();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getCurrentLocation();

        retrieveTables();
        i = 0;
        j = 0;

        hmTitleToPos = new HashMap<>();

        setFilterViewIDs();

        createMap();

        return view;
    }

    public void setFilterViewIDs(){
        constLay_filterByDate = view.findViewById(R.id.constLay_filterMarkersByDate);
        constLay_filterByHoliday = view.findViewById(R.id.constLay_filterMarkersByHoliday);
        constLay_filterByCompanions = view.findViewById(R.id.constLay_filterMarkersByCompanion);
        hideBack = view.findViewById(R.id.hide_back);

        //-------------------------------------

        edit_StartDate = view.findViewById(R.id.edit_filterStartDate);
        edit_EndDate = view.findViewById(R.id.edit_filterEndDate);
        edit_StartDate.setEnabled(false);
        edit_EndDate.setEnabled(false);

        //-------------------------------------

        edit_Companions = view.findViewById(R.id.edit_filterCompanion);

        //-------------------------------------

        btn_filterByDate = view.findViewById(R.id.btn_filterByDate);
        btn_filterByHoliday = view.findViewById(R.id.btn_filterByHoliday);
        btn_filterByCompanion = view.findViewById(R.id.btn_filterByCompanion);

        btn_filterByDate.setOnClickListener(this);
        btn_filterByHoliday.setOnClickListener(this);
        btn_filterByCompanion.setOnClickListener(this);

        //-------------------------------------

        btn_filterStartDate = view.findViewById(R.id.btn_filterStartDate);
        btn_filterEndDate = view.findViewById(R.id.btn_filterEndDate);

        btn_filterStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                validDate = false;

                DatePickerDialog startDatePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDate = new HolidayDate(dayOfMonth, monthOfYear, year);

                                edit_StartDate.setText(startDate.toString());

                            }
                        }, mYear, mMonth, mDay);

                startDatePickerDialog.show();

            }
        });
        btn_filterEndDate.setOnClickListener(new View.OnClickListener() {
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
                                endDate = new HolidayDate(dayOfMonth, monthOfYear, year);

                                if (endDate.validDate(startDate)){
                                    edit_EndDate.setText(endDate.toString());

                                } else {
                                    displayToast("Invalid Date: The End Date must not be before the Start Date, please try again");

                                }

                            }
                        }, mYear, mMonth, mDay);

                endDatePickerDialog.show();
            }
        });

        //-------------------------------------

        spinner_Holiday = view.findViewById(R.id.spinner_filterByHoliday);

        //-------------------------------------

        btn_filterMarkers = view.findViewById(R.id.btn_filterMapMarkers);
        btn_filterMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
                pictureDialog.setTitle("What would you like to filer the visited places by?");
                String[] pictureDialogItems = {
                        "Filter by Date",
                        "Filter by Holiday",
                        "Filter by Companion",
                        "Reset Filters"};
                pictureDialog.setItems(pictureDialogItems,
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    setConstLayView("date");

                                    break;
                                case 1:
                                    setConstLayView("holiday");

                                    break;
                                case 2:
                                    setConstLayView("companion");

                                    break;
                                case 3:
                                    setConstLayView("reset");

                                    break;
                            }
                        });
                pictureDialog.show();
            }
        });

        //-------------------------------------

        btn_getPOI = view.findViewById(R.id.btn_getPOI);
        btn_getPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate the RequestQueue .
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
                pictureDialog.setTitle("What category of nearby places would you like to see?");
                String[] pictureDialogItems = {
                        "Find Restaurants",
                        "Find Museum",
                        "Find Shopping Mall",
                        "Find Stadium",
                        "Find Tourist Attraction",
                        "Cancel"};
                pictureDialog.setItems(pictureDialogItems,
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    getNearbyPlaces("restaurant");
                                    break;
                                case 1:
                                    getNearbyPlaces("museum");
                                    break;
                                case 2:
                                    getNearbyPlaces("shopping_mall");
                                    break;
                                case 3:
                                    getNearbyPlaces("stadium");
                                    break;
                                case 4:
                                    getNearbyPlaces("tourist_attraction");
                                    break;
                                case 5:
                                    getNearbyPlaces("cancel");
                                    break;
                            }
                        });
                pictureDialog.show();

            }
        });

    }

    public void getNearbyPlaces(String placeType){
        RequestQueue queue = Volley.newRequestQueue (getContext());

        String url2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=52.47819,-1.89984&radius=3000&type=" + placeType + "&radius=3000&key=AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLatLong[0] + "," + currentLatLong[1] + "&radius=3000&type=" + placeType + "&radius=3000&key=AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0";

        // Request a JSON rsponse from the provided URL
        GsonRequest jsonRequest = new GsonRequest <PlaceList>(url, PlaceList.class, null, response -> {
            // Display the first 500 characters of the response string .
            List<GooglePlace> places = response.getResults();

            if (places.size() != 0) {
                Intent viewPOIIntent = new Intent(getActivity(), ViewPOI.class);

                viewPOIIntent.putExtra("AllPlaces", new ArrayList<>(places));
                viewPOIIntent.putExtra("POIType", placeType);

                startActivity(viewPOIIntent);

            } else {
                displayToast("There are none of these places within a 3000m radius");

            }

        }, error -> {
            displayToast("Failed");

            });

        // Add the request to the RequestQueue .
        queue.add(jsonRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermissions();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getCurrentLocation();

        retrieveTables();
        i = 0;
        j = 0;

        hmTitleToPos = new HashMap<>();

        setFilterViewIDs();

        createMap();
    }

    public void createMap(){
        FragmentManager fm = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_holder);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_holder, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        retrieveTables();

        if(filtersActive){
            displayToast("All filters have been reset, loading all markers");
            filtersActive = false;
        } else {
            displayToast("Loading all Map Markers");
        }


        setAllVPlaceMarkers(allVPlaces);
        setAllImageMarkers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (String currentMarkerTitle : hmTitleToPos.keySet()) {
                    if (currentMarkerTitle.equals(marker.getTitle()) && hmTitleToPos.get(currentMarkerTitle).equals(marker.getPosition())){
                        displayLog("MarkerClick", "markerTitle: " + marker.getTitle());
                        displayLog("MarkerClick", "markerPos: " + marker.getPosition());

                        displayLog("ViewMarker", "currentMarkerTitle: " + currentMarkerTitle);

                        if (currentMarkerTitle.contains("-VPlace")){
                            Intent viewIntent = new Intent(getActivity(), ViewVPlace.class);

                            String markerTitle = currentMarkerTitle.replaceAll("VPlace", "");
                            markerTitle = markerTitle.replaceAll(" ", "");
                            markerTitle = markerTitle.replaceAll("-", "");

                            displayLog("ViewMarker", "VPlace Marker Title: " + markerTitle );

                            VisitedPlace myVisitedPlace = getVPlaceByName(markerTitle);
                            displayLog("ViewMarker", "myVisitedPlace: " + myVisitedPlace);

                            viewIntent.putExtra("chosenVisitedPlace", myVisitedPlace);
                            viewIntent.putExtra("chosenVisitedPlaceImage", getImage(myVisitedPlace.getImageID()));

                            viewIntent.putExtra("chosenImage", getImage(myVisitedPlace.getImageID()));

                            startActivity(viewIntent);

                        } else if (currentMarkerTitle.contains("-Image")){
                            Intent viewIntent = new Intent(getActivity(), ViewImageActivity.class);

                            String markerTitle = currentMarkerTitle.replaceAll("Image", "");
                            markerTitle = markerTitle.replaceAll(" ", "");
                            markerTitle = markerTitle.replaceAll("-", "");

                            displayLog("ViewMarker", "Image Marker Title: " + markerTitle );

                            if (getVPlaceByName(markerTitle) != null){
                                VisitedPlace chosenVPlace = getVPlaceByName(markerTitle);

                                Images myImage = getImage(chosenVPlace.getImageID());

                                viewIntent.putExtra("chosenImage", myImage);
                                viewIntent.putExtra("chosenImageName", chosenVPlace.getName());

                                startActivity(viewIntent);

                            } else if (getHolidayByName(markerTitle) != null){
                                Holiday chosenHoliday = getHolidayByName(markerTitle);

                                Images myImage = getImage(chosenHoliday.getImageID());

                                viewIntent.putExtra("chosenImage", myImage);
                                viewIntent.putExtra("chosenImageName", chosenHoliday.getName());

                                startActivity(viewIntent);

                            } else {
                                displayLog("ViewMarker", "UNUSUAL ACTIVITY");

                            }

                        }
                    }
                }
                return false;
            }
        });
    }

    public void getCurrentLocation(){
        currentLatLong = new Double[2];

        if (!permissionsGranted){
            requestPermissions();

        }

        if (permissionsGranted){
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got Last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.d("TrackLocation", "Found Location");
                        Log.d("TrackLocation", "Longitude: " + location.getLongitude());
                        Log.d("TrackLocation", "Latitude: " + location.getLatitude());

                        currentLatLong[0] = location.getLatitude();
                        currentLatLong[1] = location.getLongitude();


                    } else {
                        Log.d("TrackLocation", "No Location");

                    }

                }

            });
        }

    }

    public void setMarker(String type, double impLat, double impLon, String impName){
        switch(type) {
            case "VPlaceFragment":
                LatLng vplaceLocation = new LatLng(impLat, impLon);

                String vPlaceName = impName + " -VPlace";

                hmTitleToPos.put(vPlaceName, vplaceLocation);

                MarkerOptions vplaceMarker = new MarkerOptions()
                        .position(vplaceLocation)
                        .title(vPlaceName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                Marker currentMarker = mMap.addMarker(vplaceMarker);

                Log.d("RemovingMarkers", "Adding VMarker to List: " + currentMarker.getTitle());
                allVPlaceMarkers.add(currentMarker);

                break;
            case "image":
                LatLng imageLocation = new LatLng(impLat, impLon);

                String imageName = impName + " -Image";

                hmTitleToPos.put(imageName, imageLocation);

                MarkerOptions imageMarker = new MarkerOptions()
                        .position(imageLocation)
                        .title(imageName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                mMap.addMarker(imageMarker);

                break;
        }

    }

    public void requestPermissions(){
        RxPermissions rxPermissions = new RxPermissions(getActivity());

        Log.d("TrackLocation", "getActivity: " + getActivity());

        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION) // ask single or multiple permission once
                .subscribe(granted -> {
                    if (granted) {
                        Log.d("TrackLocation", "Permissions Granted");
                        permissionsGranted = true;
                    } else {
                        Log.d("TrackLocation", "Permissions Denied");
                        permissionsGranted = false;
                        // At least one permission is denied
                    }
                });
    }


    public void retrieveTables(){
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        mapViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
            // Update the cached copy of the words in the adapter.
            allHolidays = holidays;

            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_Holiday.setAdapter(adapter);

            adapter.add("Please Select a Holiday");

            for (Holiday currentHoliday : holidays){
                adapter.add(currentHoliday.getName());
            }


            spinner_Holiday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenHolidayName = (String) spinner_Holiday.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        });

        mapViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            // Update the cached copy of the words in the adapter.
            allImages = images;

        });

        mapViewModel.getAllVisitedPlaces().observe(getViewLifecycleOwner(), vplaces -> {
            // Update the cached copy of the words in the adapter.
            allVPlaces = vplaces;
            allVPlacesOriginal = allVPlaces;


        });
    }

    private void setAllVPlaceMarkers(List<VisitedPlace> impAllVPlaces){
        if (impAllVPlaces != null) {
            int numVPlaces = impAllVPlaces.size();

            if (i < numVPlaces) {
                VisitedPlace visitedPlace = impAllVPlaces.get(i);
                String placeID = visitedPlace.getLocation();

                if (placeID != "") {
                    if (!Places.isInitialized()) {
                        Places.initialize(getActivity(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
                    }

                    // Define a Place ID.
                    String placeId = placeID;

                    // Specify the fields to return.s
                    List<Place.Field> placeFields = (Arrays.asList(Place.Field.ID, Place.Field.NAME,
                            Place.Field.ADDRESS, Place.Field.LAT_LNG));

                    // Construct a request object, passing the place ID and fields array.
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                    PlacesClient placesClient = Places.createClient(getActivity());
                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        setMarker("VPlaceFragment", response.getPlace().getLatLng().latitude,
                                response.getPlace().getLatLng().longitude, visitedPlace.getName());
                        i++;
                        setAllVPlaceMarkers(impAllVPlaces);

                    });
                }

            }
        } else {
            displayToast("There are no visited places within your desired filters");

        }

    }

    public void setAllImageMarkers(){
        if(j < allImages.size()){
            Images image = allImages.get(j);
            String placeID = image.getLocation();

            if (placeID != "") {
                if (!Places.isInitialized()) {
                    Places.initialize(getActivity(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
                }

                Log.d("SetAllMarkers", "ViewImage: placeID: " + placeID);
                // Define a Place ID.
                String placeId = placeID;

                // Specify the fields to return.s
                List<Place.Field> placeFields = (Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

                // Construct a request object, passing the place ID and fields array.
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                PlacesClient placesClient = Places.createClient(getActivity());

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    setMarker("image", response.getPlace().getLatLng().latitude, response.getPlace().getLatLng().longitude, getImageName(image.get_id()));
                    j++;
                    setAllImageMarkers();

                });
            }

        }

    }

    public Holiday getHolidayByName(String name){
        Holiday chosenHoliday = null;

        for (Holiday currentHoliday : allHolidays){
            if (currentHoliday.getName().equals(name)){
                chosenHoliday = currentHoliday;
                break;
            }
        }

        return chosenHoliday;
    }

    public VisitedPlace getVPlaceByName(String name){
        VisitedPlace chosenVPlace = null;

        for (VisitedPlace currentVPlace : allVPlaces){
            String currentVPlaceNameNoSpace = currentVPlace.getName().replace(" ", "");

            if (currentVPlaceNameNoSpace.equals(name)){
                chosenVPlace = currentVPlace;
                break;
            }
        }

        return chosenVPlace;
    }

    public Images getImage(int id){
        Images chosenImage = null;

        Log.d("FindImage", "allImages.size:" + allImages.size());
        for (int i = 0; i < allImages.size(); i++) {
            Images currentImage = allImages.get(i);

            if (currentImage.get_id() == id){
                chosenImage = currentImage;

                Log.d("FindImage", "ImageFound");
            } else {
                Log.d("FindImage", "ImageNotFound");

            }
        }
        return chosenImage;
    }

    public void displayToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void displayLog(String tag, String message){
        Log.d(tag, "MapFragment, " + message);
    }

    public String getImageName(int imageID){
        String name = "";

        for (Holiday holiday : allHolidays){
            if (holiday.getImageID() == imageID){
                name = holiday.getName();
            }
        }

        for (VisitedPlace vplace : allVPlaces){
            if (vplace.getImageID() == imageID){
                name = vplace.getName();
            }
        }

        return name;

    }

    public void setConstLayView(String filterType){
        switch (filterType) {
            case "date":
                constLay_filterByHoliday.setVisibility(View.INVISIBLE);
                constLay_filterByCompanions.setVisibility(View.INVISIBLE);
                constLay_filterByDate.setVisibility(View.VISIBLE);
                hideBack.setVisibility(View.VISIBLE);
                break;

            case "holiday":
                constLay_filterByDate.setVisibility(View.INVISIBLE);
                constLay_filterByCompanions.setVisibility(View.INVISIBLE);
                constLay_filterByHoliday.setVisibility(View.VISIBLE);
                hideBack.setVisibility(View.VISIBLE);

                    break;
            case "companion":
                constLay_filterByDate.setVisibility(View.INVISIBLE);
                constLay_filterByHoliday.setVisibility(View.INVISIBLE);
                constLay_filterByCompanions.setVisibility(View.VISIBLE);
                hideBack.setVisibility(View.VISIBLE);

                break;
            case "reset":
                constLay_filterByDate.setVisibility(View.INVISIBLE);
                constLay_filterByHoliday.setVisibility(View.INVISIBLE);
                constLay_filterByCompanions.setVisibility(View.INVISIBLE);
                hideBack.setVisibility(View.INVISIBLE);

                setAllVPlaceMarkers(allVPlacesOriginal);
                setAllImageMarkers();
                filtersActive = false;

                break;
            case "clear":
                constLay_filterByDate.setVisibility(View.INVISIBLE);
                constLay_filterByHoliday.setVisibility(View.INVISIBLE);
                constLay_filterByCompanions.setVisibility(View.INVISIBLE);
                hideBack.setVisibility(View.INVISIBLE);

                break;

            }

    }

    @Override
    public void onClick(View v) {
        filterVPlaces = new ArrayList<>();

        switch (v.getId()) {
            case R.id.btn_filterByDate:
                removeAllVPlaceMarkers();

                String currentStartDate = startDate.toString();
                Date startDate = getDate(currentStartDate);
                Log.d("RemovingMarkers", "startDate: " + startDate.toString());

                String currentEndDate = endDate.toString();
                Date endDate = getDate(currentEndDate);
                Log.d("RemovingMarkers", "endDate: " + endDate.toString());

                Log.d("RemovingMarkers", "------------------------");

                for (VisitedPlace visitedPlace : allVPlaces){
                    String currentVPlaceDate = visitedPlace.getDate();
                    Date vPlaceDate = getDate(currentVPlaceDate);
                    Log.d("RemovingMarkers", "vPlaceDate: " + vPlaceDate.toString());

                    Log.d("RemovingMarkers", "vPlaceDate.after(startDate) && vPlaceDate.before(endDate): " + vPlaceDate.after(startDate) + ", " + vPlaceDate.before(endDate));
                    if (vPlaceDate.after(startDate) && vPlaceDate.before(endDate)){
                        Log.d("RemovingMarkers", "Adding vPlace to List: " + visitedPlace.getName());
                        filterVPlaces.add(visitedPlace);

                    }

                }

                displayToast("Loading all filtered markers");

                for (VisitedPlace vplace : filterVPlaces){
                    Log.d("RemovingMarkers", "filtered list: " + vplace.getName());

                }

                i = 0;
                setAllVPlaceMarkers(filterVPlaces);
                setAllImageMarkers();

                setConstLayView("clear");
                filtersActive = true;

                break;

            case R.id.btn_filterByHoliday:
                removeAllVPlaceMarkers();

                Holiday chosenHoliday = null;

                if (!chosenHolidayName.equals("Please Select a Holiday")) {
                    for (Holiday currentHoliday : allHolidays) {
                        if (currentHoliday.getName().equals(chosenHolidayName)) {
                            chosenHoliday = currentHoliday;

                        }
                    }

                    for (VisitedPlace visitedPlace : allVPlaces){
                        if (visitedPlace.getHolidayID() == chosenHoliday.get_id()){
                            filterVPlaces.add(visitedPlace);

                        }
                    }

                    i = 0;
                    setAllVPlaceMarkers(filterVPlaces);
                    setAllImageMarkers();

                    setConstLayView("clear");
                    filtersActive = true;

                } else {
                    displayToast("Please select a Holiday before filtering");

                }

                break;

            case R.id.btn_filterByCompanion:
                removeAllVPlaceMarkers();

                ArrayList<String> matchedCompanions = new ArrayList<>();

                if (!edit_Companions.getText().toString().equals("")) {
                    String chosenCompanions = edit_Companions.getText().toString();
                    ArrayList<String> allChosenCompanions = extractNames(chosenCompanions);

                    for (VisitedPlace visitedPlace : allVPlaces){
                        String vPlaceCompanions = visitedPlace.getTravellers();
                        ArrayList<String> allVPlaceCompanions = extractNames(vPlaceCompanions);

                        for (String currentVPlaceCompanion : allVPlaceCompanions){
                            for (String currentChosenCompanion : allChosenCompanions){

                                if (currentChosenCompanion.toUpperCase().equals(currentVPlaceCompanion.toUpperCase())){
                                    matchedCompanions.add(currentChosenCompanion);

                                }
                            }
                        }
                    }

                    for (VisitedPlace currentVPlace : allVPlaces){
                        String vPlaceCompanions = currentVPlace.getTravellers();
                        ArrayList<String> allVPlaceCompanions = extractNames(vPlaceCompanions);

                        for (String currentVPlaceCompanion : allVPlaceCompanions){
                            for (String currentMatchedCompanion : matchedCompanions) {

                                if (currentVPlaceCompanion.toUpperCase().equals(currentMatchedCompanion.toUpperCase())) {
                                    filterVPlaces.add(currentVPlace);

                                }

                            }
                        }
                    }

                    displayToast("Loading all filtered markers");

                    i = 0;
                    setAllVPlaceMarkers(filterVPlaces);
                    setAllImageMarkers();

                    setConstLayView("clear");
                    filtersActive = true;

                } else {
                    displayToast("Please enter a list of the desired travellers");

                }

                break;

        }
    }

    public Date getDate(String impDate){
        String currentDate = impDate;

        int currentYear = Integer.parseInt(currentDate.substring(currentDate.length() - 4));
        int currentMonth = Integer.parseInt(currentDate.substring(3, 5));
        int currentDay = Integer.parseInt(currentDate.substring(0, 2));

        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth - 1, currentDay);

        return calendar.getTime();
    }

    public void removeAllVPlaceMarkers(){
        for (Marker currentMarker : allVPlaceMarkers){
            Log.d("RemovingMarkers", "Removing Marker: " + currentMarker.getTitle());

            currentMarker.remove();

        }

        Log.d("RemovingMarkers", "Emptying allVPlaceMarkers");
        allVPlaceMarkers.clear();

    }

    public ArrayList<String> extractNames(String companions){
        Log.d("ExtractName", "extractNames Start: --------------------------------" + companions);

        ArrayList<String> chosenCompanions = new ArrayList<>();

        String[] searchItems = companions.split(",");

        if (searchItems.length > 0) {
            Log.d("ExtractName", "Number of Matches: " + searchItems.length);

            for (int i = 0; i < searchItems.length; i++) {
                String currentName = searchItems[i];
                currentName = currentName.replaceAll("\\s+", "");

                Log.d("ExtractName", "currentName (without spaces): " + currentName);

                chosenCompanions.add(currentName);
            }
        } else {
            Log.d("ExtractName", "No Matches Found");

        }

        return chosenCompanions;
    }

}