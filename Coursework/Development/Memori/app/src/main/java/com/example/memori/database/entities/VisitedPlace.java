package com.example.memori.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "visited_places_table")
public class VisitedPlace implements Serializable {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int _id;

    @NonNull
    @ColumnInfo(name = "HOLIDAY_ID")
    private int holidayID;

    @NonNull
    @ColumnInfo(name = "NAME")
    private String name;

    @NonNull
    @ColumnInfo(name = "DATE")
    private String date;

    @ColumnInfo(name = "LOCATION")
    private String location;

    @ColumnInfo(name = "TRAVELLERS")
    private String travellers;

    @ColumnInfo(name = "NOTES")
    private String notes;

    @ColumnInfo(name = "IMAGE_PATH")
    private String imagePath;

    @ColumnInfo(name = "IMAGE_DATE")
    private String imageDate;

    @ColumnInfo(name = "IMAGE_TAG")
    private String imageTag;

    public VisitedPlace(@NonNull int holidayID, @NonNull String name, @NonNull String date, String location, String travellers, String notes, String imagePath, String imageDate, String imageTag) {
        this.holidayID = holidayID;
        this.name = name;
        this.date = date;
        this.location = location;
        this.travellers = travellers;
        this.notes = notes;
        this.imagePath = imagePath;
        this.imageDate = imageDate;
        this.imageTag = imageTag;

    }

    public void set_id(int mID){
        _id = mID;
    }

    public int get_id(){
        return _id;
    }

    public int getHolidayID(){ return holidayID; }

    public String getName(){ return name; }

    public String getDate(){ return date; }

    public String getLocation(){ return location; }

    public String getTravellers() { return travellers; }

    public String getNotes(){ return notes; }

    public String getImagePath(){ return imagePath; }

    public String getImageDate(){ return imageDate; }

    public String getImageTag(){ return imageTag; }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public void setImageDate(String imageDate) {
        this.imageDate = imageDate;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setHolidayID(int holidayID) {
        this.holidayID = holidayID;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTravellers(String travellers) {
        this.travellers = travellers;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }
}
