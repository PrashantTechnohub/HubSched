package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityEmployeeListBinding;
import com.android.volley.VolleyError;
import com.NakshatraTechnoHub.HubSched.Adapters.EmpListAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class EmployeeListActivity extends BaseActivity {

    ActivityEmployeeListBinding bind;
    EmpListAdapter adapter;
    ArrayList<EmpListModel>list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityEmployeeListBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);




        bind.empListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new EmpListAdapter(this, list);

        bind.empListRecyclerView.setAdapter(adapter);


        bind.searchEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.actionBarLayout.setVisibility(View.GONE);
                bind.searchBarLayout.setVisibility(View.VISIBLE);
                bind.searchBar.requestFocus();

            }
        });

        bind.closeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.actionBarLayout.setVisibility(View.VISIBLE);
                bind.searchBarLayout.setVisibility(View.GONE);
            }
        });
        bind.addEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployeeListActivity.this, CreateEmployeeActivity.class));
            }
        });

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEmpList();
            }
        });

        bind.searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                String text =  bind.searchBar.getText().toString().toLowerCase(Locale.getDefault());
                list = adapter.filter(text);

                if (!text.isEmpty()){
                    adapter = new EmpListAdapter(getApplicationContext(), list);
                    bind.empListRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    getEmpList();
                }

            }
        });


        getEmpList();

    }

    public void getEmpList() {

        new Receiver(this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {
                try {
                    JSONArray emp_details =object.getJSONArray("emp_details");
                    list.clear();


                    for (int i=0;i<emp_details.length();i++){
                        JSONObject jobj=emp_details.getJSONObject(i);
                        EmpListModel model = new Gson().fromJson(jobj.toString(),EmpListModel.class);
                        list.add(model);
                        bind.refresh.setRefreshing(false);

                    }


                } catch (JSONException e) {
                    ErrorHandler.handleException(getApplicationContext(), e);


                }

                adapter = new EmpListAdapter(getApplicationContext(), list);
                bind.empListRecyclerView.setAdapter(adapter);
                bind.empListRecyclerView.invalidate();
                bind.empListRecyclerView.removeAllViews();


                if (bind.refresh.isRefreshing()){
                    bind.refresh.setRefreshing(false);
                    Toast.makeText(EmployeeListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onError(VolleyError error) {
                if (bind.refresh.isRefreshing()){
                    bind.refresh.setRefreshing(false);
                }
                Log.d("TAG", "onError: " +error.getMessage());
                bind.noResult.setVisibility(View.VISIBLE);
                bind.empListRecyclerView.setVisibility(View.GONE);
            }
        }).getEmpList();

    }


}