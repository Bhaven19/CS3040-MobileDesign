package com.example.memori.database.listadapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.entities.Images;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryImageListAdapter extends RecyclerView.Adapter<GalleryImageListAdapter.ViewHolder> {

    private ArrayList<String> allImages;
    private List<Images> mAllImages;
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

        if (mAllImages != null) {
            String pathName = allImages.get(position);

            if (pathName != null) {
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
        if (mAllImages != null)
            return mAllImages.size();
        else return 0;
    }

    public void setImages(List<Images> images){
        mAllImages = images;
        notifyDataSetChanged();
        listToArrayList(mAllImages);


    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        Log.d("GalleryClick", "GalleryImageListAdapter, setClickListener()");

        this.mClickListener = itemClickListener;
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
            Log.d("GalleryClick", "GalleryImageListAdapter, onClick()");
            if (mClickListener != null) {
                Log.d("GalleryClick", "GalleryImageListAdapter, mClickListener is true");
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return allImages.get(id);
    }

    public void listToArrayList(List<Images> mData){
        allImages = new ArrayList<>();

        for (Images currentImage : mData) {
            allImages.add(currentImage.getPath());
        }

    }



}
