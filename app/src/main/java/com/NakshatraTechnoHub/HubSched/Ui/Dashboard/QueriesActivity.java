package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.QueryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QueriesActivity extends AppCompatActivity {

    MyAdapter adapter;

    ArrayList<QueryModel> list = new ArrayList<>();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queries);

        recyclerView = findViewById(R.id.queries_recycler_view);


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

                try {
                    JSONArray array = response.getJSONArray("result");

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

//                            ColorStateList thumbColorStateList = ContextCompat.getColorStateList(QueriesActivity.this, R.color.red);
//                            action.setThumbTintList(thumbColorStateList);

//                            ColorStateList trackColorStateList = ContextCompat.getColorStateList(QueriesActivity.this, R.color.red);
//                            action.setTrackTintList(trackColorStateList);


                            id.setText(model.getUserId() + "");
                            description.setText(model.getDescription());
                            status.setText(model.getStatus());


                            action.setChecked(true);
                            if (model.getStatus().equals("opened")) {

                                action.setChecked(true);
                            } else {
                                action.setChecked(false);
                            }


                            registerListener (action, list.get(position), model.get_id()+"");


                        }
                    }, R.layout.cl_bug_list);

                    recyclerView.setLayoutManager(new LinearLayoutManager(QueriesActivity.this));
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onError(VolleyError error) {

            }
        }).ticket_list();
    }

    private void registerListener(SwitchCompat action, QueryModel queryModel, String s) {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    issueChanger(true, compoundButton.getContext(), queryModel);
                } else {

                    issueChanger(false, compoundButton.getContext(),  queryModel);
                }
            }
        };

        action.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void issueChanger(boolean b, Context context,  QueryModel queryModel) {

       JSONObject object = new JSONObject();
        try {
            object.put("_id" , queryModel.get_id());
            object.put("status" , b);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        new Receiver(QueriesActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {
                try {
                    String msg = object.getString("message");
                } catch (JSONException e) {
                    ErrorHandler.handleException(context, e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(context, error);

            }
        }).ticket_status(object);
    }
}