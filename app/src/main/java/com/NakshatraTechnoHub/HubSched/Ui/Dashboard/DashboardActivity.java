package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.NotificationModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.HomeFragment;
import com.NakshatraTechnoHub.HubSched.Ui.Fragment.MeetingFragment;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityDashboardBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding bind;

    MyAdapter<NotificationModel> notificationModelMyAdapter;

    ArrayList<NotificationModel> notificationModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("notification")) {
            // Open the material dialog for notifications
            showNotificationDialog();
        }


        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        MenuItem notificationItem = toolbar.getMenu().findItem(R.id.action_notification);

        notificationItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showNotificationDialog();

                return true;
            }
        });

        ChipNavigationBar chipNavigationBar = findViewById(R.id.bottom_nav);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment selectedFragment = null;
                switch (id) {
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.meeting:
                        selectedFragment = new MeetingFragment();
                        break;

                }

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment != null && currentFragment.getClass().equals(selectedFragment.getClass())) {
                    // Selected fragment is already visible, no need to replace
                    return;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commitAllowingStateLoss();
            }
        });

        chipNavigationBar.setBackground(null);
        chipNavigationBar.setItemSelected(R.id.home, true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if (isOpen) {
                        chipNavigationBar.setVisibility(View.GONE);
                    } else {
                        chipNavigationBar.setVisibility(View.VISIBLE);
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

                    case R.id.queries:
                        intent = new Intent(DashboardActivity.this, QueriesActivity.class);
                        break;

                    case R.id.support:
                        intent = new Intent(DashboardActivity.this, SupportActivity.class);
                        break;

                    case R.id.myAccount:
                        intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                        break;

                    case R.id.logOutAccount:
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DashboardActivity.this, R.style.MaterialAlertDialog_Rounded);
                        LayoutInflater inflater1 = (LayoutInflater) DashboardActivity.this.getSystemService(DashboardActivity.this.LAYOUT_INFLATER_SERVICE);
                        View team = inflater1.inflate(R.layout.cl_alert, null);
                        builder.setView(team);
                        builder.setCancelable(false);

                        Button logout = team.findViewById(R.id.yes_btn);
                        Button no = team.findViewById(R.id.no_btn);
                        TextView text = team.findViewById(R.id.alert_text);

                        text.setText("Are you sure want to logout !!");

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        logout.setOnClickListener(v1 -> LocalPreference.LogOutUser(DashboardActivity.this, "out", dialog));

                        no.setOnClickListener(v12 -> dialog.cancel());
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

    private void showNotificationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DashboardActivity.this, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View team = inflater1.inflate(R.layout.cl_notification_view, null);
        builder.setView(team);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        // Apply animation to the dialog
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.DialogAnimation); // Replace DialogAnimation with your animation style
        }

        ImageView back = team.findViewById(R.id.back);
        GifImageView empty = team.findViewById(R.id.no_result);
        RecyclerView notificationRecyclerView = team.findViewById(R.id.notification_recyclerview);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        List<Pair<String, String>> notifications = LocalPreference.getNotifications(DashboardActivity.this);
        notificationModelArrayList.clear(); // Clear the list before populating it again
        for (Pair<String, String> notification : notifications) {
            String title = notification.first;
            String detail = notification.second;
            NotificationModel model = new NotificationModel(title, detail);
            notificationModelArrayList.add(model);
        }

        if (notificationModelArrayList.isEmpty()) {
            notificationRecyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }

        notificationModelMyAdapter = new MyAdapter<NotificationModel>(notificationModelArrayList, new MyAdapter.OnBindInterface() {
            @Override
            public void onBindHolder(MyAdapter.MyHolder holder, int position) {
                NotificationModel model = notificationModelArrayList.get(position);
                TextView title = holder.itemView.findViewById(R.id.notification_title);
                TextView detail = holder.itemView.findViewById(R.id.notification_detail);


                if (model.getDetail() != null && model.getTitle() != null) {
                    title.setText(model.getTitle());
                    detail.setText(model.getDetail());
                } else {
                    Toast.makeText(DashboardActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                }


            }
        }, R.layout.cl_notification_list);

        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
        notificationRecyclerView.setAdapter(notificationModelMyAdapter);

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DashboardActivity.this, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View team = inflater1.inflate(R.layout.cl_alert, null);
        builder.setView(team);
        builder.setCancelable(false);

        Button yes = team.findViewById(R.id.yes_btn);
        Button no = team.findViewById(R.id.no_btn);
        TextView text = team.findViewById(R.id.alert_text);

        text.setText("Are you sure you want to exit?");
        AlertDialog dialog = builder.create();
        dialog.show();

        yes.setOnClickListener(v1 -> {
            finish();
            dialog.cancel();
            clearFromRecentTasks();
        });

        no.setOnClickListener(v12 -> dialog.cancel());
    }

    private void clearFromRecentTasks() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.AppTask appTask = getSystemService(ActivityManager.class).getAppTasks().get(0);
            appTask.finishAndRemoveTask();
        } else {
            // For older Android versions
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(getPackageName());
        }
    }
}
