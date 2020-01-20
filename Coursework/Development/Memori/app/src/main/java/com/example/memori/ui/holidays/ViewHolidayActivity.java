package com.example.memori.ui.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;

public class ViewHolidayActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.roomwordssample.REPLY";

    private Holiday impHoliday;

    private TextView viewHolidayNotes;
    private TextView viewHolidayCompanions;
    private TextView viewHolidayName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_holiday);

        Intent obtainIntent = getIntent();
        impHoliday = (Holiday) obtainIntent.getSerializableExtra("chosenHoliday");

        viewHolidayNotes = findViewById(R.id.text_holidayNotes);
        viewHolidayCompanions = findViewById(R.id.text_holidayCompanions);
        viewHolidayName = findViewById(R.id.text_HolidayName);

        viewHolidayNotes.setText(impHoliday.getNotes());
        viewHolidayCompanions.setText(impHoliday.getTravellers());
        viewHolidayName.setText(impHoliday.getName());


    }
}
