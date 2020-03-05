package com.example.memori.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "images_table")
public class Images implements Serializable {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int _id;

    @ColumnInfo(name = "HOLIDAY_ID")
    private int holidayID;

    @ColumnInfo(name = "VPLACE_ID")
    private int vPlaceID;

    @ColumnInfo(name = "PATH")
    private String path;

    @ColumnInfo(name = "TAG")
    private String tag;

    @ColumnInfo(name = "DATE")
    private String date;

    @ColumnInfo(name = "TYPE")
    private String type;

    public Images(int holidayID, int vPlaceID, String path, String tag, String date, @NonNull String type) {
        this.holidayID = holidayID;
        this.vPlaceID = vPlaceID;
        this.path = path;
        this.tag = tag;
        this.date = date;
        this.type = type;
    }

    public void set_id(int mID){
        _id = mID;
    }

    public int get_id(){
        return _id;
    }

    public int getHolidayID() {
        return holidayID;
    }

    public void setHolidayID(int holidayID) {
        this.holidayID = holidayID;
    }

    public int getVPlaceID() {
        return vPlaceID;
    }

    public void setVPlaceID(int vPlaceID) {
        this.vPlaceID = vPlaceID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
