package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Models.QueryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.CustomErrorDialog;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.android.volley.VolleyError;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class QueriesActivity extends AppCompatActivity {

    MyAdapter adapter;

    ArrayList<QueryModel> list = new ArrayList<>();

    RecyclerView recyclerView;

    SwipeRefreshLayout refresh;

    GifImageView noResult;

    MaterialCardView pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queries);

        recyclerView = findViewById(R.id.queries_recycler_view);
        refresh = findViewById(R.id.refresh);
        noResult = findViewById(R.id.no_result);
        pd = findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQueries();

            }
        });


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getQueries();
    }

    private void getQueries() {
        new Receiver(QueriesActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {

                pd.setVisibility(View.GONE);
                if (refresh.isRefreshing()) {
                    refresh.setRefreshing(false);
                }
                list.clear();
                try {
                    JSONArray array = response.getJSONArray("result");

                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            QueryModel model = new QueryModel(object.getInt("userId"), object.getInt("_id"),
                                    object.getString("description"), object.getString("status").toUpperCase());
                            list.add(model);
                        }

                        adapter = new MyAdapter<QueryModel>(list, new MyAdapter.OnBindInterface() {
                            @Override
                            public void onBindHolder(MyAdapter.MyHolder holder, int position) {
                                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation);
                                holder.itemView.startAnimation(animation);

                                QueryModel model = list.get(position);

                                TextView id = holder.itemView.findViewById(R.id.member_name);
                                TextView date = holder.itemView.findViewById(R.id.query_date);
                                TextView description = holder.itemView.findViewById(R.id.detail_query);
                                TextView status = holder.itemView.findViewById(R.id.query_status_text);
                                SwitchCompat action = holder.itemView.findViewById(R.id.action_query_btn);


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CustomErrorDialog.mShow(QueriesActivity.this, model.getDescription());
                                    }
                                });
                                id.setText(model.getUserId() + "");
                                description.setText(model.getDescription());
                                status.setText(model.getStatus());

                                if (model.getStatus().toUpperCase().equals("OPENED")) {
                                    action.setChecked(true);
                                } else {
                                    action.setChecked(false);
                                }

                                registerListener(action, model);


                            }
                        }, R.layout.cl_bug_list);

                        recyclerView.setLayoutManager(new LinearLayoutManager(QueriesActivity.this));
                        recyclerView.setAdapter(adapter);

                    } else {
                        pd.setVisibility(View.GONE);
                        noResult.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    ErrorHandler.handleException(QueriesActivity.this, e);
                }


            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(QueriesActivity.this, error);
            }
        }).ticket_list();
    }

    private void registerListener(SwitchCompat action, QueryModel model) {


        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    statusChanger(true, model);
                } else {

                    statusChanger(false, model);
                }
            }
        };

        action.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void statusChanger(boolean bull, QueryModel model) {
        JSONObject object = new JSONObject();

        try {
            object.put("_id", model.get_id());
            object.put("status", bull);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        new Receiver(QueriesActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {
                try {

                    getQueries();
                    String msg = object.getString("message");
                    Toast.makeText(QueriesActivity.this, msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        }).ticket_status(object);
    }


}