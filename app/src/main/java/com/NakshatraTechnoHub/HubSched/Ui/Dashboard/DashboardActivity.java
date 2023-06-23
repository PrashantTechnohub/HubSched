package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.MeetingFragment;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.ProfileFragment;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.SupportFragment;

import com.NakshatraTechnoHub.HubSched.Ui.Fragment.HomeFragment;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityDashboardBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();


        setContentView(view);

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

            new Handler().postDelayed(() -> {
                Intent intent = null;

                switch (id) {
                    case R.id.hallManagement:
                        intent = new Intent(DashboardActivity.this, RoomListActivity.class);
                        break;

                    case R.id.employeeList:
                        intent = new Intent(DashboardActivity.this, EmployeeListActivity.class);
                        break;

                    case R.id.meetingList:
                        intent = new Intent(DashboardActivity.this, MeetingListActivity.class);
                        break;

                    case R.id.editContent:
                        intent = new Intent(DashboardActivity.this, EditContentActivity.class);
                        break;

                    default:
                        break;
                }

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_enter_animation, R.anim.slide_exit_animation);
                }
            }, 200); // Adjust the delay duration as needed

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

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment != null && currentFragment.getClass().equals(selectedFragment.getClass())) {
                // Selected fragment is already visible, no need to replace
                return true;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.fragment_container, selectedFragment);
            transaction.commitAllowingStateLoss();

            return true;
        }
    };

}