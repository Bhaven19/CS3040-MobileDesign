package com.example.memori.ui;

import android.util.Log;

import java.util.Calendar;

public class HolidayDate {

    private int year, month, date = 0;
    private final Calendar c = Calendar.getInstance();

    public HolidayDate(int date, int month, int year){
        this.year = year;
        this.month = month;
        this.date = date;

    }

    public String toString(){
        return date + "/" + month + "/" + year;
    }

    public Boolean validDate(){
        Log.d("DateObject", "HolidayDate: Calendar.YEAR: " + Calendar.getInstance().get(Calendar.YEAR));
        Log.d("DateObject", "HolidayDate: Calendar.MONTH: " + Calendar.getInstance().get(Calendar.MONTH));
        Log.d("DateObject", "HolidayDate: Calendar.DAY_OF_MONTH: " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        if (year > Calendar.getInstance().get(Calendar.YEAR)){
            return false;

        } else if (year == Calendar.getInstance().get(Calendar.YEAR) && month > Calendar.getInstance().get(Calendar.MONTH)){
            return false;

        } else if (year == Calendar.getInstance().get(Calendar.YEAR) && month == Calendar.getInstance().get(Calendar.MONTH) && date > Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            return false;

        } else {
            return true;
        }

    }

}
