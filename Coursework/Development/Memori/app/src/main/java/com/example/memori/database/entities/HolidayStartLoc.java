package com.example.memori.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "holiday_table")
public class HolidayStartLoc {

    @NonNull
    @ColumnInfo(name = "start_loc")
    private String mStartLoc;

    public HolidayStartLoc(@NonNull String mHolidayName) {
        this.mStartLoc = mHolidayName;
    }

    public String getHolidayStartLoc(){
        return this.mStartLoc;
    }

    public void setHolidayStartLoc(@NonNull String mHolidayName){
        this.mStartLoc = mHolidayName;
    }
}
