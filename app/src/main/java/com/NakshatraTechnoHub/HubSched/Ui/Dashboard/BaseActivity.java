package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.NetworkReceiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;

public class BaseActivity extends AppCompatActivity {

    NetworkReceiver mNetworkReceiver = new NetworkReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, R.anim.activity_close_animation);

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

        // Apply the animation when starting a new activity
        overridePendingTransition(0, R.anim.activity_close_animation);



    }
    @Override
    public void finish() {
        super.finish();

        // Apply the animation when finishing the activity
        overridePendingTransition(0, R.anim.activity_close_animation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (pd.isDialogShown()) {
            pd.handleBackPress(this, keyCode);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_close_animation);

    }

    @Override
    protected void onPause() {
        super.onPause();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }
}
