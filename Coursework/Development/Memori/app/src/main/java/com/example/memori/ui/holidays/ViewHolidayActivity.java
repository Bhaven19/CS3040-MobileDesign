package com.example.memori.ui.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;

public class ViewHolidayActivity extends AppCompatActivity {

    private Holiday impHoliday;

    private TextView viewHolidayNotes, viewHolidayStartingLoc, viewHolidayDestination, viewHolidayCompanions, viewHolidayName;

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

        viewHolidayNotes.setText(impHoliday.getNotes());
        viewHolidayStartingLoc.setText(impHoliday.getStartingLoc());
        viewHolidayDestination.setText(impHoliday.getDestination());
        viewHolidayCompanions.setText(impHoliday.getTravellers());
        viewHolidayName.setText(impHoliday.getName());

        setResult(2, obtainIntent);

    }
}
