package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.NakshatraTechnoHub.HubSched.databinding.ActivityOrganizerListBinding;

public class OrganizerListActivity extends AppCompatActivity {
    ActivityOrganizerListBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityOrganizerListBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}