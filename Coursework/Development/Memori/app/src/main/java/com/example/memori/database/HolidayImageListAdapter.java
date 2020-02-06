package com.example.memori.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.HolidayImage;

import java.util.List;

public class HolidayImageListAdapter extends RecyclerView.Adapter<HolidayImageListAdapter.HolidayViewHolder>{

    private final LayoutInflater mInflater;
    private List<HolidayImage> mHolidayImages;
    private Context mContext;

    //----------------------------------------------

    public HolidayImageListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public HolidayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);

        return new HolidayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HolidayViewHolder holder, int position) {
//        if (mHolidays != null) {
//            Holiday current = mHolidays.get(position);
//            holder.holidayName.setText(current.getName());
//            holder.holidayStartLoc.setText(current.getStartingLoc());
//            holder.holidayEndLoc.setText(current.getDestination());
//            //holder.holidayImage.setImageBitmap(null);
//        } else {
//            // Covers the case of data not being ready yet.
//            holder.holidayName.setText("Not Ready");
//            holder.holidayStartLoc.setText("Not Ready");
//            holder.holidayEndLoc.setText("Not Ready");
//            //holder.holidayImage.setImageBitmap(null);
//        }
    }

    @Override
    public int getItemCount() {
        if (mHolidayImages != null)
            return mHolidayImages.size();
        else return 0;
    }

    public void setWords(List<HolidayImage> holidayImages){
        mHolidayImages = holidayImages;
        notifyDataSetChanged();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mHolidayImage;

        private HolidayViewHolder(View itemView) {
            super(itemView);
            mHolidayImage = itemView.findViewById(R.id.holiday_image);
        }
    }

    public HolidayImage getWordAtPosition (int position) {
        return mHolidayImages.get(position);
    }

    //----------------------------------------------

    public void displayToast(String message){
        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

}
