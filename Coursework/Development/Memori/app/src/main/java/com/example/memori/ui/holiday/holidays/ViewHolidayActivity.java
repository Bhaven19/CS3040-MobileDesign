package com.example.memori.ui.holiday.holidays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;

import java.io.File;

public class ViewHolidayActivity extends AppCompatActivity {

    private Holiday impHoliday;

    private TextView viewHolidayNotes, viewHolidayStartingLoc, viewHolidayDestination, viewHolidayStartDate, viewHolidayEndDate, viewHolidayCompanions, viewHolidayName, viewNoImage;
    private ImageView viewHolidayImage;
    private Images impImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_holiday);

        Intent obtainIntent = getIntent();
        impHoliday = (Holiday) obtainIntent.getSerializableExtra("chosenHoliday");

        if (obtainIntent.getSerializableExtra("chosenImage") != null){
            impImage = (Images) obtainIntent.getSerializableExtra("chosenImage");
        } else {
            impImage = null;
        }

        viewHolidayName = findViewById(R.id.text_VPlaceName);
        viewHolidayStartingLoc = findViewById(R.id.label_VPlaceLoc);
        viewHolidayDestination = findViewById(R.id.text_VPlaceLocation);
        viewHolidayStartDate = findViewById(R.id.text_VPlaceDate);
        viewHolidayEndDate = findViewById(R.id.label_HolidayEndDate);
        viewHolidayCompanions = findViewById(R.id.text_VPlaceCompanions);
        viewHolidayNotes = findViewById(R.id.text_VPlaceNotes);
        viewHolidayImage = findViewById(R.id.holidayImage);
        viewNoImage = findViewById(R.id.label_noImage);

        viewHolidayNotes.setText(impHoliday.getNotes());
        viewHolidayStartingLoc.setText(impHoliday.getStartingLoc());
        viewHolidayDestination.setText(impHoliday.getDestination());
        viewHolidayStartDate.setText(impHoliday.getStartDate());
        viewHolidayEndDate.setText(impHoliday.getEndDate());
        viewHolidayCompanions.setText(impHoliday.getTravellers());
        viewHolidayName.setText(impHoliday.getName());

        if (impImage != null) {
            Log.d("ImageFind", "Images path, impHoliday.getImagePath(): " + impImage.getPath());
            String pathName = impImage.getPath();

            File imageFile = new File(pathName);

            if (imageFile.exists()){
                viewNoImage.setVisibility(View.INVISIBLE);

                Bitmap mHolidayImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                viewHolidayImage.setImageBitmap(mHolidayImage);

                Log.d("ImageFind", "ViewHolidayActivity, IMAGE FOUND");

            } else {
                viewNoImage.setVisibility(View.VISIBLE);

                Log.d("ImageFind", "ViewHolidayActivity, IMAGE NOT FOUND");

            }
        } else {
            Log.d("ImageFind", "ViewHolidayActivity, NO IMAGE SAVED");

        }


        setResult(2, obtainIntent);

    }
}
