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

    @ColumnInfo(name = "TRAVELLERS")
    private String travellers;

    @ColumnInfo(name = "NOTES")
    private String notes;

    @ColumnInfo(name = "IMAGE_PATH")
    private String imagePath;

    @ColumnInfo(name = "IMAGE_TAG")
    private String imageTag;

    public Holiday(@NonNull String name, String startingLoc, String destination, String travellers, String notes, String imagePath, String imageTag) {
        this.name = name;
        this.startingLoc = startingLoc;
        this.destination = destination;
        this.travellers = travellers;
        this.notes = notes;
        this.imagePath = imagePath;
        this.imageTag = imageTag;

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

    public String getImagePath(){ return imagePath; }

    public String getImageTag(){ return imageTag; }
}
