package com.example.memori.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.entities.Holiday;
import com.example.memori.database.entities.Images;
import com.example.memori.database.entities.VisitedPlace;
import com.example.memori.database.listadapters.GalleryImageListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GalleryFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private GalleryViewModel galleryViewModel;
    private GalleryImageListAdapter galleryImageListAdapter;
    private Toolbar gToolbar;

    private static final int SORT_OPTIONS_NAME_A_TO_Z = 1;
    private static final int SORT_OPTIONS_NAME_Z_TO_A = 2;
    private static final int SORT_OPTIONS_LOCATION_A_TO_Z = 3;
    private static final int SORT_OPTIONS_LOCATION_Z_TO_A = 4;
    private static final int SORT_OPTIONS_DATE_OLD_TO_NEW = 5;
    private static final int SORT_OPTIONS_DATE_NEW_TO_OLD = 6;
    private static final int SORT_OPTIONS_TAG_A_TO_Z = 7;
    private static final int SORT_OPTIONS_TAG_Z_TO_A = 8;

    private List<Images> mAllImages;
    private List<Holiday> mAllHolidays;
    private List<VisitedPlace> mAllVPlaces;

    private HashMap<String, Integer> hmNameToID;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createToolbar(view);

        setUpRecyclerView(-1);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_name) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
            pictureDialog.setTitle("Select the order for sorting the Images Name...");
            String[] pictureDialogItems = {
                    "Ascending",
                    "Descending"};
            pictureDialog.setItems(pictureDialogItems,
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Log.d("SortingGallery", "Sort By Name ASC");

                                setUpRecyclerView(SORT_OPTIONS_NAME_A_TO_Z);

                                break;
                            case 1:
                                Log.d("SortingGallery", "Sort By Name DESC");

                                setUpRecyclerView(SORT_OPTIONS_NAME_Z_TO_A);
                                break;
                        }
                    });
            pictureDialog.show();

            return true;

        } else if (id == R.id.action_sort_date) {
            displayToast("Sort Date Selected");

            return true;

        } else if (id == R.id.action_sort_location) {
            displayToast("Sort Location Selected");

            return true;

        } else if (id == R.id.action_sort_tag) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
            pictureDialog.setTitle("Select the order for sorting the Images Tag...");
            String[] pictureDialogItems = {
                    "Ascending",
                    "Descending"};
            pictureDialog.setItems(pictureDialogItems,
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Log.d("SortingGallery", "Sort By Tag ASC");

                                setUpRecyclerView(SORT_OPTIONS_TAG_A_TO_Z);

                                break;
                            case 1:
                                Log.d("SortingGallery", "Sort By Tag DESC");

                                setUpRecyclerView(SORT_OPTIONS_TAG_Z_TO_A);
                                break;
                        }
                    });
            pictureDialog.show();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        return root;
    }

    private void setUpRecyclerView(int sortOption) {
        // set up the RecyclerView
        RecyclerView recyclerView = getView().findViewById(R.id.gallery_recyclerview);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        galleryImageListAdapter = new GalleryImageListAdapter(getContext());

        Log.d("GalleryClick", "GalleryFragment, setting up recycler view");

        galleryImageListAdapter.setClickListener(new GalleryImageListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("GalleryClick", "GalleryFragment, onItemClick");

                displayToast("You clicked an image, which is at cell position " + position);
            }
        });

        recyclerView.setAdapter(galleryImageListAdapter);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        galleryViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            // Update the cached copy of the words in the adapter.
            Log.d("SortingGallery", "GalleryFragment: Updating Holidays");

            mAllImages = images;

            images = sortBy(sortOption, images);

            galleryImageListAdapter.setImages(images);

        });

    }

    private void createToolbar(View view){
        setHasOptionsMenu(true);
        gToolbar = view.findViewById(R.id.toolbar_holidays);

        gToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);
        gToolbar.getMenu().getItem(1).setOnMenuItemClickListener(this);
        gToolbar.getMenu().getItem(2).setOnMenuItemClickListener(this);
        gToolbar.getMenu().getItem(3).setOnMenuItemClickListener(this);

    }

    private List<Images> sortBy(int option, List<Images> mImages){
        Log.d("SortingGallery", "GalleryFragment: Sorting");

        ArrayList<Images> images = new ArrayList<>();
        ArrayList<String> imageSortField = new ArrayList<>();

        obtainAll();

        switch (option) {
            case SORT_OPTIONS_NAME_A_TO_Z:
                Log.d("SortingGallery", "GalleryFragment: Sorting Name ASC");

                images.clear();
                imageSortField.clear();

                accessAllImagesNames(mImages);

                for (HashMap.Entry<String, Integer> currentPair : hmNameToID.entrySet()){
                    imageSortField.add(currentPair.getKey());

                    Log.d("OrderGallery", "currentPair.getKey(): " + currentPair.getKey());
                }

                Collections.sort(imageSortField);
                Log.d("OrderGallery", "------SORTED-------");

                for (String currentImageName : imageSortField){
                    Log.d("OrderGallery", "currentImageName: " + currentImageName);

                    Images currentImage = getImage(hmNameToID.get(currentImageName));

                    images.add(currentImage);

                }

                return images;

            case SORT_OPTIONS_NAME_Z_TO_A:
                Log.d("SortingGallery", "GalleryFragment: Sorting Name DESC");

                images.clear();
                imageSortField.clear();

                accessAllImagesNames(mImages);

                for (HashMap.Entry<String, Integer> currentPair : hmNameToID.entrySet()){
                    imageSortField.add(currentPair.getKey());

                    Log.d("OrderGallery", "currentPair.getKey(): " + currentPair.getKey());
                }

                Collections.sort(imageSortField);
                Collections.reverse(imageSortField);
                Log.d("OrderGallery", "------SORTED-------");

                for (String currentImageName : imageSortField){
                    Log.d("OrderGallery", "currentImageName: " + currentImageName);

                    Images currentImage = getImage(hmNameToID.get(currentImageName));

                    images.add(currentImage);

                }

                return images;
//            case SORT_OPTIONS_LOCATION_A_TO_Z:
//                Log.d("SortingGallery", "GalleryFragment: Sorting Location ASC");
//
//                holiday.clear();
//                hSortField.clear();
//
//                for (Images currentImage : mImages){
//                    hSortField.add(currentImage.getName());
//                }
//
//                Collections.reverse(hSortField);
//
//                for (String currentHolidayName : hSortField){
//                    for (Images currentImage : mImages){
//                        if (currentHolidayName == currentImage.getName()){
//                            holiday.add(currentImage);
//
//                        }
//
//                    }
//                }
//
//                return holiday;
//            case SORT_OPTIONS_LOCATION_Z_TO_A:
//                ArrayList<String> hNames = new ArrayList<>();
//
//                for (Images currentImage : mImages){
//                    hNames.add(currentImage.getName());
//                }
//
//                Collections.sort(hNames);
//
//                for (String currentHolidayName : hNames){
//                    for (Images currentImage : mImages){
//                        if (currentHolidayName == currentImage.getName()){
//                            holiday.add(currentImage);
//
//                        }
//
//                    }
//                }
//
//
//                return holiday;
//            case SORT_OPTIONS_NAME_A_TO_Z:
//                ArrayList<String> hNames = new ArrayList<>();
//
//                for (Images currentImage : mImages){
//                    hNames.add(currentImage.getName());
//                }
//
//                Collections.sort(hNames);
//
//                for (String currentHolidayName : hNames){
//                    for (Images currentImage : mImages){
//                        if (currentHolidayName == currentImage.getName()){
//                            holiday.add(currentImage);
//
//                        }
//
//                    }
//                }
//
//
//                return holiday;
//            case SORT_OPTIONS_NAME_Z_TO_A:
//                ArrayList<String> hNames = new ArrayList<>();
//
//                for (Images currentImage : mImages){
//                    hNames.add(currentImage.getName());
//                }
//
//                Collections.sort(hNames);
//
//                for (String currentHolidayName : hNames){
//                    for (Images currentImage : mImages){
//                        if (currentHolidayName == currentImage.getName()){
//                            holiday.add(currentImage);
//
//                        }
//
//                    }
//                }
//
//
//                return holiday;
            case SORT_OPTIONS_TAG_A_TO_Z:
                ArrayList<String> hAZTagsNames = new ArrayList<>();

                for (Images currentImage : mImages){
                    hAZTagsNames.add(currentImage.getTag());

                    Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());
                }

                Collections.sort(hAZTagsNames);
                Log.d("OrderGallery", "----------SORTED----------");

                for (String currentHolidayTag : hAZTagsNames) {
                    for (Images currentImage : mImages) {
                        if (currentHolidayTag == currentImage.getTag()) {
                            images.add(currentImage);

                            Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());

                        }

                    }
                }

                return images;

            case SORT_OPTIONS_TAG_Z_TO_A:
                ArrayList<String> hZATagsNames = new ArrayList<>();

                for (Images currentImage : mImages){
                    hZATagsNames.add(currentImage.getTag());

                    Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());
                }

                Collections.sort(hZATagsNames);
                Collections.reverse(hZATagsNames);
                Log.d("OrderGallery", "----------SORTED----------");

                for (String currentHolidayTag : hZATagsNames) {
                    for (Images currentImage : mImages) {
                        if (currentHolidayTag == currentImage.getTag()) {
                            images.add(currentImage);

                            Log.d("OrderGallery", "currentImage.getTag(): " + currentImage.getTag());

                        }

                    }
                }

                return images;
        }

        return mImages;

    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void accessAllImagesNames(List<Images> mAllImages){
        hmNameToID = new HashMap<>();

        Log.d("OrderGallery", "mAllHolidays.size(): " + mAllHolidays.size());
        Log.d("OrderGallery", "mAllVPlaces.size(): " + mAllVPlaces.size());

        for (Holiday currentHoliday : mAllHolidays){
            hmNameToID.put(currentHoliday.getName(), currentHoliday.getImageID());
        }

        for (VisitedPlace currentVPlace : mAllVPlaces){
            hmNameToID.put(currentVPlace.getName(), currentVPlace.getImageID());
        }

    }

    private Images getImage(int imageID){
        Images returnImg = null;

        for (Images currentImage : mAllImages) {
            if (imageID == currentImage.get_id()) {
                returnImg = currentImage;

            }
        }

        return returnImg;
    }

    private void obtainAll(){
        galleryViewModel.getAllHolidays().observe(getViewLifecycleOwner(), holidays -> {
            mAllHolidays = holidays;

        });

        galleryViewModel.getAllVisitedPlaces().observe(getViewLifecycleOwner(), vplaces -> {
            mAllVPlaces = vplaces;

        });
    }

}