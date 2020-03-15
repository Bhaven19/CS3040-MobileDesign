package com.example.memori.ui.holiday;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.memori.R;
import com.example.memori.components.HolidayDate;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditHolidayActivity extends AppCompatActivity implements View.OnClickListener{

    private Holiday editedHoliday;
    private Images editedImage;

    private TextView textViewNoImage;
    private EditText textViewHolidayName, textViewStartDate, textViewEndDate, textViewTravellers, textViewNotes, textViewImageTag;
    private ImageView imageViewHolidayImage;
    private Button mAddImage, mSaveHoliday, btnStartDate, btnEndDate, btnRemoveImage;
    private ConstraintLayout constLayNoImage, constLayImageSaved;

    private final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Boolean validDate = false;
    private HolidayDate startDate, endDate;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/memori/";
    private Boolean pictureSaved = false;
    private String mImagePath, mImagePathOrig, mImageDate, mImageTag, mImageLocation;
    private Bitmap bmpHolidayImage;

    private Boolean permissionsGranted = false;
    private Place chosenPlace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_holiday);

        constLayNoImage = findViewById(R.id.constLay_noImage);
        constLayImageSaved = findViewById(R.id.constLay_imageSaved);

        textViewHolidayName = findViewById(R.id.edit_vPlaceName);

        textViewStartDate = findViewById(R.id.edit_holidayStartDate);
        textViewEndDate = findViewById(R.id.edit_holidayEndDate);
        textViewStartDate.setEnabled(false);
        textViewEndDate.setEnabled(false);

        textViewTravellers = findViewById(R.id.edit_vPlaceCompanions);
        textViewNotes = findViewById(R.id.edit_vPlaceNotes);
        textViewImageTag = findViewById(R.id.edit_holidayImageTag);

        imageViewHolidayImage = findViewById(R.id.imageView_newHolidayImage);

        mAddImage = findViewById(R.id.btn_holidaySaveImage);
        mAddImage.setOnClickListener(this);

        mSaveHoliday = findViewById(R.id.btn_saveHoliday);
        mSaveHoliday.setOnClickListener(this);

        btnStartDate = findViewById(R.id.btn_holidayStartDate);
        btnStartDate.setOnClickListener(this);

        btnEndDate = findViewById(R.id.btn_holidayEndDate);
        btnEndDate.setOnClickListener(this);

        btnRemoveImage = findViewById(R.id.btn_holidayDeleteImage);
        btnRemoveImage.setOnClickListener(this);

        getCurrentPlace();

        setValues();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_saveHoliday:
                Intent saveHolidayIntent = new Intent();

                if (TextUtils.isEmpty(textViewHolidayName.getText())) {

                    setResult(0, saveHolidayIntent);

                    Toast.makeText(getApplicationContext(), "Fields are empty, nothing was saved", Toast.LENGTH_LONG).show();

                } else {
                    String hName = textViewHolidayName.getText().toString();
                    String hStartDate = textViewStartDate.getText().toString();
                    String hEndDate = textViewEndDate.getText().toString();
                    String hCompanions = textViewTravellers.getText().toString();
                    String hNotes = textViewNotes.getText().toString();
                    String mImageTag = textViewImageTag.getText().toString();

                    if(mImagePath.equals(mImagePathOrig)){
                        editedImage.setTag(mImageTag);
                        saveHolidayIntent.putExtra("editedImage", editedImage);

                    } else {
                        String placeID = chosenPlace.getId();

                        editedImage.setPath(mImagePath);
                        editedImage.setDate(mImageDate);
                        editedImage.setTag(mImageTag);
                        editedImage.setLocation(placeID);
                        saveHolidayIntent.putExtra("editedImage", editedImage);
                    }

                    Log.d("ImageTrackLocation", "getPlace: " + chosenPlace.getName());

                    editedHoliday.setName(hName);
                    editedHoliday.setStartDate(hStartDate);
                    editedHoliday.setEndDate(hEndDate);
                    editedHoliday.setTravellers(hCompanions);
                    editedHoliday.setNotes(hNotes);

                    saveHolidayIntent.putExtra("editedHoliday", editedHoliday);

                    setResult(3, saveHolidayIntent);
                }
                finish();

                break;

            case R.id.btn_holidaySaveImage:
                if (!pictureSaved) {
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

                imageViewHolidayImage.setImageBitmap(bmpHolidayImage);
                break;

            case R.id.btn_holidayStartDate:
                // Get Current Date
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                validDate = false;

                DatePickerDialog startDatePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDate = new HolidayDate(dayOfMonth, monthOfYear, year);

                                textViewStartDate.setText(startDate.toString());

                            }
                        }, mYear, mMonth, mDay);
                startDatePickerDialog.show();
                break;

            case R.id.btn_holidayEndDate:
                // Get Current Date
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                validDate = false;

                DatePickerDialog endDatePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endDate = new HolidayDate(dayOfMonth, monthOfYear, year);

                                if (endDate.validDate(startDate)){
                                    textViewEndDate.setText(endDate.toString());

                                } else {
                                    displayToast("Invalid Date: The End Date must not be before the Start Date, please try again");

                                }

                            }
                        }, mYear, mMonth, mDay);
                endDatePickerDialog.show();
                break;

            case R.id.btn_holidayDeleteImage:
                printImage(false);

                mImagePath = "";
                mImageDate = "";
                mImageTag = "";
                bmpHolidayImage = null;
                pictureSaved = false;

                break;
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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

                    Toast.makeText(this, "Images Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            saveImage(thumbnail);

            Toast.makeText(this, "Images Saved!", Toast.LENGTH_SHORT).show();

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
            Log.d("ImageFind", "CreateHoliday: " + f.getAbsolutePath());
            mImagePath = f.getAbsolutePath();
            mImageDate = HolidayDate.getCurrentDate();

            pictureSaved = true;

        } catch (IOException e1) {
            e1.printStackTrace();

        }


        File imageFile = new File(mImagePath);
        if (imageFile.exists()){
            bmpHolidayImage = BitmapFactory.decodeFile(mImagePath);

            Log.d("ImageFind", "CreateHoliday, IMAGE FOUND");

            printImage(true);

        } else {
            Log.d("ImageFind", "CreateHoliday, IMAGE NOT FOUND");

        }

    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    public void printImage(Boolean choice){
        if (choice) {
            Log.d("ImageFind", "CreateHoliday, printImage(true)");

            constLayImageSaved.setVisibility(View.VISIBLE);
            constLayNoImage.setVisibility(View.INVISIBLE);

            imageViewHolidayImage.setImageBitmap(bmpHolidayImage);


        } else {
            Log.d("ImageFind", "CreateHoliday, printImage(false)");

            constLayImageSaved.setVisibility(View.INVISIBLE);
            constLayNoImage.setVisibility(View.VISIBLE);

            imageViewHolidayImage.setImageBitmap(null);

        }
    }

    public void getCurrentPlace(){
        if (!permissionsGranted) {
            RxPermissions rxPermissions = new RxPermissions(this);

            Log.d("ImageTrackLocation", "getActivity: " + this);

            rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(granted -> {
                if (granted) {
                    Log.d("ImageTrackLocation", "Permissions Granted");
                    permissionsGranted = true;
                } else {
                    Log.d("ImageTrackLocation", "Permissions Denied");
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

                    Log.d("ImageTrackLocation", "Most Likely Place Name: " + chosenPlace.getName());
                    Log.d("ImageTrackLocation", "Most Likely Place ID: " + chosenPlace.getId());
                    Log.d("ImageTrackLocation", "Most Likely Place getAddress: " + chosenPlace.getAddress());

                } else {
                    Exception exception = task.getException();

                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;

                        Log.d("ImageTrackLocation", "Place not found: " + apiException.getStatusCode());

                    }
                }
            });

        }

    }

    public void setValues(){
        editedHoliday = (Holiday) getIntent().getSerializableExtra("chosenHoliday");
        editedImage = (Images) getIntent().getSerializableExtra("chosenHolidayImage");

        textViewHolidayName.setText(editedHoliday.getName());
        textViewStartDate.setText(editedHoliday.getStartDate());
        textViewEndDate.setText(editedHoliday.getEndDate());
        textViewTravellers.setText(editedHoliday.getTravellers());
        textViewNotes.setText(editedHoliday.getNotes());
        textViewImageTag.setText(editedImage.getTag());

        if (editedImage != null) {
            mImagePath = editedImage.getPath();
            mImagePathOrig = editedImage.getPath();
            mImageDate = editedImage.getDate();
            mImageLocation = editedImage.getLocation();

            pictureSaved = true;

            File imageFile = new File(mImagePath);
            if (imageFile.exists()){
                bmpHolidayImage = BitmapFactory.decodeFile(mImagePath);

                Log.d("ImageFind", "CreateHoliday, IMAGE FOUND");

                printImage(true);

            }


        }
    }

}
