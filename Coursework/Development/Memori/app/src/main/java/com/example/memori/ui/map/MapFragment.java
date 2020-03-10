package com.example.memori.ui.map;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.example.memori.ui.holiday.vplaces.ViewVPlace;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private MapViewModel mapViewModel;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Boolean permissionsGranted = false;

    private Button btn_currentLoc;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private List<Holiday> allHolidays;
    private List<Images> allImages;
    private List<VisitedPlace> allVPlaces;

    private HashMap<String, LatLng> hmTitleToPos;
    private List<Place> allPlaces = null;

    private int i;
    private int j;
    private Boolean move = false;

    public ArrayList<Integer> vPlaceIDPerHoliday;

    private Boolean markersLoaded = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        retrieveTables();
        i = 0;
        j = 0;

        requestPermissions();

        btn_currentLoc = root.findViewById(R.id.btn_currentLoc);
        btn_currentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TrackLocation", "Button Pressed");
                getCurrentLocation();

            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        hmTitleToPos = new HashMap<>();

        createMap();

        return root;
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

        setAllVPlaceMarkers();
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

                            viewIntent.putExtra("chosenVisitedPlace", myVisitedPlace);
                            viewIntent.putExtra("chosenVisitedPlaceImage", getImage(myVisitedPlace.getImageID()));

                            viewIntent.putExtra("chosenImage", getImage(myVisitedPlace.getImageID()));

                            startActivity(viewIntent);

                        }

                    }
                }

                return false;
            }
        });
    }

    public void getCurrentLocation(){
        if (permissionsGranted){
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got Last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.d("TrackLocation", "Found Location");
                        Log.d("TrackLocation", "Longitude: " + location.getLongitude());
                        Log.d("TrackLocation", "Latitude: " + location.getLatitude());

                        setMarker("default", location.getLatitude(), location.getLongitude(), "CurrentLocation");

                    } else {
                        Log.d("TrackLocation", "No Location");

                    }

                }

            });
        }

    }

    public void setMarker(String type, double impLat, double impLon, String impName){
        Log.d("TrackLocation", "setMarker- Longitude: " + impLat);
        Log.d("TrackLocation", "setMarker- Latitude: " + impLon);

        switch(type) {
            case "default":
                LatLng defaultLocation = new LatLng(impLat, impLon);

                hmTitleToPos.put(impName, defaultLocation);

                MarkerOptions defaultMarker = new MarkerOptions()
                        .position(defaultLocation)
                        .title(impName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                mMap.addMarker(defaultMarker);

                break;
            case "vplace":
                LatLng vplaceLocation = new LatLng(impLat, impLon);

                hmTitleToPos.put(impName + " -VPlace", vplaceLocation);

                MarkerOptions vplaceMarker = new MarkerOptions()
                        .position(vplaceLocation)
                        .title(impName + " -VPlace")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                mMap.addMarker(vplaceMarker);

                break;
            case "image":
                LatLng imageLocation = new LatLng(impLat, impLon);

                hmTitleToPos.put(impName + " -Image", imageLocation);

                MarkerOptions imageMarker = new MarkerOptions()
                        .position(imageLocation)
                        .title(impName + " -Image")
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap = null;
    }

    public void retrieveTables(){
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        mapViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
            // Update the cached copy of the words in the adapter.
            allHolidays = holidays;


        });

        mapViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            // Update the cached copy of the words in the adapter.
            allImages = images;

        });

        mapViewModel.getAllVisitedPlaces().observe(getViewLifecycleOwner(), vplaces -> {
            // Update the cached copy of the words in the adapter.
            allVPlaces = vplaces;

        });
    }

    public void setAllVPlaceMarkers(){
        int numVPlaces = allVPlaces.size();

        if(i < numVPlaces){
            VisitedPlace visitedPlace = allVPlaces.get(i);
            String placeID = visitedPlace.getLocation();

            if (placeID != "") {
                if (!Places.isInitialized()) {
                    Places.initialize(getActivity(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
                }

                Log.d("SetAllMarkers", "ViewVPlace: placeID: " + placeID);
                // Define a Place ID.
                String placeId = placeID;

                // Specify the fields to return.s
                List<Place.Field> placeFields = (Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

                // Construct a request object, passing the place ID and fields array.
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                PlacesClient placesClient = Places.createClient(getActivity());
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    setMarker("vplace", response.getPlace().getLatLng().latitude, response.getPlace().getLatLng().longitude, response.getPlace().getName());
                    i++;
                    setAllVPlaceMarkers();

                });
            }

        }

    }

    public void setAllImageMarkers(){
        int numImages = allImages.size();

        if(j < numImages){
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
                    setMarker("image", response.getPlace().getLatLng().latitude, response.getPlace().getLatLng().longitude, response.getPlace().getName());
                    j++;
                    setAllImageMarkers();

                });
            }

        }

    }

    public void displayLog(String tag, String message){
        Log.d(tag, "MapFragment, " + message);
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

}