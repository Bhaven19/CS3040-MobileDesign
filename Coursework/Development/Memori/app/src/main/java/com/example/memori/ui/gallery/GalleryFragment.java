package com.example.memori.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.GalleryImageListAdapter;
import com.example.memori.database.HolidayListAdapter;
import com.example.memori.database.entities.Holiday;
import com.example.memori.ui.holidays.HolidayViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
            pictureDialog.setTitle("Sort Name By...");
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
            displayToast("Sort Tag Selected");

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

        galleryImageListAdapter.setClickListener(new GalleryImageListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                displayToast("You clicked an image, which is at cell position " + position);
            }
        });

        recyclerView.setAdapter(galleryImageListAdapter);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        galleryViewModel.getAllHolidays().observe(this, holidays -> {
            // Update the cached copy of the words in the adapter.

            Log.d("SortingGallery", "GalleryFragment: Updating Holidays");

            holidays = sortBy(sortOption, holidays);

            galleryImageListAdapter.setHolidays(holidays);

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

    private List<Holiday> sortBy(int option, List<Holiday> mHolidays){
        Log.d("SortingGallery", "GalleryFragment: Sorting");

        ArrayList<Holiday> holidays = new ArrayList<>();
        ArrayList<String> hSortField = new ArrayList<>();

        switch (option) {
            case SORT_OPTIONS_NAME_A_TO_Z:
                Log.d("SortingGallery", "GalleryFragment: Sorting Name ASC");

                holidays.clear();
                hSortField.clear();

                for (Holiday currentHoliday : mHolidays){
                    hSortField.add(currentHoliday.getName());
                }

                Collections.reverse(hSortField);

                for (String currentHolidayName : hSortField){
                    for (Holiday currentHoliday : mHolidays){
                        if (currentHolidayName == currentHoliday.getName()){
                            holidays.add(currentHoliday);

                        }

                    }
                }

                return holidays;

            case SORT_OPTIONS_NAME_Z_TO_A:
                Log.d("SortingGallery", "GalleryFragment: Sorting Name DESC");

                holidays.clear();
                hSortField.clear();

                for (Holiday currentHoliday : mHolidays){
                    hSortField.add(currentHoliday.getName());
                }

                Collections.sort(hSortField);

                for (String currentHolidayName : hSortField){
                    for (Holiday currentHoliday : mHolidays){
                        if (currentHolidayName == currentHoliday.getName()){
                            holidays.add(currentHoliday);

                        }

                    }
                }

                return holidays;
//            case SORT_OPTIONS_LOCATION_A_TO_Z:
//                Log.d("SortingGallery", "GalleryFragment: Sorting Location ASC");
//
//                holidays.clear();
//                hSortField.clear();
//
//                for (Holiday currentHoliday : mHolidays){
//                    hSortField.add(currentHoliday.getName());
//                }
//
//                Collections.reverse(hSortField);
//
//                for (String currentHolidayName : hSortField){
//                    for (Holiday currentHoliday : mHolidays){
//                        if (currentHolidayName == currentHoliday.getName()){
//                            holidays.add(currentHoliday);
//
//                        }
//
//                    }
//                }
//
//                return holidays;
//            case SORT_OPTIONS_LOCATION_Z_TO_A:
//                ArrayList<String> hNames = new ArrayList<>();
//
//                for (Holiday currentHoliday : mHolidays){
//                    hNames.add(currentHoliday.getName());
//                }
//
//                Collections.sort(hNames);
//
//                for (String currentHolidayName : hNames){
//                    for (Holiday currentHoliday : mHolidays){
//                        if (currentHolidayName == currentHoliday.getName()){
//                            holidays.add(currentHoliday);
//
//                        }
//
//                    }
//                }
//
//
//                return holidays;
//            case SORT_OPTIONS_NAME_A_TO_Z:
//                ArrayList<String> hNames = new ArrayList<>();
//
//                for (Holiday currentHoliday : mHolidays){
//                    hNames.add(currentHoliday.getName());
//                }
//
//                Collections.sort(hNames);
//
//                for (String currentHolidayName : hNames){
//                    for (Holiday currentHoliday : mHolidays){
//                        if (currentHolidayName == currentHoliday.getName()){
//                            holidays.add(currentHoliday);
//
//                        }
//
//                    }
//                }
//
//
//                return holidays;
//            case SORT_OPTIONS_NAME_Z_TO_A:
//                ArrayList<String> hNames = new ArrayList<>();
//
//                for (Holiday currentHoliday : mHolidays){
//                    hNames.add(currentHoliday.getName());
//                }
//
//                Collections.sort(hNames);
//
//                for (String currentHolidayName : hNames){
//                    for (Holiday currentHoliday : mHolidays){
//                        if (currentHolidayName == currentHoliday.getName()){
//                            holidays.add(currentHoliday);
//
//                        }
//
//                    }
//                }
//
//
//                return holidays;
        }

        return mHolidays;

    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

}