package com.example.memori.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryImageListAdapter extends RecyclerView.Adapter<com.example.memori.database.GalleryImageListAdapter.ViewHolder> {

    private ArrayList<String> allImages;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public GalleryImageListAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        allImages = listToArrayList(data);
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("GalleryImages", "GalleryImageListAdapter: Image path: " + allImages.get(position));
        String pathName = allImages.get(position);

        if (pathName == null) {
            Log.d("GalleryImages", "ViewHolidayActivity, NO IMAGE SAVED");

        } else {
            File imageFile = new File(pathName);

            if (imageFile.exists()){
                Bitmap mHolidayImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                holder.myImageView.setImageBitmap(mHolidayImage);

                Log.d("GalleryImages", "ViewHolidayActivity, IMAGE FOUND");

            } else {
                Log.d("GalleryImages", "ViewHolidayActivity, IMAGE NOT FOUND");

            }
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return allImages.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.recyclerview_holidayimage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return allImages.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public ArrayList listToArrayList(List<String> mData){
        ArrayList<String> holidayImages = new ArrayList<>();

        for (String currentImage : mData){
            holidayImages.add(currentImage);
        }

        return holidayImages;

    }

}
