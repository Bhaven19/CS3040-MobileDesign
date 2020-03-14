package com.example.memori.ui.map;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.memori.R;
import com.example.memori.components.places.GooglePlace;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

public class ViewPOI extends AppCompatActivity implements OnMapReadyCallback {

    private TextView mPOIType;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private SupportMapFragment viewPOIFragment;
    private Boolean permissionsGranted = false;

    private ArrayList<GooglePlace> currentPOIPlaces;
    private String POIType;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_p_o_i);

        i = 0;

        mPOIType = findViewById(R.id.label_viewPOI);

        createMap();
    }

    private void retrieveIntent(){
        currentPOIPlaces = (ArrayList<GooglePlace>) getIntent().getSerializableExtra("AllPlaces");

        mPOIType.setText(getIntent().getStringExtra("POIType"));
    }

    public void createMap(){
        FragmentManager fm = getSupportFragmentManager();/// getChildFragmentManager();
        viewPOIFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_holder);
        if (viewPOIFragment == null) {
            viewPOIFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.viewPOI_Map_Holder, viewPOIFragment).commit();
        }
        viewPOIFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        retrieveIntent();

        setAllMarkers();

        getCurrentLocation();

    }

    public void getCurrentLocation(){
        if (!permissionsGranted){
            requestPermissions();
        }

        if (permissionsGranted){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got Last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.d("TrackLocation", "Found Location");
                        Log.d("TrackLocation", "Longitude: " + location.getLongitude());
                        Log.d("TrackLocation", "Latitude: " + location.getLatitude());

                        setMarker("current", location.getLatitude(), location.getLongitude(), "CurrentLocation");

                    } else {
                        Log.d("TrackLocation", "No Location");

                    }

                }

            });

        }

    }

    public void requestPermissions(){
        RxPermissions rxPermissions = new RxPermissions(this);

        Log.d("TrackLocation", "getActivity: " + this);

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

    public void setAllMarkers(){
        if (currentPOIPlaces != null) {
            int numVPlaces = currentPOIPlaces.size();

            if (i < numVPlaces) {
                GooglePlace currentPlace = currentPOIPlaces.get(i);

                setMarker("place", currentPlace.getGeometry().getLocation().getLat(), currentPlace.getGeometry().getLocation().getLng(), currentPlace.getName());
                i++;
                setAllMarkers();

            }
        } else {
            displayToast("There are no visited places within your desired filters");

        }

    }

    public void setMarker(String type, double impLat, double impLon, String impName){
        switch(type) {
            case "current":
                LatLng vplaceLocation = new LatLng(impLat, impLon);

                String vPlaceName = impName;

                MarkerOptions vplaceMarker = new MarkerOptions()
                        .position(vplaceLocation)
                        .title(vPlaceName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                mMap.addMarker(vplaceMarker);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vplaceMarker.getPosition(), 13));

                break;
            case "place":
                LatLng imageLocation = new LatLng(impLat, impLon);

                String imageName = impName;

                MarkerOptions imageMarker = new MarkerOptions()
                        .position(imageLocation)
                        .title(imageName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                mMap.addMarker(imageMarker);

                break;
        }

    }

    public void displayToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
