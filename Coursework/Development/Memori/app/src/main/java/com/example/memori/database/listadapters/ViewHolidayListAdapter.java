package com.example.memori.database.listadapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.entities.VisitedPlace;

import java.util.List;

public class ViewHolidayListAdapter extends RecyclerView.Adapter<ViewHolidayListAdapter.ViewHolidayViewHolder> {

    private final LayoutInflater mInflater;
    private List<VisitedPlace> mVPlaces;
    private Context mContext;
    private ItemClickListener mClickListener;

    //----------------------------------------------

    public ViewHolidayListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public ViewHolidayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_vplaceitem, parent, false);

        return new ViewHolidayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolidayViewHolder holder, int position) {
        if (mVPlaces != null) {
            VisitedPlace current = mVPlaces.get(position);
            holder.vPlaceName.setText(current.getName());
            holder.vPlaceDate.setText(current.getDate());

        } else {
            // Covers the case of data not being ready yet.
            holder.vPlaceName.setText("Not Ready");
            holder.vPlaceDate.setText("Not Ready");


        }
    }

    @Override
    public int getItemCount() {
        if (mVPlaces != null)
            return mVPlaces.size();
        else return 0;
    }

    public void setVPlaces(List<VisitedPlace> vPlaces){
        mVPlaces = vPlaces;
        notifyDataSetChanged();
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // allows clicks events to be caught
    public void setClickListener(ViewHolidayListAdapter.ItemClickListener itemClickListener) {
        Log.d("HolidayClick", "VPlaceListAdapter, setClickListener");

        this.mClickListener = itemClickListener;
    }

    class ViewHolidayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView vPlaceName, vPlaceDate;
        //private final ImageView holidayImage;

        private ViewHolidayViewHolder(View itemView) {
            super(itemView);
            vPlaceName = itemView.findViewById(R.id.textView_vPlaceName);
            vPlaceDate = itemView.findViewById(R.id.textView_vPlaceDate);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Log.d("HolidayClick", "VPlaceListAdapter, onClick");

            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

    }

    public VisitedPlace getWordAtPosition (int position) {
        return mVPlaces.get(position);
    }


}
