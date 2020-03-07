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
import com.example.memori.database.entities.VisitedPlace;

import java.io.File;
import java.util.ArrayList;

public class ViewHolidayActivity extends AppCompatActivity {

    private Holiday impHoliday;

    private TextView viewHolidayNotes, viewHolidayStartingLoc, viewHolidayDestination, viewHolidayStartDate, viewHolidayEndDate, viewHolidayCompanions, viewHolidayName, viewNoImage;
    private ImageView viewHolidayImage;
    private Images impImage;

    private ArrayList<VisitedPlace> allVisitedPlaces = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_holiday);

        Intent obtainIntent = getIntent();

        getValuesFromIntent();

        viewHolidayName = findViewById(R.id.text_VPlaceName);
        viewHolidayStartDate = findViewById(R.id.text_VPlaceDate);
        viewHolidayEndDate = findViewById(R.id.label_HolidayEndDate);
        viewHolidayCompanions = findViewById(R.id.text_VPlaceCompanions);
        viewHolidayNotes = findViewById(R.id.text_VPlaceNotes);
        viewHolidayImage = findViewById(R.id.holidayImage);
        viewNoImage = findViewById(R.id.label_noImage);

        viewHolidayNotes.setText(impHoliday.getNotes());
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

    private void getValuesFromIntent(){
        impHoliday = (Holiday) getIntent().getSerializableExtra("chosenHoliday");

        if (getIntent().getSerializableExtra("chosenImage") != null){
            impImage = (Images) getIntent().getSerializableExtra("chosenImage");
        } else {
            impImage = null;
        }

        ArrayList<Integer> vPlaceArrayIDs = getIntent().getIntegerArrayListExtra("vPlaceArrayID");
        Bundle allVisitedPlacesBundle = getIntent().getBundleExtra("bundle");

        for (int i = 0; i < vPlaceArrayIDs.size(); i++) {
            VisitedPlace visitedPlace = (VisitedPlace) allVisitedPlacesBundle.get("VPlaceObj" + vPlaceArrayIDs.get(i));

            allVisitedPlaces.add(visitedPlace);
        }




    }
}
