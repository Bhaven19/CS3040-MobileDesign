package uk.aston.navigation;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;


public class ChooseRecipientFragment extends Fragment implements View.OnClickListener {
    //private NavController navController;
    private TextInputEditText inputRecipient;


    public ChooseRecipientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_recipient, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputRecipient = view.findViewById(R.id.input_recipient);
        //navController = Navigation.findNavController(view);
        view.findViewById(R.id.next_btn).setOnClickListener(this);
        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            if (view.getId() == R.id.next_btn) {
                if (!inputRecipient.getText().toString().isEmpty()) {
                    String recipient = inputRecipient.getText().toString();
                    ChooseRecipientFragmentDirections.ActionChooseRecipientFragmentToSpecifyAmountFragment action = ChooseRecipientFragmentDirections.actionChooseRecipientFragmentToSpecifyAmountFragment().setRecipient(recipient);
                    Navigation.findNavController(view).navigate(action);
                    //Bundle bundle = new Bundle();
                    //bundle.putString("recipient", inputRecipient.getText().toString());
                    //navController.navigate(R.id.action_chooseRecipientFragment_to_specifyAmountFragment, bundle);


                } else {
                    Toast.makeText(getActivity(), "Please enter a recipient.", Toast.LENGTH_SHORT).show();
                }
            } else if (view.getId() == R.id.cancel_btn) {
                getActivity().onBackPressed();
            }
        }
    }
}
