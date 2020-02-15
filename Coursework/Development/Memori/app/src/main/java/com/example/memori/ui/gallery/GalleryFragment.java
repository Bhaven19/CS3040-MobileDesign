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
import com.example.memori.ui.holidays.HolidayViewModel;

import java.util.ArrayList;

public class GalleryFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private GalleryViewModel galleryViewModel;
    private GalleryImageListAdapter galleryImageListAdapter;
    private Toolbar gToolbar;

    private int SORT_OPTIONS_DATE_OLD_TO_NEW = 0;
    private int SORT_OPTIONS_DATE_NEW_TO_OLD = 1;
    private int SORT_OPTIONS_LOCATION_A_TO_Z = 2;
    private int SORT_OPTIONS_LOCATION_Z_TO_A = 3;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createToolbar(view);

        setUpRecyclerView();
    }


    public void setUpRecyclerView() {
        // set up the RecyclerView
        RecyclerView recyclerView = getView().findViewById(R.id.gallery_recyclerview);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        galleryImageListAdapter = new GalleryImageListAdapter(getContext());

        galleryImageListAdapter.setClickListener(new GalleryImageListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(), "You clicked number " + galleryImageListAdapter.getItem(position) + ", which is at cell position " + position, Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(galleryImageListAdapter);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        galleryViewModel.getAllHolidays().observe(this, holidays -> {
            // Update the cached copy of the words in the adapter.
            galleryImageListAdapter.setHolidays(holidays);

        });


    }

    public void createToolbar(View view){
        setHasOptionsMenu(true);
        gToolbar = view.findViewById(R.id.toolbar_holidays);

        gToolbar.getMenu().getItem(0).setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            displayToast("Sort Selected");

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }
}