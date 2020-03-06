package com.example.memori.ui.holiday.vplaces;

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
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;

import java.io.File;

public class ViewVPlace extends AppCompatActivity {

    private VisitedPlace impVisitedPlace;
    private Images impImage;

    private TextView viewVPlaceName, viewVPlaceDate, viewVPlaceNotes, viewVPlaceCompanions, viewNoImage;
    private ImageView viewHolidayImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vplace);

        Intent obtainIntent = getIntent();

        impVisitedPlace = (VisitedPlace) obtainIntent.getSerializableExtra("chosenVisitedPlace");
        if (obtainIntent.getSerializableExtra("chosenImage") != null){
            impImage = (Images) obtainIntent.getSerializableExtra("chosenImage");
        } else {
            impImage = null;
        }


        viewVPlaceName = findViewById(R.id.text_VPlaceName);
        viewVPlaceDate = findViewById(R.id.text_VPlaceDate);
        viewVPlaceNotes = findViewById(R.id.text_VPlaceNotes);
        viewVPlaceCompanions = findViewById(R.id.text_VPlaceCompanions);
        //viewVPlaceLocation = findViewById(R.id.label_HolidayEndDate);


        viewHolidayImage = findViewById(R.id.holidayImage);
        viewNoImage = findViewById(R.id.label_noImage);

        viewVPlaceName.setText(impVisitedPlace.getName());
        viewVPlaceDate.setText(impVisitedPlace.getDate());
        viewVPlaceNotes.setText(impVisitedPlace.getNotes());
        viewVPlaceCompanions.setText(impVisitedPlace.getTravellers());

        if (impImage != null) {
            Log.d("ImageFind", "Images path, impVisitedPlace.getImagePath(): " + impImage.getPath());
            String pathName = impImage.getPath();

            File imageFile = new File(pathName);

            if (imageFile.exists()){
                viewNoImage.setVisibility(View.INVISIBLE);

                Bitmap mHolidayImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                viewHolidayImage.setImageBitmap(mHolidayImage);

                Log.d("ImageFind", "ViewVPlace, IMAGE FOUND");

            } else {
                viewNoImage.setVisibility(View.VISIBLE);

                Log.d("ImageFind", "ViewVPlace, IMAGE NOT FOUND");

            }
        } else {

            Log.d("ImageFind", "ViewVPlace, NO IMAGE SAVED");

        }


        setResult(5, obtainIntent);

    }
}
