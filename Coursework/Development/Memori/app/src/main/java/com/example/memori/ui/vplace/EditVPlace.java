package com.example.memori.ui.vplace;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.memori.R;
import com.example.memori.components.HolidayDate;
import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditVPlace extends AppCompatActivity implements View.OnClickListener{

    private HolidayRepository holidayRepository;

    private EditText textViewVPlaceName, textViewVPlaceDate, textViewVPlaceLoc, textViewVPlaceTravellers, textViewVPlaceNotes, textViewImageTag;
    private TextView textViewVPlaceAddress, textViewNoImage;
    private Spinner spinnerChooseHoliday;
    private ImageView imageViewVPlaceImage;
    private Button mAddImage, mSaveVPlace, btnDate, btnRemoveImage;
    private ConstraintLayout constLayNoImage, constLayImageSaved;

    private final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Boolean validDate = false;
    private HolidayDate date;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/memori/";
    private Boolean pictureSaved = false;
    private String mImagePath, mImagePathOrig;
    private Bitmap bmpHolidayImage;
    private ImageButton mGetCurrentLoc;

    private ArrayList<String> allHolidays;
    private HashMap<String, Integer> adapterVisitedPlaces;
    private String mImageDate = "", mImageTag = "";
    private Holiday chosenHoliday;

    public static final int SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 6;

    private VisitedPlace mVisitedPlace;
    private Images vPlaceImage;

    private Boolean setSpinner = false;

    private Boolean permissionsGranted = false;

    private String placeID = "";
    private Place chosenPlace;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vplace);

        mVisitedPlace = (VisitedPlace) getIntent().getSerializableExtra("chosenVisitedPlace");

        if (getIntent().getSerializableExtra("vPlaceImage") != null){
            vPlaceImage = (Images) getIntent().getSerializableExtra("vPlaceImage");

        } else {
            vPlaceImage = null;
        }


        chosenHoliday = (Holiday) getIntent().getSerializableExtra("chosenHoliday");
        allHolidays = getIntent().getStringArrayListExtra("holidayNameList");

        textViewVPlaceName = findViewById(R.id.edit_vPlaceName);
        textViewVPlaceDate = findViewById(R.id.edit_VPlaceDate);
        textViewVPlaceAddress = findViewById(R.id.label_VPlaceAddress);
        textViewVPlaceTravellers = findViewById(R.id.edit_vPlaceCompanions);
        textViewVPlaceNotes = findViewById(R.id.edit_vPlaceNotes);
        textViewNoImage = findViewById(R.id.label_noImage);

        constLayNoImage = findViewById(R.id.constLay_noImage);
        constLayImageSaved = findViewById(R.id.constLay_imageSaved);

        textViewVPlaceDate.setEnabled(false);

        imageViewVPlaceImage = findViewById(R.id.imageView_newHolidayImage);
        textViewNoImage = findViewById(R.id.label_vPlaceNoImage);
        textViewImageTag = findViewById(R.id.edit_holidayImageTag);

        mAddImage = findViewById(R.id.btn_holidaySaveImage);
        mAddImage.setOnClickListener(this);

        btnRemoveImage = findViewById(R.id.btn_holidayDeleteImage);
        btnRemoveImage.setOnClickListener(this);

        mSaveVPlace = findViewById(R.id.btn_saveVPlace);
        mSaveVPlace.setOnClickListener(this);

        btnDate = findViewById(R.id.btn_selectVPlaceDate);
        btnDate.setOnClickListener(this);

        setValues();

        setupSpinner();

        mGetCurrentLoc = findViewById(R.id.btn_getCurrentLocation);
        mGetCurrentLoc.setOnClickListener(this);

        setupAutoComplete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_saveVPlace:
                Intent saveVPlaceIntent = new Intent();

                if (TextUtils.isEmpty(textViewVPlaceName.getText()) || TextUtils.isEmpty(textViewVPlaceDate.getText()) ) {

                    setResult(0, saveVPlaceIntent);

                    Toast.makeText(getApplicationContext(), "Fields are empty, nothing was saved", Toast.LENGTH_LONG).show();

                } else {
                    String vPlaceHoliday = spinnerChooseHoliday.getSelectedItem().toString();
                    mVisitedPlace.setName(textViewVPlaceName.getText().toString());
                    mVisitedPlace.setLocation(placeID);
                    mVisitedPlace.setTravellers(textViewVPlaceTravellers.getText().toString());
                    mVisitedPlace.setNotes(textViewVPlaceNotes.getText().toString());

                    if (date != null){
                        mVisitedPlace.setDate(date.toString());
                    }

                    if(mImagePath.equals(mImagePathOrig)){
                        vPlaceImage.setTag(textViewImageTag.getText().toString());
                        saveVPlaceIntent.putExtra("editedVPlaceImage", vPlaceImage);

                    } else {
                        if (chosenPlace != null){
                            placeID = chosenPlace.getId();
                        } else {
                            placeID = "";
                        }

                        vPlaceImage.setPath(mImagePath);
                        vPlaceImage.setDate(mImageDate);
                        vPlaceImage.setTag(textViewImageTag.getText().toString());
                        vPlaceImage.setLocation(placeID);
                        saveVPlaceIntent.putExtra("editedVPlaceImage", vPlaceImage);
                    }

                    saveVPlaceIntent.putExtra("editedVPlaceHolidayName", vPlaceHoliday);
                    saveVPlaceIntent.putExtra("editedVPlace", mVisitedPlace);

                    setResult(SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE, saveVPlaceIntent);
                }
                finish();

                break;
            case R.id.btn_holidaySaveImage:
                if (pictureSaved == false) {
                    AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
                    pictureDialog.setTitle("Select Action");
                    String[] pictureDialogItems = {
                            "Select Images from gallery",
                            "Take new Images with camera"};
                    pictureDialog.setItems(pictureDialogItems,
                            (dialog, which) -> {
                                switch (which) {
                                    case 0:
                                        choosePhotoFromGallary();
                                        break;
                                    case 1:
                                        takePhotoFromCamera();
                                        break;
                                }
                            });
                    pictureDialog.show();

                    pictureSaved = true;

                } else {
                    displayToast("You can only save 1 image, any new images will overwrite previous images");

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {

                    }

                    AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
                    pictureDialog.setTitle("Select Action");
                    String[] pictureDialogItems = {
                            "Select Images from gallery",
                            "Take new Images with camera"};
                    pictureDialog.setItems(pictureDialogItems,
                            (dialog, which) -> {
                                switch (which) {
                                    case 0:
                                        choosePhotoFromGallary();
                                        break;
                                    case 1:
                                        takePhotoFromCamera();
                                        break;
                                }
                            });
                    pictureDialog.show();


                }

                //imageViewVPlaceImage.setImageBitmap(bmpHolidayImage);

                break;
            case R.id.btn_selectVPlaceDate:
                // Get Current Date
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                validDate = false;

                DatePickerDialog startDatePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date = new HolidayDate(dayOfMonth, monthOfYear, year);

                                textViewVPlaceDate.setText(date.toString());

                            }
                        }, mYear, mMonth, mDay);
                startDatePickerDialog.show();
                break;
            case R.id.btn_getCurrentLocation:
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);;

                if (!permissionsGranted) {
                    RxPermissions rxPermissions = new RxPermissions(this);

                    Log.d("TrackLocation", "getActivity: " + this);

                    rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(granted -> {
                        if (granted) {
                            Log.d("VPlaceTrackLocation", "Permissions Granted");
                            permissionsGranted = true;
                        } else {
                            Log.d("VPlaceTrackLocation", "Permissions Denied");
                            permissionsGranted = false;
                            // At least one permission is denied
                        }
                    });
                }

                if (permissionsGranted){
                    if (!Places.isInitialized()) {
                        Places.initialize(getApplicationContext(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
                    }

                    // Use fields to define the data types to return.
                    List<Place.Field> placeFields = (Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

                    // Use the builder to create a FindCurrentPlaceRequest.
                    FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

                    PlacesClient placesClient = Places.createClient(this);

                    Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                    placeResponse.addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            FindCurrentPlaceResponse response = task.getResult();

                            chosenPlace = response.getPlaceLikelihoods().get(0).getPlace();

                            placeID = chosenPlace.getId();
                            textViewVPlaceAddress.setText("Address: " + chosenPlace.getAddress());

                            Log.d("VPlaceTrackLocation", "Most Likely Place Name: " + chosenPlace.getName());
                            Log.d("VPlaceTrackLocation", "Most Likely Place ID: " + chosenPlace.getId());
                            Log.d("VPlaceTrackLocation", "Most Likely Place getAddress: " + chosenPlace.getAddress());

                        } else {
                            Exception exception = task.getException();

                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;

                                Log.d("VPlaceTrackLocation", "Place not found: " + apiException.getStatusCode());

                            }
                        }
                    });

                }
                break;
            case R.id.btn_holidayDeleteImage:
                printImage(false);

                mImagePath = "";
                mImageDate = "";
                textViewImageTag.setText("");
                bmpHolidayImage = null;
                pictureSaved = false;

                break;
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);

                    bmpHolidayImage = bitmap;

                    Toast.makeText(EditVPlace.this, "Images Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditVPlace.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String path = saveImage(thumbnail);

            bmpHolidayImage = thumbnail;

            Toast.makeText(EditVPlace.this, "Images Saved!", Toast.LENGTH_SHORT).show();

        }


        File imageFile = new File(mImagePath);
        if (imageFile.exists()){
            bmpHolidayImage = BitmapFactory.decodeFile(mImagePath);

            Log.d("ImageFind", "CreateVplace, IMAGE FOUND");

            printImage(true);

        } else {
            Log.d("ImageFind", "CreateVplace, IMAGE NOT FOUND");

        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            mImagePath = f.getAbsolutePath();

            HolidayDate currentImageDate = new HolidayDate(Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR);
            mImageDate = currentImageDate.toString();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    public void setupSpinner(){
        spinnerChooseHoliday = findViewById(R.id.spinner_vPlaceChooseHoliday);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChooseHoliday.setAdapter(adapter);

        int i = 0;

        for (String currentHolidayName : allHolidays){
            adapter.add(currentHolidayName);
            adapterVisitedPlaces.put(currentHolidayName, i);
            i++;

        }

        if (setSpinner){

            for (String currentHolidayName : allHolidays){
                Log.d("VisitedPlaceEdit", "EditVPlace: currentHolidayName: " + currentHolidayName);

                if (currentHolidayName.equals(chosenHoliday.getName())){
                    Log.d("VisitedPlaceEdit", "EditVPlace: Matched Name: " + chosenHoliday.getName());

                    int adaptPos = adapterVisitedPlaces.get(currentHolidayName);

                    spinnerChooseHoliday.setSelection(adaptPos);
                } else {
                    Log.d("VisitedPlaceEdit", "EditVPlace: NOT-MATCHED: " + chosenHoliday.getName());
                }
            }
        }

        Log.d("VPlaceStorage", "EditVPlace: ON-ENTER- spinnerChooseHoliday.getSelectedItem: " + spinnerChooseHoliday.getSelectedItem());

    }



    public String isNull(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    public void setAddress(){
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
            chosenPlace = response.getPlace();
            Log.d("VPlaceLocation", "ViewVPlace: Place found: " + chosenPlace.getName());

            textViewVPlaceAddress.setText("Address: " + chosenPlace.getAddress());

        }).addOnFailureListener(e ->
                Log.d("VPlaceLocation", "ViewVPlace: Place Not Found"));
    }

    public void setupAutoComplete(){
        // Initialize the AutocompleteSupportFragment.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDMPsU2SV31MnUAONzl0WEI2iEDkU31kZ0", Locale.UK);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("PlacesAutoComplete", "Place: " + place.getName() + ", " + place.getId());

                placeID = place.getId();
                textViewVPlaceAddress.setText("Address: " + place.getAddress());

                Log.d("PlacesAutoComplete", "CreateVPlace: getAddress: " + place.getAddress());
                //Log.d("PlacesAutoComplete", "CreateVPlace: Lat, Long: " + place.getLatLng().latitude + ", " + place.getLatLng().longitude);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d("PlacesAutoComplete", "An error occurred: " + status);
            }
        });

    }

    public void printImage(Boolean choice){
        if (choice) {
            Log.d("ImageFind", "CreateHoliday, printImage(true)");

            constLayImageSaved.setVisibility(View.VISIBLE);
            constLayNoImage.setVisibility(View.INVISIBLE);

            imageViewVPlaceImage.setImageBitmap(bmpHolidayImage);


        } else {
            Log.d("ImageFind", "CreateHoliday, printImage(false)");

            constLayImageSaved.setVisibility(View.INVISIBLE);
            constLayNoImage.setVisibility(View.VISIBLE);

            imageViewVPlaceImage.setImageBitmap(null);

        }
    }

    public void setValues(){
        adapterVisitedPlaces = new HashMap<>();
        setSpinner = true;

        textViewVPlaceName.setText(isNull(mVisitedPlace.getName()));
        textViewVPlaceDate.setText(isNull(mVisitedPlace.getDate()));
        textViewVPlaceTravellers.setText(isNull(mVisitedPlace.getTravellers()));
        textViewVPlaceNotes.setText(isNull(mVisitedPlace.getNotes()));

        if (vPlaceImage != null){
            pictureSaved = true;
            mImagePath = vPlaceImage.getPath();
            mImagePathOrig = mImagePath;
            mImageDate = vPlaceImage.getDate();
            mImageTag = vPlaceImage.getTag();

            textViewImageTag.setText(mImageTag);

            File imageFile = new File(mImagePath);
            if (imageFile.exists()){
                bmpHolidayImage = BitmapFactory.decodeFile(mImagePath);

                Log.d("ImageFind", "CreateHoliday, IMAGE FOUND");

                printImage(true);

            }
        }

        placeID = mVisitedPlace.getLocation();
        if (placeID != null){
            setAddress();
        }


    }

}
