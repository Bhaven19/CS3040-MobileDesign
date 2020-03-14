package com.example.memori.ui.vplace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.memori.R;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ViewVPlace extends AppCompatActivity implements OnMapReadyCallback{

    private VisitedPlace impVisitedPlace;
    private Images impImage;

    private TextView viewVPlaceName, viewVPlaceDate, viewVPlaceAddress, viewVPlaceNotes, viewVPlaceCompanions, viewNoImage;
    private ImageView viewHolidayImage;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private String placeID;
    private Place vPlaceLoc;

    private ImageButton btnShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vplace);

        Intent obtainIntent = getIntent();

        impVisitedPlace = (VisitedPlace) obtainIntent.getSerializableExtra("chosenVisitedPlace");
        if (obtainIntent.getSerializableExtra("chosenImage") != null){
            impImage = (Images) obtainIntent.getSerializableExtra("chosenImage");
        } else {
            impImage = null;
        }

        viewVPlaceName = findViewById(R.id.title_vPlaceName);
        viewVPlaceDate = findViewById(R.id.text_vPlaceDate);
        viewVPlaceNotes = findViewById(R.id.text_vPlaceNotes);
        viewVPlaceCompanions = findViewById(R.id.text_vPlaceCompanions);
        viewVPlaceAddress = findViewById(R.id.label_vPlaceAddress);

        viewHolidayImage = findViewById(R.id.imageView_vPlace);
        viewNoImage = findViewById(R.id.label_noImage);

        viewVPlaceName.setText(impVisitedPlace.getName());
        viewVPlaceDate.setText(impVisitedPlace.getDate());
        viewVPlaceNotes.setText(impVisitedPlace.getNotes());
        viewVPlaceCompanions.setText(impVisitedPlace.getTravellers());

        if (impImage != null) {
            Log.d("ImageFind", "Images path, impVisitedPlace.getImagePath(): " + impImage.getPath());
            String pathName = impImage.getPath();

            File imageFile = new File(pathName);

            if (imageFile.exists()){
                viewNoImage.setVisibility(View.INVISIBLE);

                Bitmap mHolidayImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                viewHolidayImage.setImageBitmap(mHolidayImage);

                Log.d("ImageFind", "ViewVPlace, IMAGE FOUND");

            } else {
                viewNoImage.setVisibility(View.VISIBLE);

                Log.d("ImageFind", "ViewVPlace, IMAGE NOT FOUND");

            }
        } else {

            Log.d("ImageFind", "ViewVPlace, NO IMAGE SAVED");

        }


        setResult(5, obtainIntent);

        placeID = impVisitedPlace.getLocation();

        createMap();


        btnShare = findViewById(R.id.btn_shareVPlace);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, impVisitedPlace.toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Sharing Visited Place...");
                startActivity(shareIntent);
            }
        });

    }

    public void createMap(){
        FragmentManager fm = getSupportFragmentManager();/// getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.vplace_map_holder);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.vplace_map_holder, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
        }

        Log.d("VPlaceLocation", "ViewVPlace: placeID: " + placeID);
        // Define a Place ID.
        String placeId = placeID;

        // Specify the fields to return.s
        List<Place.Field> placeFields = (Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        PlacesClient placesClient = Places.createClient(getApplicationContext());
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            vPlaceLoc = response.getPlace();
            Log.d("VPlaceLocation", "ViewVPlace: Place found: " + vPlaceLoc.getName());

            double impLat = vPlaceLoc.getLatLng().latitude;
            double impLon = vPlaceLoc.getLatLng().longitude;

            Log.d("VPlaceLocation", "setMarker- Longitude: " + impLat);
            Log.d("VPlaceLocation", "setMarker- Latitude: " + impLon);

            LatLng location = new LatLng(impLat, impLon);
            mMap.addMarker(new MarkerOptions().position(location).title(vPlaceLoc.getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

            viewVPlaceAddress.setText("Address: " + vPlaceLoc.getAddress());

        }).addOnFailureListener(e ->
                Log.d("VPlaceLocation", "ViewVPlace: Place Not Found: " + vPlaceLoc.getId()));


    }

    public void displayToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
