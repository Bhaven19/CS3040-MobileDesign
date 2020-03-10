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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.example.memori.database.listadapters.VPlaceListAdapter;
import com.example.memori.ui.holiday.vplaces.ViewVPlace;

import java.io.File;
import java.util.ArrayList;

public class ViewHolidayActivity extends AppCompatActivity {

    private Holiday impHoliday;

    private TextView viewHolidayNotes, viewHolidayStartingLoc, viewHolidayDestination, viewHolidayStartDate, viewHolidayEndDate, viewHolidayCompanions, viewHolidayName, viewNoImage;
    private ImageView viewHolidayImage;
    private Images holidayImage, vPlaceImage;

    private ArrayList<VisitedPlace> allVisitedPlaces = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_holiday);

        allVisitedPlaces.clear();

        getValuesFromIntent();

        viewHolidayName = findViewById(R.id.title_holidayName);
        viewHolidayStartDate = findViewById(R.id.text_holidayStartDate);
        viewHolidayEndDate = findViewById(R.id.text_holidayEndDate);
        viewHolidayCompanions = findViewById(R.id.text_holidayCompanions);
        viewHolidayNotes = findViewById(R.id.text_holidayNotes);
        viewHolidayImage = findViewById(R.id.imageView_holiday);
        viewNoImage = findViewById(R.id.label_noImage);

        viewHolidayNotes.setText(impHoliday.getNotes());
        viewHolidayStartDate.setText(impHoliday.getStartDate());
        viewHolidayEndDate.setText(impHoliday.getEndDate());
        viewHolidayCompanions.setText(impHoliday.getTravellers());
        viewHolidayName.setText(impHoliday.getName());

        if (holidayImage != null) {
            Log.d("ImageFind", "Images path, impHoliday.getImagePath(): " + holidayImage.getPath());
            String pathName = holidayImage.getPath();

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

        setupRecyclerView();

    }

    private void getValuesFromIntent(){
        impHoliday = (Holiday) getIntent().getSerializableExtra("chosenHoliday");

        if (getIntent().getSerializableExtra("chosenImage") != null){
            holidayImage = (Images) getIntent().getSerializableExtra("chosenImage");
        } else {
            holidayImage = null;
        }

        vPlaceImage = (Images) getIntent().getSerializableExtra("chosenVisitedPlaceImage");

        ArrayList<Integer> vPlaceArrayIDs = getIntent().getIntegerArrayListExtra("vPlaceArrayID");
        Bundle allVisitedPlacesBundle = getIntent().getBundleExtra("bundle");

        for (int i = 0; i < vPlaceArrayIDs.size(); i++) {
            VisitedPlace visitedPlace = (VisitedPlace) allVisitedPlacesBundle.get("VPlaceObj" + vPlaceArrayIDs.get(i));

            allVisitedPlaces.add(visitedPlace);
        }

    }

    public void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview_holiday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        VPlaceListAdapter vPlaceListAdapter = new VPlaceListAdapter(getApplicationContext());

        Log.d("VPlaceClick", "HolidayFragment, setting up recycler view");

        recyclerView.setAdapter(vPlaceListAdapter);
        vPlaceListAdapter.setVPlaces(allVisitedPlaces);

        vPlaceListAdapter.setClickListener(new VPlaceListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VisitedPlace myVisitedPlace = vPlaceListAdapter.getWordAtPosition(position);

                Intent viewIntent = new Intent(getApplicationContext(), ViewVPlace.class);
                viewIntent.putExtra("chosenVisitedPlace", myVisitedPlace);
                viewIntent.putExtra("chosenImage", vPlaceImage);

                startActivity(viewIntent);

            }

        });

    }

}
