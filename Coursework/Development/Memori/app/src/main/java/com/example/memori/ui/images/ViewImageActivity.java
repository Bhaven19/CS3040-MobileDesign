package com.example.memori.ui.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.memori.R;
import com.example.memori.database.entities.Images;
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

public class ViewImageActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView mImageName, mImageDate, mNoImage, mImageAddress;
    private ImageView mImageView;

    private Images impImage;
    private String impImageName, impImageDate, impImagePath, impImageTag;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private String placeID;
    private Place imageLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mImageName = findViewById(R.id.title_imageName);
        mImageDate = findViewById(R.id.text_imageDate);
        mNoImage = findViewById(R.id.label_noImage);
        mImageAddress = findViewById(R.id.label_imageAddress);
        mImageView = findViewById(R.id.imageView_image);

        setValuesFromIntent();

        createMap();

    }

    public void setValuesFromIntent(){
        impImage = (Images) getIntent().getSerializableExtra("chosenImage");
        impImageName = getIntent().getStringExtra("chosenImageName");

        impImageDate = impImage.getDate();
        impImagePath = impImage.getPath();
        impImageTag = impImage.getTag();

        mImageName.setText(impImageName);
        mImageDate.setText(impImageDate);
        //mImageTag.setText(impImageTag);

        File imageFile = new File(impImagePath);

        if (imageFile.exists()){
            mImageView.setVisibility(View.VISIBLE);
            mNoImage.setVisibility(View.INVISIBLE);

            Bitmap mHolidayImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            mImageView.setImageBitmap(mHolidayImage);

            Log.d("ImageFind", "ViewVPlace, IMAGE FOUND");

        } else {
            Log.d("ImageFind", "ViewVPlace, IMAGE NOT FOUND");

        }

    }

    public void createMap(){
        FragmentManager fm = getSupportFragmentManager();/// getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.image_map_holder);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.image_map_holder, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
        }

        // Define a Place ID.
        String placeId = impImage.getLocation();

        // Specify the fields to return.s
        List<Place.Field> placeFields = (Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        PlacesClient placesClient = Places.createClient(getApplicationContext());
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            imageLocation = response.getPlace();

            double impLat = imageLocation.getLatLng().latitude;
            double impLon = imageLocation.getLatLng().longitude;

            LatLng location = new LatLng(impLat, impLon);
            mMap.addMarker(new MarkerOptions().position(location).title(imageLocation.getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

            mImageAddress.setText("Address: " + imageLocation.getAddress());

        }).addOnFailureListener(e ->
                Log.d("VPlaceLocation", "ViewVPlace: Place Not Found: " + imageLocation.getId()));


    }

}
