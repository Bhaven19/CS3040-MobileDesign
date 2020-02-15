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
import com.example.memori.database.entities.Holiday;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryImageListAdapter extends RecyclerView.Adapter<com.example.memori.database.GalleryImageListAdapter.ViewHolder> {

    private ArrayList<String> allImages;
    private List<Holiday> mAllHolidays;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int imageCount = 0;
    private int imageHeight, imageWidth, startX, startY, distanceX, distanceY;

    // data is passed into the constructor
    public GalleryImageListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        imageCount++;
        Log.d("GalleryImages", "GalleryImageListAdapter: imageCount: " + imageCount);

        if (mAllHolidays != null) {
            String pathName = allImages.get(position);

            if (pathName == null) {

            } else {
                File imageFile = new File(pathName);

                if (imageFile.exists()){
                    Bitmap mHolidayImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                    imageWidth = mHolidayImage.getWidth();
                    imageHeight = mHolidayImage.getHeight();
                    Log.d("GalleryImages", "GalleryImageListAdapter: imageX and imageY: " + imageWidth + ", " + imageHeight);

                    if (imageHeight > 500 && imageWidth > 500){
                        startX = (int) Math.round(mHolidayImage.getWidth() * 0.25);
                        startY = (int) Math.round(mHolidayImage.getHeight() * 0.25);
                        Log.d("GalleryImages", "GalleryImageListAdapter: X and Y: " + startX + ", " + startY);

                        distanceX = (int) Math.round(mHolidayImage.getWidth() * 0.75);
                        distanceY = (int) Math.round(mHolidayImage.getHeight() * 0.75);
                        Log.d("GalleryImages", "GalleryImageListAdapter: distanceX and distanceY: " + distanceX + ", " + distanceY);

                    } else if (imageWidth > 500){
                        startY = 0;
                        distanceY = imageHeight;

                        startX = (int) Math.round(mHolidayImage.getWidth() * 0.25);
                        Log.d("GalleryImages", "GalleryImageListAdapter: X and Y: " + startX + ", " + startY);

                        distanceX = (int) Math.round(mHolidayImage.getWidth() * 0.75);
                        Log.d("GalleryImages", "GalleryImageListAdapter: distanceX and distanceY: " + distanceX + ", " + distanceY);
                        Log.d("GalleryImages", "GalleryImageListAdapter: distanceX and distanceY: " + distanceX + ", " + distanceY);

                    } else if (imageHeight > 500){
                        startX = 0;
                        distanceX = imageWidth;

                        startY = (int) Math.round(mHolidayImage.getHeight() * 0.25);
                        Log.d("GalleryImages", "GalleryImageListAdapter: X and Y: " + startX + ", " + startY);

                        distanceY = (int) Math.round(mHolidayImage.getHeight() * 0.75);
                        Log.d("GalleryImages", "GalleryImageListAdapter: distanceX and distanceY: " + distanceX + ", " + distanceY);

                    } else {
                        startX = (int) Math.round(mHolidayImage.getWidth() * 0.15);
                        startY = (int) Math.round(mHolidayImage.getHeight() * 0.15);
                        Log.d("GalleryImages", "GalleryImageListAdapter: X and Y: " + startX + ", " + startY);

                        distanceX = (int) Math.round(mHolidayImage.getWidth() * 0.85);
                        distanceY = (int) Math.round(mHolidayImage.getHeight() * 0.85);
                        Log.d("GalleryImages", "GalleryImageListAdapter: distanceX and distanceY: " + distanceX + ", " + distanceY);
                    }

                    Bitmap croppedImage = Bitmap.createBitmap(mHolidayImage, startX, startY, distanceX, distanceY);

                    holder.myImageView.setImageBitmap(croppedImage);

                }

            }
        }
    }

    @Override
    public int getItemCount() {
        if (mAllHolidays != null)
            return mAllHolidays.size();
        else return 0;
    }

    public void setHolidays(List<Holiday> holidays){
        mAllHolidays = holidays;
        notifyDataSetChanged();
        listToArrayList(mAllHolidays);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView myImageView;

        private ViewHolder(View itemView) {
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

    public void listToArrayList(List<Holiday> mData){
        allImages = new ArrayList<>();

        for (Holiday currentHoliday : mData){
            allImages.add(currentHoliday.getImagePath());
        }

    }

    public void sortHolidays(int option){
        if (option == 1){

        }

    }
}
