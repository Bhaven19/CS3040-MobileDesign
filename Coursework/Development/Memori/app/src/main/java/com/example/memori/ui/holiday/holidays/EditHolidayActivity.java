package com.example.memori.ui.holiday.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;

public class EditHolidayActivity extends AppCompatActivity {
    private EditText mEditHolidayNameView, mStartingLoc, mDestination, mTravellersView, mTravelNotes;
    private Holiday mHoliday;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_holiday);

        mHoliday = (Holiday) getIntent().getSerializableExtra("chosenHoliday");

        mEditHolidayNameView = findViewById(R.id.edit_vPlaceName);
        mStartingLoc = findViewById(R.id.edit_vPlaceLoc);
        mDestination = findViewById(R.id.edit_EndLoc);
        mTravellersView = findViewById(R.id.edit_vPlaceCompanions);
        mTravelNotes = findViewById(R.id.edit_vPlaceNotes);

        final Button button = findViewById(R.id.btn_saveHoliday);

        setValues();

        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();

            if (TextUtils.isEmpty(mEditHolidayNameView.getText()) ||
                    TextUtils.isEmpty(mStartingLoc.getText()) ||
                    TextUtils.isEmpty(mDestination.getText()) ||
                    TextUtils.isEmpty(mTravellersView.getText()) ||
                    TextUtils.isEmpty(mTravelNotes.getText()) ) {

                setResult(0, replyIntent);

                Toast.makeText(getApplicationContext(), "Holiday was not updated, some fields were empty", Toast.LENGTH_LONG).show();

            } else {
                mHoliday.setName(mEditHolidayNameView.getText().toString());
                mHoliday.setStartingLoc(mStartingLoc.getText().toString());
                mHoliday.setDestination(mDestination.getText().toString());
                mHoliday.setTravellers(mTravellersView.getText().toString());
                mHoliday.setNotes(mTravelNotes.getText().toString());

                replyIntent.putExtra("editedHoliday", mHoliday);

                setResult(3, replyIntent);
            }
            finish();
        });
    }

    public void setValues(){
        mEditHolidayNameView.setText(mHoliday.getName());
        mStartingLoc.setText(mHoliday.getStartingLoc());
        mDestination.setText(mHoliday.getDestination());
        mTravellersView.setText(mHoliday.getTravellers());
        mTravelNotes.setText(mHoliday.getNotes());

    }
}
