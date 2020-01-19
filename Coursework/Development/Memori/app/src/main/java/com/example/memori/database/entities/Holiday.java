package com.example.memori.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.memori.database.Converters;

import java.util.Date;

@Entity(tableName = "holiday_table")
public class Holiday {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int _id;

    @NonNull
    @ColumnInfo(name = "NAME")
    private String name;

    @NonNull
    @ColumnInfo(name = "START_DATE")
    @TypeConverters({Converters.class})
    private Date startDate;

    public Holiday(@NonNull String name, @NonNull Date startDate) {
        this.name = name;
        this.startDate = startDate;
    }

    public void set_id(int mID){
        _id = mID;
    }

    public int get_id(){
        return _id;
    }

    @NonNull
    public Date getStartDate() {
        return startDate;
    }

    public String getName(){
        return name;
    }
}
