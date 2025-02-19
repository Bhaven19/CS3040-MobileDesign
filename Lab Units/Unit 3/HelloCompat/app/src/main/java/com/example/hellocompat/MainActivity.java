package com.example.hellocompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView mHelloTextView;

    private String[] mColorArray = {"red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal", "green",
            "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown", "grey", "blue_grey", "black" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelloTextView = findViewById(R.id.hello_textview);

        if (savedInstanceState != null){
            mHelloTextView.setTextColor(savedInstanceState.getInt("color"));

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putInt("color", mHelloTextView.getCurrentTextColor());

    }

    public void changeColor(View view) {
        Random rnd = new Random();

        String colorName = mColorArray[rnd.nextInt(20)];

        int colourResourceName = getResources().getIdentifier(colorName,"color", getApplicationContext().getPackageName());

        int colorRes = ContextCompat.getColor(this, colourResourceName);

        mHelloTextView.setTextColor(colorRes);


    }
}
