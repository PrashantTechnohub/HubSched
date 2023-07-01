package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentHomeBinding;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentSupportBinding;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class SupportFragment extends Fragment {


    FragmentSupportBinding bind;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentSupportBinding.inflate(inflater, container, false);
        View rootView = bind.getRoot();

        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Support");


        bind.havingIssueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.cl_bug_report, null);
                builder.setView(team);
                builder.setCancelable(false);

                Button submit = team.findViewById(R.id.submit_query_btn);
                Button cancel = team.findViewById(R.id.cancel_btn);
                EditText query = team.findViewById(R.id.get_query_edit_text);


                AlertDialog dialog = builder.create();
                dialog.show();

                submit.setOnClickListener(v1 -> {



                    if (isAdded()){
                        Context context = getContext();

                        String getQuery = query.getText().toString();

                        JSONObject object = new JSONObject();
                        try {
                            object.put("description", getQuery);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        new Receiver(context, new Receiver.ApiListener() {
                            @Override
                            public void onResponse(JSONObject object) {
                                try {
                                    String msg = object.getString("message");
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                } catch (JSONException e) {
                                    ErrorHandler.handleException(context, e);
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                ErrorHandler.handleVolleyError(context, error);
                            }
                        }).post_ticket(object);

                    }

                });

                cancel.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });
            }
        });


        return rootView;
    }
}