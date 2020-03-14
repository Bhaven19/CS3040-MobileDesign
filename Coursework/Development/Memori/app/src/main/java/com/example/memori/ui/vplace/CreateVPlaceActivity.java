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
import android.widget.AdapterView;
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

import com.example.memori.R;
import com.example.memori.components.HolidayDate;
import com.example.memori.database.entities.Images;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
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
import java.util.List;
import java.util.Locale;

public class CreateVPlaceActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText textViewVPlaceName, textViewVPlaceDate, textViewVPlaceTravellers, textViewVPlaceNotes;
    private TextView textViewVPlaceAddress, textViewNoImage;
    private Spinner spinnerChooseHoliday;
    private ImageView imageViewVPlaceImage;
    private Button mAddImage, mSaveVPlace, btnDate, btnRemoveImage;
    private ImageButton mGetCurrentLoc;

    private final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Boolean validDate = false;
    private HolidayDate date;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/memori/";
    private Boolean pictureSaved = false;
    private String mImagePath, mImageDate, mImageTag;
    private Bitmap bmpHolidayImage;

    private ArrayList<String> allHolidays;
    private String chosenHolidayName = "";

    public static final int NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 4;

    private String placeID = "";

    private Boolean permissionsGranted = false;

    private Place chosenPlace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vplace);

        allHolidays = getIntent().getStringArrayListExtra("holidayList");

        spinnerChooseHoliday = findViewById(R.id.spinner_vPlaceChooseHoliday);
        setupSpinner();

        textViewVPlaceName = findViewById(R.id.edit_vPlaceName);
        textViewVPlaceDate = findViewById(R.id.edit_VPlaceDate);
        textViewVPlaceAddress = findViewById(R.id.label_VPlaceAddress);
        textViewVPlaceTravellers = findViewById(R.id.edit_vPlaceCompanions);
        textViewVPlaceNotes = findViewById(R.id.edit_vPlaceNotes);

        textViewVPlaceDate.setEnabled(false);

        imageViewVPlaceImage = findViewById(R.id.imageView_newVPlaceImage);
        textViewNoImage = findViewById(R.id.label_vPlaceNoImage);

        mAddImage = findViewById(R.id.btn_saveVPlaceImage);
        mAddImage.setOnClickListener(this);

        mSaveVPlace = findViewById(R.id.btn_saveVPlace);
        mSaveVPlace.setOnClickListener(this);

        btnDate = findViewById(R.id.btn_selectVPlaceDate);
        btnDate.setOnClickListener(this);

        mGetCurrentLoc = findViewById(R.id.btn_getCurrentLocation);
        mGetCurrentLoc.setOnClickListener(this);

        btnRemoveImage = findViewById(R.id.btn_deleteVPlaceImage);
        btnRemoveImage.setOnClickListener(this);

        setupAutoComplete();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_saveVPlace:
                Intent saveVPlaceIntent = new Intent();

                if (chosenHolidayName == null || TextUtils.isEmpty(textViewVPlaceName.getText()) || TextUtils.isEmpty(textViewVPlaceDate.getText()) ) {

                    setResult(0, saveVPlaceIntent);

                    Toast.makeText(getApplicationContext(), "Fields are empty, nothing was saved", Toast.LENGTH_LONG).show();

                } else {
                    String vPlaceHoliday = chosenHolidayName;
                    String vPlaceName = textViewVPlaceName.getText().toString();
                    String vPlaceDate = "";
                    String vPlaceLocation = placeID;
                    String vPlaceCompanions = textViewVPlaceTravellers.getText().toString();
                    String vPlaceNotes = textViewVPlaceNotes.getText().toString();

                    if (date != null){
                        vPlaceDate = date.toString();
                    }

                    saveVPlaceIntent.putExtra("vPlaceHolidayName", vPlaceHoliday);
                    saveVPlaceIntent.putExtra("vPlaceName", vPlaceName);
                    saveVPlaceIntent.putExtra("vPlaceDate", vPlaceDate);
                    saveVPlaceIntent.putExtra("vPlaceLocation", vPlaceLocation);
                    saveVPlaceIntent.putExtra("vPlaceCompanions", vPlaceCompanions);
                    saveVPlaceIntent.putExtra("vPlaceNotes", vPlaceNotes);

                    mImageTag = "";
                    Images newImage = new Images(mImagePath, mImageDate, mImageTag, vPlaceLocation);

                    saveVPlaceIntent.putExtra("vImage", newImage);

                    setResult(NEW_VISITED_PLACE_ACTIVITY_REQUEST_CODE, saveVPlaceIntent);
                }
                finish();

                break;
            case R.id.btn_saveVPlaceImage:
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

                } else {
                    displayToast("You can only save 1 image, any new images will overwrite previous images");

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
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
            case R.id.btn_deleteVPlaceImage:
                printImage(false);

                mImagePath = "";
                mImageDate = "";
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
                    saveImage(bitmap);

                    Toast.makeText(CreateVPlaceActivity.this, "Images Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateVPlaceActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            saveImage(thumbnail);


            Toast.makeText(CreateVPlaceActivity.this, "Images Saved!", Toast.LENGTH_SHORT).show();

        }
    }

    public void saveImage(Bitmap myBitmap) {
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
            mImageDate = HolidayDate.getCurrentDate();

            pictureSaved = true;

            printImage(true);

        } catch (IOException e1) {
            e1.printStackTrace();
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

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    public void setupSpinner(){
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChooseHoliday.setAdapter(adapter);

        for (String currentHolidayName : allHolidays){
            adapter.add(currentHolidayName);
        }

        spinnerChooseHoliday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenHolidayName = (String) spinnerChooseHoliday.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void printImage(Boolean choice){
        if (choice) {
            Log.d("ImageFind", "CreateHoliday, printImage(true)");

            textViewNoImage.setVisibility(View.INVISIBLE);

            imageViewVPlaceImage.setVisibility(View.VISIBLE);
            imageViewVPlaceImage.setImageBitmap(bmpHolidayImage);

            mAddImage.setVisibility(View.INVISIBLE);
            btnRemoveImage.setVisibility(View.VISIBLE);

        } else {
            Log.d("ImageFind", "CreateHoliday, printImage(false)");

            textViewNoImage.setVisibility(View.VISIBLE);

            imageViewVPlaceImage.setVisibility(View.INVISIBLE);
            imageViewVPlaceImage.setImageBitmap(null);

            mAddImage.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.INVISIBLE);

        }
    }

}
