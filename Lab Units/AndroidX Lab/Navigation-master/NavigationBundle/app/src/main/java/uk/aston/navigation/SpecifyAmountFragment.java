package uk.aston.navigation;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;


public class SpecifyAmountFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private String recipient;
    private TextInputEditText inputAmount;

    public SpecifyAmountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipient = getArguments().getString("recipient");
        Log.i("AJB", "Got recipient " + recipient);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specify_amount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.send_btn).setOnClickListener(this);
        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        TextView messageView = view.findViewById(R.id.recipient);
        messageView.setText("Sending money to " + recipient);
        inputAmount = view.findViewById(R.id.input_amount);
    }


    @Override
    public void onClick(View view) {
        if (view != null && navController != null) {
            if (view.getId() == R.id.send_btn) {
                if (!inputAmount.getText().toString().isEmpty()) {
                    Bundle bundle = new Bundle();
                    Money amount = new Money(new BigDecimal(inputAmount.getText().toString()));
                    bundle.putSerializable("amount", amount);
                    bundle.putString("recipient", recipient);
                    navController.navigate(
                            R.id.action_specifyAmountFragment_to_confirmationFragment, bundle);
                }else {
                    Toast.makeText(getActivity(), "Please enter an amount.", Toast.LENGTH_SHORT).show();
                }
            } else if (view.getId() == R.id.cancel_btn) {
                getActivity().onBackPressed();
            }
        }
    }
}
