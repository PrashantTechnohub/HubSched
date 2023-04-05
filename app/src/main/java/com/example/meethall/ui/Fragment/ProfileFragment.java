package com.example.meethall.ui.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.meethall.R;
import com.example.meethall.UserChecker.checkUserDetailPreference;
import com.example.meethall.databinding.FragmentProfileBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bind = FragmentProfileBinding.inflate(inflater);
        MaterialToolbar toolbar = getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Account");


        bind.logOutBtn.setOnClickListener(v -> {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MaterialAlertDialog_Rounded);
            LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View team = inflater1.inflate(R.layout.alert_layout, null);
            builder.setView(team);
            builder.setCancelable(false);

            Button logout = team.findViewById(R.id.yes_btn);
            Button no = team.findViewById(R.id.no_btn);
            TextView text = team.findViewById(R.id.alert_text);

            text.setText("Are you sure want to logout !!");

            AlertDialog dialog = builder.create();
            dialog.show();

            logout.setOnClickListener(v1 -> checkUserDetailPreference.LogOutUser(getActivity(), "out", dialog));

            no.setOnClickListener(v12 -> dialog.cancel());

        });

        return bind.getRoot();
    }
}