package com.example.memori.ui.holidays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;

import java.io.File;

public class ViewHolidayActivity extends AppCompatActivity {

    private Holiday impHoliday;

    private TextView viewHolidayNotes, viewHolidayStartingLoc, viewHolidayDestination, viewHolidayCompanions, viewHolidayName;
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
        viewHolidayCompanions = findViewById(R.id.text_holidayCompanions);
        viewHolidayNotes = findViewById(R.id.text_holidayNotes);
        viewHolidayImage = findViewById(R.id.holidayImage);

        viewHolidayNotes.setText(impHoliday.getNotes());
        viewHolidayStartingLoc.setText(impHoliday.getStartingLoc());
        viewHolidayDestination.setText(impHoliday.getDestination());
        viewHolidayCompanions.setText(impHoliday.getTravellers());
        viewHolidayName.setText(impHoliday.getName());

        String pathName = Environment.getExternalStorageDirectory() + "/memori/" + impHoliday.getImagePath();
        Log.d("ImageFind", "Image path, pathName: " + pathName);
        File imageFile = new File(pathName);

        if (imageFile.mkdir()){
            Toast.makeText(getApplicationContext(), "Image Found", Toast.LENGTH_LONG).show();
            viewHolidayImage.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeFile(impHoliday.getImagePath())));

            Log.d("ImageFind", "Image path, IMAGE FOUND");

        } else {
            Toast.makeText(getApplicationContext(), "Image Not Found", Toast.LENGTH_LONG).show();

            Log.d("ImageFind", "Image path, IMAGE NOT FOUND");

        }


        setResult(2, obtainIntent);

    }
}
