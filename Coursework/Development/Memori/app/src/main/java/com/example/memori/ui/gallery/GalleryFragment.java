package com.example.memori.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memori.R;
import com.example.memori.database.GalleryImageListAdapter;

import java.util.ArrayList;

public class GalleryFragment extends Fragment implements GalleryImageListAdapter.ItemClickListener {

    private GalleryViewModel galleryViewModel;
    private GalleryImageListAdapter galleryImageListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createToolbar(view);
    }


    public void createToolbar(View view) {
        ArrayList<String> data = galleryViewModel.getAllHolidayImagePaths();

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_holidayimage);
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), numberOfColumns));
        galleryImageListAdapter = new GalleryImageListAdapter(this.getContext(), data);
        galleryImageListAdapter.setClickListener(this);
        recyclerView.setAdapter(galleryImageListAdapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("TAG", "You clicked number " + galleryImageListAdapter.getItem(position) + ", which is at cell position " + position);
    }
}