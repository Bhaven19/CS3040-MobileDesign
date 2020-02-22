package com.example.memori.database;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;

import java.util.List;

public class HolidayListAdapter extends RecyclerView.Adapter<com.example.memori.database.HolidayListAdapter.HolidayViewHolder> {

    private final LayoutInflater mInflater;
    private List<Holiday> mHolidays;
    private Toolbar hToolbar;
    private Context mContext;
    private ItemClickListener mClickListener;

    //----------------------------------------------

    public HolidayListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public HolidayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_holidayitem, parent, false);

        return new HolidayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        if (mHolidays != null) {
            Holiday current = mHolidays.get(position);
            holder.holidayName.setText(current.getName());
            holder.holidayStartLoc.setText(current.getStartingLoc());
            holder.holidayEndLoc.setText(current.getDestination());
            holder.holidayStartDate.setText(current.getStartDate());
            holder.holidayEndDate.setText(current.getEndDate());
            //holder.holidayImage.setImageBitmap(null);
        } else {
            // Covers the case of data not being ready yet.
            holder.holidayName.setText("Not Ready");
            holder.holidayStartLoc.setText("Not Ready");
            holder.holidayEndLoc.setText("Not Ready");
            holder.holidayStartDate.setText("Not Ready");
            holder.holidayEndDate.setText("Not Ready");
            //holder.holidayImage.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        if (mHolidays != null)
            return mHolidays.size();
        else return 0;
    }

    public void setWords(List<Holiday> words){
        mHolidays = words;
        notifyDataSetChanged();
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // allows clicks events to be caught
    public void setClickListener(HolidayListAdapter.ItemClickListener itemClickListener) {
        Log.d("HolidayClick", "HolidayListAdapter, setClickListener");

        this.mClickListener = itemClickListener;
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView holidayName, holidayStartLoc, holidayEndLoc, holidayStartDate, holidayEndDate;
        //private final ImageView holidayImage;

        private HolidayViewHolder(View itemView) {
            super(itemView);
            holidayName = itemView.findViewById(R.id.textView_hName);
            holidayStartLoc = itemView.findViewById(R.id.textView_hStartLoc);
            holidayEndLoc = itemView.findViewById(R.id.textView_hEndLoc);
            holidayStartDate = itemView.findViewById(R.id.textView_hStartDate);
            holidayEndDate = itemView.findViewById(R.id.textView_hEndDate);

            //holidayImage = itemView.findViewById(R.id.imageView_hImage);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Log.d("HolidayClick", "HolidayListAdapter, onClick");

            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

    }

    public Holiday getWordAtPosition (int position) {
        return mHolidays.get(position);
    }

    public void displayToast(String message){
        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

}
