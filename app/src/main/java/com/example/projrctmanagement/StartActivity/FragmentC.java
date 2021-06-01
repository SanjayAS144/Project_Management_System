package com.example.projrctmanagement.StartActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projrctmanagement.MainActivity;
import com.example.projrctmanagement.R;
import com.google.android.material.button.MaterialButton;

public class FragmentC extends Fragment {


    OnBoardingActivity activity = new OnBoardingActivity();

    MaterialButton gotologinbtn;

    public FragmentC() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_c, container, false);

        MaterialButton btn = view.findViewById(R.id.sharedPref);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainActivity = new Intent(view.getContext(), LoginActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                ((OnBoardingActivity)getActivity()).savePrefsData();
                getActivity().finish();

            }
        });




        return view;
    }


}