package com.example.memori.ui.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateHolidayActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.roomwordssample.REPLY";

    private EditText mEditHolidayNameView, mStartingLoc, mDestination, mTravellersView, mTravelNotes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_holiday);

        mEditHolidayNameView = findViewById(R.id.edit_Name);
        mStartingLoc = findViewById(R.id.edit_StartLoc);
        mDestination = findViewById(R.id.edit_EndLoc);
        mEditHolidayNameView = findViewById(R.id.edit_Name);
        mTravellersView = findViewById(R.id.edit_Companions);
        mTravelNotes = findViewById(R.id.edit_Notes);

        final Button button = findViewById(R.id.btn_saveHoliday);

        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();

            if (TextUtils.isEmpty(mEditHolidayNameView.getText()) ||
                    TextUtils.isEmpty(mStartingLoc.getText()) ||
                    TextUtils.isEmpty(mDestination.getText()) ||
                    TextUtils.isEmpty(mTravellersView.getText()) ||
                    TextUtils.isEmpty(mTravelNotes.getText()) ) {

                setResult(0, replyIntent);

                Toast.makeText(getApplicationContext(), "Fields are empty, nothing was saved", Toast.LENGTH_LONG).show();

            } else {
                String hName = mEditHolidayNameView.getText().toString();
                String hStartingLoc = mStartingLoc.getText().toString();
                String hDestination = mDestination.getText().toString();
                String hCompanions = mTravellersView.getText().toString();
                String hNotes = mTravelNotes.getText().toString();

                replyIntent.putExtra("holidayName", hName);
                replyIntent.putExtra("holidayStartingLoc", hStartingLoc);
                replyIntent.putExtra("holidayDestination", hDestination);
                replyIntent.putExtra("holidayTravellers", hCompanions);
                replyIntent.putExtra("holidayNotes", hNotes);

                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}
