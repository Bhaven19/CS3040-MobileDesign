package com.example.androidxlab;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class chooseRecipientFragment extends Fragment implements View.OnClickListener{

    private NavController navController;

    public chooseRecipientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_recipient, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view != null && navController != null) {
            if (view.getId() == R.id.chooseRecipient_next_btn) {
                navController.navigate(R.id.action_chooseRecipientFragment_to_specifyAmountFragment);

            } else if (view.getId() == R.id.chooseRecipient_cancel_btn) {
                getActivity().onBackPressed();

            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        view.findViewById(R.id.chooseRecipient_next_btn).setOnClickListener(this);
        view.findViewById(R.id.chooseRecipient_cancel_btn).setOnClickListener(this);

    }
}
