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

    @ColumnInfo(name = "PATH")
    private String path;

    @ColumnInfo(name = "TAG")
    private String tag;

    @ColumnInfo(name = "DATE")
    private String date;

    public Images(String path, String tag, String date) {
        this.path = path;
        this.tag = tag;
        this.date = date;
    }

    public void set_id(int mID){
        _id = mID;
    }

    public int get_id(){
        return _id;
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

}
