package com.example.memori.ui.holiday;

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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.components.HolidayDate;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EditHolidayActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText textViewHolidayName, textViewStartDate, textViewEndDate, textViewTravellers, textViewNotes;
    private ImageView imageViewHolidayImage;
    private Button mAddImage, mSaveHoliday, btnStartDate, btnEndDate;

    private final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private HolidayDate startDate, endDate;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/memori/";
    private String mImagePath, mImageDate, mImageTag;
    private Bitmap bmpHolidayImage;

    private Holiday editedHoliday;
    private Images editedImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_holiday);

        editedHoliday = (Holiday) getIntent().getSerializableExtra("chosenHoliday");
        editedImage = (Images) getIntent().getSerializableExtra("chosenHolidayImage");

        textViewHolidayName = findViewById(R.id.edit_vPlaceName);

        textViewStartDate = findViewById(R.id.edit_holidayStartDate);
        textViewEndDate = findViewById(R.id.edit_holidayEndDate);
        textViewStartDate.setEnabled(false);
        textViewEndDate.setEnabled(false);

        textViewTravellers = findViewById(R.id.edit_vPlaceCompanions);
        textViewNotes = findViewById(R.id.edit_vPlaceNotes);

        imageViewHolidayImage = findViewById(R.id.imageView_newHolidayImage);

        mAddImage = findViewById(R.id.btn_holidaySaveImage);
        mAddImage.setOnClickListener(this);

        mSaveHoliday = findViewById(R.id.btn_saveHoliday);
        mSaveHoliday.setOnClickListener(this);

        btnStartDate = findViewById(R.id.btn_holidayStartDate);
        btnStartDate.setOnClickListener(this);

        btnEndDate = findViewById(R.id.btn_holidayEndDate);
        btnEndDate.setOnClickListener(this);

        setValues();



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_saveHoliday:
                Intent replyIntent = new Intent();

                if (TextUtils.isEmpty(textViewHolidayName.getText())) {

                    setResult(0, replyIntent);

                    Toast.makeText(getApplicationContext(), "Fields are empty, nothing was saved", Toast.LENGTH_LONG).show();

                } else {
                    String hName = textViewHolidayName.getText().toString();

                    String hStartDate = "";
                    String hEndDate = "";
                    if (startDate != null && endDate != null){
                        hStartDate = startDate.toString();
                        hEndDate = endDate.toString();

                    }

                    String hCompanions = textViewTravellers.getText().toString();
                    String hNotes = textViewNotes.getText().toString();

                    //NEED TO IMPLEMENT TAG
                    mImageTag = "";

                    editedImage.setPath(mImagePath);
                    editedImage.setDate(mImageDate);
                    editedImage.setTag(mImageTag);

                    editedHoliday.setName(hName);
                    editedHoliday.setStartDate(hStartDate);
                    editedHoliday.setEndDate(hEndDate);
                    editedHoliday.setTravellers(hCompanions);
                    editedHoliday.setNotes(hNotes);
                    editedHoliday.setImageID(editedImage.get_id());

                    replyIntent.putExtra("editedHoliday", editedHoliday);
                    replyIntent.putExtra("editedImage", editedImage);

                    setResult(3, replyIntent);

                }
                finish();

                break;
            case R.id.btn_holidaySaveImage:
                if (mImagePath.equals("")) {
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

    public void setValues(){
        textViewHolidayName.setText(editedHoliday.getName());
        textViewStartDate.setText(editedHoliday.getStartDate());
        textViewEndDate.setText(editedHoliday.getEndDate());
        textViewTravellers.setText(editedHoliday.getTravellers());
        textViewNotes.setText(editedHoliday.getNotes());

        mImagePath = editedImage.getPath();
        mImageDate = editedImage.getDate();
        mImageTag = editedImage.getTag();

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

                    Toast.makeText(EditHolidayActivity.this, "Images Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditHolidayActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String path = saveImage(thumbnail);

            bmpHolidayImage = thumbnail;

            Toast.makeText(EditHolidayActivity.this, "Images Saved!", Toast.LENGTH_SHORT).show();

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

}
