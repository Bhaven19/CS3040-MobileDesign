package com.example.memori.ui.map;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.memori.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mapViewModel;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Boolean permissionsGranted = false;

    private Button btn_currentLoc;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

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

        setMarker(50, 50, "Test");
    }

    public void getCurrentLocation(){
        if (permissionsGranted){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("TrackLocation", "Found Location");
                                Log.d("TrackLocation", "Longitude: " + location.getLongitude());
                                Log.d("TrackLocation", "Latitude: " + location.getLatitude());

                                setMarker(location.getLatitude(), location.getLongitude(), "CurrentLocation");

                            } else {
                                Log.d("TrackLocation", "No Location");
                            }
                        }
                    });
        }

    }

    public void setMarker(double impLat, double impLon, String impName){
        Log.d("TrackLocation", "setMarker- Longitude: " + impLat);
        Log.d("TrackLocation", "setMarker- Latitude: " + impLon);

        LatLng location = new LatLng(impLat, impLon);
        mMap.addMarker(new MarkerOptions().position(location).title(impName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
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
}