package com.example.memori.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "holiday_table")
public class Holiday implements Serializable {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int _id;

    @NonNull
    @ColumnInfo(name = "NAME")
    private String name;

    @ColumnInfo(name = "STARTING_LOC")
    private String startingLoc;

    @ColumnInfo(name = "DESTINATION")
    private String destination;

    @ColumnInfo(name = "START_DATE")
    private String startDate;

    @ColumnInfo(name = "END_DATE")
    private String endDate;

    @ColumnInfo(name = "TRAVELLERS")
    private String travellers;

    @ColumnInfo(name = "NOTES")
    private String notes;

    @ColumnInfo(name = "IMAGE_ID")
    private int imageID;

    public Holiday(@NonNull String name, String startingLoc, String destination, String startDate, String endDate, String travellers, String notes, int imageID) {
        this.name = name;
        this.startingLoc = startingLoc;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getNotes(){return notes;}

    public String getTravellers() {
        return travellers;
    }

    public String getName(){
        return name;
    }

    public String getStartingLoc(){
        return startingLoc;
    }

    public String getDestination(){
        return destination;
    }

    public String getStartDate(){ return startDate; }

    public String getEndDate(){ return  endDate; }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setStartingLoc(String startingLoc) {
        this.startingLoc = startingLoc;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setTravellers(String travellers) {
        this.travellers = travellers;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
