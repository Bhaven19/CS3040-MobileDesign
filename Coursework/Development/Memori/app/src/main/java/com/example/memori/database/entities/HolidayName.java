package com.example.memori.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "holiday_table")
public class HolidayName {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String mHolidayName;

    public HolidayName(@NonNull String mHolidayName) {
        this.mHolidayName = mHolidayName;
    }

    public String getHolidayName(){
        return this.mHolidayName;
    }

    public void setHolidayName(@NonNull String mHolidayName){
        this.mHolidayName = mHolidayName;
    }
}
