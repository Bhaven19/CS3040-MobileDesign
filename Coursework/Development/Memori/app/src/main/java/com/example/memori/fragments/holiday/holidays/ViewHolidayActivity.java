package com.example.memori.fragments.holiday.holidays;

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

import java.io.File;

public class ViewHolidayActivity extends AppCompatActivity {

    private Holiday impHoliday;

    private TextView viewHolidayNotes, viewHolidayStartingLoc, viewHolidayDestination, viewHolidayStartDate, viewHolidayEndDate, viewHolidayCompanions, viewHolidayName, viewNoImage;
    private ImageView viewHolidayImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_holiday);

        Intent obtainIntent = getIntent();
        impHoliday = (Holiday) obtainIntent.getSerializableExtra("chosenHoliday");

        viewHolidayName = findViewById(R.id.text_HolidayName);
        viewHolidayStartingLoc = findViewById(R.id.label_HolidayStartLoc);
        viewHolidayDestination = findViewById(R.id.label_HolidayEndLoc);
        viewHolidayStartDate = findViewById(R.id.label_HolidayStartDate);
        viewHolidayEndDate = findViewById(R.id.label_HolidayEndDate);
        viewHolidayCompanions = findViewById(R.id.text_holidayCompanions);
        viewHolidayNotes = findViewById(R.id.text_holidayNotes);
        viewHolidayImage = findViewById(R.id.holidayImage);
        viewNoImage = findViewById(R.id.label_noImage);

        viewHolidayNotes.setText(impHoliday.getNotes());
        viewHolidayStartingLoc.setText(impHoliday.getStartingLoc());
        viewHolidayDestination.setText(impHoliday.getDestination());
        viewHolidayStartDate.setText(impHoliday.getStartDate());
        viewHolidayEndDate.setText(impHoliday.getEndDate());
        viewHolidayCompanions.setText(impHoliday.getTravellers());
        viewHolidayName.setText(impHoliday.getName());

        Log.d("ImageFind", "Image path, impHoliday.getImagePath(): " + impHoliday.getImagePath());
        String pathName = impHoliday.getImagePath();

        if (pathName == null) {
            Log.d("ImageFind", "ViewHolidayActivity, NO IMAGE SAVED");

        } else {
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
        }


        setResult(2, obtainIntent);

    }
}
