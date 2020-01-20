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

    @ColumnInfo(name = "TRAVELLERS")
    private String travellers;

    @ColumnInfo(name = "NOTES")
    private String notes;

    public Holiday(@NonNull String name, String travellers, String notes) {
        this.name = name;
        this.travellers = travellers;
        this.notes = notes;
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
}
