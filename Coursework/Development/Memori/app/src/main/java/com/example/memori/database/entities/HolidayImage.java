package com.example.memori.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "image_table")
public class HolidayImage implements Serializable {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int _id;

    @NonNull
    @ColumnInfo(name = "HOLIDAYID")
    private int holidayID;

    @NonNull
    @ColumnInfo(name = "IMAGEPATH")
    private String imagePath;


    public HolidayImage(@NonNull int holidayID, String imagePath) {
        this.holidayID = holidayID;
        this.imagePath = imagePath;
    }

    public void set_id(int mID){
        _id = mID;
    }

    public int get_id(){
        return _id;
    }

    public int getHolidayID(){return holidayID;}

    public String getImagePath() {
        return imagePath;
    }
}
