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
import com.example.memori.database.HolidayRepository;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EditVPlace extends AppCompatActivity implements View.OnClickListener{

    private HolidayRepository holidayRepository;

    private EditText textViewVPlaceName, textViewVPlaceDate, textViewVPlaceLoc, textViewVPlaceTravellers, textViewVPlaceNotes;
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
    private String mImagePath;
    private Bitmap bmpHolidayImage;

    private ArrayList<String> allHolidays;
    private HashMap<String, Integer> adapterVisitedPlaces;
    private String mImageDate = "", mImageTag = "";
    private Holiday chosenHoliday;

    public static final int SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE = 6;

    private VisitedPlace mVisitedPlace;
    private Images vPlaceImage;

    private Boolean setSpinner = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vplace);

        mVisitedPlace = (VisitedPlace) getIntent().getSerializableExtra("chosenVisitedPlace");
        vPlaceImage = (Images) getIntent().getSerializableExtra("vPlaceImage");
        chosenHoliday = (Holiday) getIntent().getSerializableExtra("chosenHoliday");
        allHolidays = getIntent().getStringArrayListExtra("holidayNameList");

        textViewVPlaceName = findViewById(R.id.edit_vPlaceName);
        textViewVPlaceDate = findViewById(R.id.edit_VPlaceDate);
        textViewVPlaceLoc = findViewById(R.id.edit_vPlaceLoc);
        textViewVPlaceTravellers = findViewById(R.id.edit_vPlaceCompanions);
        textViewVPlaceNotes = findViewById(R.id.edit_vPlaceNotes);

        textViewVPlaceDate.setEnabled(false);

        mAddImage = findViewById(R.id.btn_saveVPlaceImage);
        mAddImage.setOnClickListener(this);

        mSaveVPlace = findViewById(R.id.btn_saveVPlace);
        mSaveVPlace.setOnClickListener(this);

        btnDate = findViewById(R.id.btn_selectVPlaceDate);
        btnDate.setOnClickListener(this);

        setValues();

        setupSpinner();
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
                    mVisitedPlace.setLocation(textViewVPlaceLoc.getText().toString());
                    mVisitedPlace.setTravellers(textViewVPlaceTravellers.getText().toString());
                    mVisitedPlace.setNotes(textViewVPlaceNotes.getText().toString());

                    if (date != null){
                        mVisitedPlace.setDate(date.toString());
                    }

                    vPlaceImage.setPath(mImagePath);
                    vPlaceImage.setDate(mImageDate);
                    vPlaceImage.setTag(mImageTag);

                    saveVPlaceIntent.putExtra("editedVPlaceHolidayName", vPlaceHoliday);
                    saveVPlaceIntent.putExtra("editedVPlace", mVisitedPlace);
                    saveVPlaceIntent.putExtra("editedVPlaceImage", vPlaceImage);

                    setResult(SUCCESSFULY_EDITED_VISITED_PLACE_ACTIVITY_REQUEST_CODE, saveVPlaceIntent);
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

    public void setValues(){
        adapterVisitedPlaces = new HashMap<>();
        setSpinner = true;

        textViewVPlaceName.setText(isNull(mVisitedPlace.getName()));
        textViewVPlaceDate.setText(isNull(mVisitedPlace.getDate()));
        textViewVPlaceLoc.setText(isNull(mVisitedPlace.getLocation()));
        textViewVPlaceTravellers.setText(isNull(mVisitedPlace.getTravellers()));
        textViewVPlaceNotes.setText(isNull(mVisitedPlace.getNotes()));

        if (vPlaceImage.getPath() != null){
            pictureSaved = true;
            mImagePath = vPlaceImage.getPath();
            mImageDate = vPlaceImage.getDate();
            mImageTag = vPlaceImage.getTag();
        }


    }

    public String isNull(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

}
