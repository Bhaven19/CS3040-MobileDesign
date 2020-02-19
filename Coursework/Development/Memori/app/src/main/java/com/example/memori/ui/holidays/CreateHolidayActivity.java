package com.example.memori.ui.holidays;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.ui.HolidayDate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CreateHolidayActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText textViewHolidayName, textViewStartingLoc, textViewDestination, textViewStartDate, textViewEndDate, textViewTravellers, textViewNotes;
    private Button mAddImage, mSaveHoliday, btnStartDate, btnEndDate;

    private final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Boolean validDate = false;
    private HolidayDate startDate, endDate;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/memori/";
    private Boolean pictureSaved = false;
    private String mImagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_holiday);

        textViewHolidayName = findViewById(R.id.edit_Name);
        textViewStartingLoc = findViewById(R.id.edit_StartLoc);
        textViewDestination = findViewById(R.id.edit_EndLoc);

        textViewStartDate = findViewById(R.id.edit_StartDate);
        textViewEndDate = findViewById(R.id.edit_EndDate);
        textViewStartDate.setEnabled(false);
        textViewEndDate.setEnabled(false);

        textViewTravellers = findViewById(R.id.edit_Companions);
        textViewNotes = findViewById(R.id.edit_Notes);

        mAddImage = findViewById(R.id.btn_saveImage);
        mAddImage.setOnClickListener(this);

        mSaveHoliday = findViewById(R.id.btn_saveHoliday);
        mSaveHoliday.setOnClickListener(this);

        btnStartDate = findViewById(R.id.btn_startDate);
        btnStartDate.setOnClickListener(this);

        btnEndDate = findViewById(R.id.btn_endDate);
        btnEndDate.setOnClickListener(this);



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
                    String hStartingLoc = textViewStartingLoc.getText().toString();
                    String hDestination = textViewDestination.getText().toString();


                    String hStartDate = "";
                    String hEndDate = "";
                    if (startDate == null && endDate == null){

                    } else if (startDate == null){
                        hStartDate = startDate.toString();

                    } else if (endDate == null){
                        hStartDate = startDate.toString();

                    } else {
                        hStartDate = startDate.toString();
                        hEndDate = endDate.toString();
                    }

                    String hCompanions = textViewTravellers.getText().toString();
                    String hNotes = textViewNotes.getText().toString();
                    String hImagePath = mImagePath;

                    saveHolidayIntent.putExtra("holidayName", hName);
                    saveHolidayIntent.putExtra("holidayStartingLoc", hStartingLoc);
                    saveHolidayIntent.putExtra("holidayDestination", hDestination);
                    saveHolidayIntent.putExtra("holidayStartDate", hStartDate);
                    saveHolidayIntent.putExtra("holidayEndDate", hEndDate);
                    saveHolidayIntent.putExtra("holidayTravellers", hCompanions);
                    saveHolidayIntent.putExtra("holidayNotes", hNotes);
                    saveHolidayIntent.putExtra("holidayImagePath", hImagePath);

                    setResult(1, saveHolidayIntent);
                }
                finish();

                break;
            case R.id.btn_saveImage:
                if (pictureSaved == false) {
                    AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
                    pictureDialog.setTitle("Select Action");
                    String[] pictureDialogItems = {
                            "Select Image from gallery",
                            "Take new Image with camera"};
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
                            "Select Image from gallery",
                            "Take new Image with camera"};
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
                break;
            case R.id.btn_startDate:
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
            case R.id.btn_endDate:
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

                    Toast.makeText(CreateHolidayActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateHolidayActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String path = saveImage(thumbnail);

            Toast.makeText(CreateHolidayActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

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
            Log.d("ImageFind", "CreateHoliday: " + f.getAbsolutePath());
            mImagePath = f.getAbsolutePath();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

}
