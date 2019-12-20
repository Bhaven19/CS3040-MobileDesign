package uk.aston.navigation;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class HomeFragment extends Fragment
        implements View.OnClickListener {
    private NavController navController;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.view_transactions_btn).setOnClickListener(this);
        view.findViewById(R.id.send_money_btn).setOnClickListener(this);
        view.findViewById(R.id.view_balance_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null && navController != null) {
            if (view.getId() == R.id.view_transactions_btn) {
                navController.navigate(R.id.action_homeFragment2_to_viewTransactionsFragment2);
            } else if (view.getId() == R.id.send_money_btn) {
                navController.navigate(R.id.action_homeFragment2_to_chooseRecipientFragment);
            } else if (view.getId() == R.id.view_balance_btn) {
                navController.navigate(R.id.action_homeFragment2_to_viewBalanceFragment);
            }
        }
    }
}
