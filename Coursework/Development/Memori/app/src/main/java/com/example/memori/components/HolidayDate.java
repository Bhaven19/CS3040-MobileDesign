package com.example.memori.components;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HolidayDate {

    private int year, month, date;

    public HolidayDate(int date, int month, int year){
        this.year = year;
        this.month = month;
        this.date = date;

    }

    public static HolidayDate toObject(String stringDate){
        int date;
        int month;
        int year;

        if ("/".equals(stringDate.charAt(1)) && "/".equals(stringDate.charAt(3)) ) {
            date = stringDate.charAt(0);
            month = stringDate.charAt(2);
            year = Integer.parseInt(stringDate.substring(4, 7));

        } else if ("/".equals(stringDate.charAt(1)) && "/".equals(stringDate.charAt(4)) ) {
            date = stringDate.charAt(0);
            month = Integer.parseInt(stringDate.substring(2, 3));
            year = Integer.parseInt(stringDate.substring(5, 8));

        } else if ("/".equals(stringDate.charAt(2)) && "/".equals(stringDate.charAt(4)) ) {
            date = Integer.parseInt(stringDate.substring(0, 1));
            month = stringDate.charAt(3);
            year = Integer.parseInt(stringDate.substring(5, 8));

        } else if ("/".equals(stringDate.charAt(2)) && "/".equals(stringDate.charAt(5)) ) {
            date = Integer.parseInt(stringDate.substring(0, 1));
            month = Integer.parseInt(stringDate.substring(3, 4));
            year = Integer.parseInt(stringDate.substring(5, 8));

        } else {
            return null;
        }

        return new HolidayDate(date, month, year);

    }

    public int getYear(){ return year;}

    public int getMonth(){ return month;}

    public int getDate(){ return date;}

    public String toString(){
        Log.d("DatePadding", "old date: " + date);
        Log.d("DatePadding", "old month: " + month);

        String dateString = String.valueOf(date);
        if (String.valueOf(date).length() == 1){
            dateString = String.format("%02d", date);

        }

        String monthString = String.valueOf(month);
        if (String.valueOf(month).length() == 1){
            monthString = String.format("%02d", month);
        }

        Log.d("DatePadding", "new date: " + dateString);
        Log.d("DatePadding", "new month: " + monthString);

        return dateString + "/" + monthString + "/" + year;
    }

    public Boolean validDate(HolidayDate startDate){
        Log.d("DateObject", "HolidayDate: Calendar.YEAR: " + Calendar.getInstance().get(Calendar.YEAR));
        Log.d("DateObject", "HolidayDate: Calendar.MONTH: " + Calendar.getInstance().get(Calendar.MONTH));
        Log.d("DateObject", "HolidayDate: Calendar.DAY_OF_MONTH: " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        if (startDate.getYear() > year){
            return false;

        } else if (startDate.getYear() == year && startDate.getMonth() > month){
            return false;

        } else if (startDate.getYear() == year && startDate.getMonth() == month && startDate.getDate() > date){
            return false;

        } else {
            return true;
        }

    }

    public static String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }


}
