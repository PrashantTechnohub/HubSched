package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.MeetingFragment;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.ProfileFragment;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.SupportFragment;
import com.NakshatraTechnoHub.HubSched.UtilHelper.NetworkReceiver;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityDashboardBinding;

import com.NakshatraTechnoHub.HubSched.Ui.Fragment.HomeFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();


        setContentView(view);
        showNoti("Sived", "aksf laksjf lksa jflksa jl");

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        bottomNavigationView.setBackground(null);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if(isOpen){
                        bottomNavigationView.setVisibility(View.GONE);
                    }else{
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id) {

                case R.id.hallManagement:
                    startActivity(new Intent(DashboardActivity.this, RoomListActivity.class));
                    break;

                case R.id.employeeList:
                    startActivity(new Intent(DashboardActivity.this, EmployeeListActivity.class));
                    break;

                case R.id.organizerList:
                    startActivity(new Intent(DashboardActivity.this, OrganizerListActivity.class));
                    break;

                case R.id.editContent:
                    startActivity(new Intent(DashboardActivity.this, EditContentActivity.class));
                    break;


                default:
                    return true;

            }
            return true;
        });

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.meeting:
                    selectedFragment = new MeetingFragment();
                    break;

                case R.id.account:
                    selectedFragment = new ProfileFragment();
                    break;

                case R.id.support:
                    selectedFragment = new SupportFragment();
                    break;


            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };

    private void showNoti(String title, String body) {
        // Create a unique notification channel ID for Android Oreo and above
        String channelId = "my_channel_id";
        String channelName = "My Channel";

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Create the notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Display the notification
        notificationManager.notify(0, builder.build());
    }

}