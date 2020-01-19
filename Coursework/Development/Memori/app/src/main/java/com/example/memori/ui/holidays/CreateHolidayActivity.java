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

    private EditText mEditHolidayNameView;
    private EditText mEditStartDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_holiday);

        mEditHolidayNameView = findViewById(R.id.edit_Name);
        mEditStartDate = findViewById(R.id.edit_StartDate);

        final Button button = findViewById(R.id.btn_saveHoliday);

        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();

            if (TextUtils.isEmpty(mEditHolidayNameView.getText()) || TextUtils.isEmpty(mEditStartDate.getText())) {
                setResult(RESULT_CANCELED, replyIntent);

                Toast.makeText(getApplicationContext(), "Fields are empty, nothing was saved", Toast.LENGTH_LONG).show();

            } else {
                String hName = mEditHolidayNameView.getText().toString();

                replyIntent.putExtra(EXTRA_REPLY, hName);

                try {
                    Date hStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(mEditStartDate.getText().toString());

                    replyIntent.putExtra(EXTRA_REPLY, hStartDate);

                    Toast.makeText(getApplicationContext(), "Name: " + hName + " ... Date: " + hStartDate, Toast.LENGTH_LONG).show();

                } catch(java.text.ParseException e) {
                    e.printStackTrace();
                }

                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}
