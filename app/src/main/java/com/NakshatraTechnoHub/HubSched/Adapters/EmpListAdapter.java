package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.CreateEmployeeActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class EmpListAdapter extends RecyclerView.Adapter<EmpListAdapter.EmpHolder> {

    Context context;
    ArrayList<EmpListModel> data = null;
    ArrayList<EmpListModel> empList;


    public void registerListner(SwitchCompat switchCompat, EmpListModel model) {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    EmpStatusChanger("active", compoundButton.getContext(), (SwitchCompat) compoundButton, model);
                } else {

                    EmpStatusChanger("blocked", compoundButton.getContext(), (SwitchCompat) compoundButton, model);
                }
            }
        };

        switchCompat.setOnCheckedChangeListener(checkedChangeListener);
    }


    public EmpListAdapter(Context context, ArrayList<EmpListModel> empList1) {
        this.context = context;
        this.empList = empList1;

        data = empList;
        empList = new ArrayList<EmpListModel>();
        empList.addAll(data);
    }

    @NonNull
    @Override
    public EmpListAdapter.EmpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_emp_list, parent, false);
        return new EmpHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpListAdapter.EmpHolder holder, @SuppressLint("RecyclerView") int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation);
        holder.itemView.startAnimation(animation);

        EmpListModel model = empList.get(position);

        holder.empId.setText(empList.get(position).getEmpId() + "");
        holder.empName.setText(empList.get(position).getName());
        holder.empPost.setText(empList.get(position).getPosition());
        String status = empList.get(position).getStatus();
        holder.blockBtn.setSelected(true);


        if (status.equals("active")) {
            holder.blockBtn.setText("Active");
            holder.blockBtn.setChecked(true);
            holder.blockBtn.setTextColor(Color.parseColor("#149519"));
        } else {

            holder.blockBtn.setText("Blocked");
            holder.blockBtn.setChecked(false);
            holder.blockBtn.setTextColor(Color.parseColor("#BF3D33"));
        }

        String type = LocalPreference.getType(context);

        if (type.equals("organiser")) {
            holder.editBtn.setVisibility(View.GONE);
        }


        registerListner(holder.blockBtn, empList.get(position));

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure to remove " + empList.get(position).getName() + " !!");

                builder.setTitle("Confirm");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeEmployee(empList.get(position).get_id());

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss(); // Dismiss the dialog
                    }
                });
                builder.create().show();

            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CreateEmployeeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("actionType", "EmpUpdateByAdmin");
                intent.putExtra("userType", model.getUserType()+"");
                intent.putExtra("id", model.get_id()+"");
                intent.putExtra("empId", model.getEmpId()+"");
                intent.putExtra("name", model.getName());
                intent.putExtra("gender",model.getGender());
                intent.putExtra("mobile", model.getMobile());
                intent.putExtra("email",model.getEmail());
                intent.putExtra("position",model.getPosition());
                intent.putExtra("password", model.getPassword());
                context.startActivity(intent);
            }
        });


    }

    private void removeEmployee(int id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.DELETE, Constant.withToken(Constant.REMOVE_EMP_URL + (id + ""), context), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("message");
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                    // Remove the employee from the list and notify the adapter
                    empList.remove(getEmployeePosition(id));
                    notifyDataSetChanged();


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                notifyDataSetChanged();
                try {
                    if (error.networkResponse.statusCode == 500) {
                        String errorString = new String(error.networkResponse.data);
                        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        queue.add(objectRequest);
    }

    private int getEmployeePosition(int id) {
        for (int i = 0; i < empList.size(); i++) {
            if (empList.get(i).get_id() == id) {
                return i;
            }
        }
        return -1;
    }

    private void EmpStatusChanger(String active, Context context, SwitchCompat switchCompat, EmpListModel model) {
        JSONObject params = new JSONObject();

        if (active.equals("active")) {
            try {
                params.put("status", true);
                params.put("empId", model.getEmpId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                params.put("status", false);
                params.put("empId", model.getEmpId());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.EMP_STATUS_URL, context), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                    Log.d("aa", "onResponse: " + status);

                    EmpListAdapter adapter = new EmpListAdapter(context, empList);

                    model.setStatus(status);
                    notifyDataSetChanged();
//                    if (status.equals("active")){
//                       switchCompat.setText("Active");
//                        switchCompat.setChecked(true);
//                        switchCompat.setTextColor(Color.parseColor("#149519"));
//                    }else{
//
//                        switchCompat.setText("Blocked");
//                        switchCompat.setChecked(false);
//                        switchCompat.setTextColor(Color.parseColor("#BF3D33"));
//                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                notifyDataSetChanged();
                try {
                    if (error.networkResponse.statusCode == 500) {
                        String errorString = new String(error.networkResponse.data);
                        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        queue.add(objectRequest);
    }

    @Override
    public int getItemCount() {
        return empList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public ArrayList<EmpListModel> filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        data.clear();
        if (charText.length() == 0) {
            data.addAll(empList);
        } else {
            for (EmpListModel emp : empList) {
                if (String.valueOf(emp.getEmpId()).toLowerCase(Locale.getDefault()).contains(charText) || emp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(emp);
                }
            }
        }

        notifyDataSetChanged();
        return data;
    }

    public static class EmpHolder extends RecyclerView.ViewHolder {

        TextView empId, empName, empPost;
        MaterialButton removeBtn, editBtn;

        SwitchCompat blockBtn;

        public EmpHolder(@NonNull View emp) {
            super(emp);

            empId = emp.findViewById(R.id.emp_id);
            empName = emp.findViewById(R.id.emp_name);
            empPost = emp.findViewById(R.id.emp_post);

            blockBtn = emp.findViewById(R.id.block_emp_btn);
            removeBtn = emp.findViewById(R.id.remove_emp_btn);
            editBtn = emp.findViewById(R.id.edit_emp_btn);


        }
    }
}