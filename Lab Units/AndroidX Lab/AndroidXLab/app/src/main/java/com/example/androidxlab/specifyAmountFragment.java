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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;


/**
 * A simple {@link Fragment} subclass.
 */
public class specifyAmountFragment extends Fragment implements View.OnClickListener{

    private NavController navController;

    private String recipient;
    private TextInputEditText inputAmount;

    public specifyAmountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedState){
        super.onCreate(savedState);

        recipient = getArguments().getString("recipient");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specify_amount, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view != null && navController != null) {
            if (view.getId() == R.id.specifyAmount_send_btn) {
                if (!inputAmount.getText().toString().isEmpty()) {
                    Bundle bundle = new Bundle();
                    Money amount = new Money(new BigDecimal(inputAmount.getText().toString()));
                    bundle.putSerializable("amount", amount);
                    bundle.putString("recipient", recipient);

                    navController.navigate(R.id.action_specifyAmountFragment_to_confirmationFragment, bundle);

                }else {
                    Toast.makeText(getActivity(), "Please enter an amount.", Toast.LENGTH_SHORT).show();
                }

            } else if (view.getId() == R.id.specifyAmount_cancel_btn) {
                getActivity().onBackPressed();

            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        view.findViewById(R.id.specifyAmount_send_btn).setOnClickListener(this);
        view.findViewById(R.id.specifyAmount_cancel_btn).setOnClickListener(this);

    }


}
