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

    @ColumnInfo(name = "IMAGE_ID")
    private int imageID;

    public VisitedPlace(@NonNull int holidayID, @NonNull String name, @NonNull String date, String location, String travellers, String notes, int imageID) {
        this.holidayID = holidayID;
        this.name = name;
        this.date = date;
        this.location = location;
        this.travellers = travellers;
        this.notes = notes;
        this.imageID = imageID;

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

    public void setDate(@NonNull String date) {
        this.date = date;
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

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
