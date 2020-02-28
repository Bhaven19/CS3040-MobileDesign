package com.example.memori.ui.map;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mapViewModel;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private double currentLat, currentLon;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        createMap();

        return root;
    }

    public void getCurrentLocation(){
        Log.d("TrackLocation", "getCurrentLocation Started");
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d("TrackLocation", "Found Location");

                            currentLon = location.getLongitude();
                            currentLat = location.getLatitude();

                            Log.d("TrackLocation", "Longitude: " + location.getLongitude());
                            Log.d("TrackLocation", "Latitude: " + location.getLatitude());

                        } else {
                            Log.d("TrackLocation", "No Location");
                        }
                    }
                });

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng currentLocation = new LatLng(currentLat, currentLon);
        Log.d("TrackLocation", "onMapReady- Longitude: " + currentLat);
        Log.d("TrackLocation", "onMapReady- Latitude: " + currentLon);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("CurrentLocation"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

    }

    public void createMap(){
        Log.d("TrackLocation", "createMap Started");

        FragmentManager fm = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("TrackLocation", "onMapReady Started");

        mMap = googleMap;

    }



}