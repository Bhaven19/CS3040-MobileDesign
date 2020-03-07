package com.example.memori.ui.holiday.vplaces;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.components.HolidayDate;
import com.example.memori.database.entities.Images;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class CreateVPlaceActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText textViewVPlaceName, textViewVPlaceDate, textViewVPlaceTravellers, textViewVPlaceNotes;
    private Spinner spinnerChooseHoliday;
    private ImageView imageViewVPlaceImage;
    private Button mAddImage, mSaveVPlace, btnDate;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vplace);

        allHolidays = getIntent().getStringArrayListExtra("holidayList");

        spinnerChooseHoliday = findViewById(R.id.spinner_vPlaceChooseHoliday);
        setupSpinner();

        textViewVPlaceName = findViewById(R.id.edit_vPlaceName);
        textViewVPlaceDate = findViewById(R.id.edit_VPlaceDate);
        textViewVPlaceTravellers = findViewById(R.id.edit_vPlaceCompanions);
        textViewVPlaceNotes = findViewById(R.id.edit_vPlaceNotes);

        textViewVPlaceDate.setEnabled(false);

        mAddImage = findViewById(R.id.btn_saveVPlaceImage);
        mAddImage.setOnClickListener(this);

        mSaveVPlace = findViewById(R.id.btn_saveVPlace);
        mSaveVPlace.setOnClickListener(this);

        btnDate = findViewById(R.id.btn_selectVPlaceDate);
        btnDate.setOnClickListener(this);

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
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("PlacesAutoComplete", "Place: " + place.getName() + ", " + place.getId());
                placeID = place.getId();

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
                    Images newImage = new Images(mImagePath, mImageDate, mImageTag);

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

                    Toast.makeText(CreateVPlaceActivity.this, "Images Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateVPlaceActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String path = saveImage(thumbnail);

            bmpHolidayImage = thumbnail;

            Toast.makeText(CreateVPlaceActivity.this, "Images Saved!", Toast.LENGTH_SHORT).show();

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
            mImageDate = HolidayDate.getCurrentDate();

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

}
