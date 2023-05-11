package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.AddEmployeeActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class EmpListAdapter extends RecyclerView.Adapter<EmpListAdapter.EmpHolder> {

    Context context;
    ArrayList<EmpListModel> data= null;
    ArrayList<EmpListModel> empList;




    public void registerListner(SwitchCompat switchCompat , EmpListModel model){
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){

                    EmpStatusChanger("active", compoundButton.getContext(), (SwitchCompat) compoundButton, model);
                }else {

                    EmpStatusChanger("blocked", compoundButton.getContext(), (SwitchCompat) compoundButton, model);
                }
            }
        };

        switchCompat.setOnCheckedChangeListener(checkedChangeListener);
    }



    public EmpListAdapter(Context context, ArrayList<EmpListModel> empList1) {
        this.context = context;
        this.empList = empList1;

        data= empList;
        empList=new ArrayList<EmpListModel>();
        empList.addAll(data);
    }

    @NonNull
    @Override
    public EmpListAdapter.EmpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_emp_list, parent, false);
        return new EmpHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpListAdapter.EmpHolder holder, @SuppressLint("RecyclerView") int position) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(holder.blockBtn.getContext(), R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater) holder.blockBtn.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View team = inflater1.inflate(R.layout.cl_alert, null);

        View editProfileLayout = inflater1.inflate(R.layout.cl_edit_profile, null);
        EditText userType = editProfileLayout.findViewById(R.id.userType_ed);
        EditText userName = editProfileLayout.findViewById(R.id.user_name);
        EditText userEmpId = editProfileLayout.findViewById(R.id.user_EmpId);
        EditText userEmail = editProfileLayout.findViewById(R.id.user_email);
        EditText userMobile = editProfileLayout.findViewById(R.id.user_mobile);
        EditText userGender = editProfileLayout.findViewById(R.id.user_gender);
        EditText userPosition = editProfileLayout.findViewById(R.id.user_position);
        EditText userPassword = editProfileLayout.findViewById(R.id.user_password);
        Button updateProfileBtn = editProfileLayout.findViewById(R.id.update_profile_ed);

        userType.setText(String.valueOf(empList.get(position).getUserType()));
        userName.setText(empList.get(position).getName());
        userEmpId.setText(String.valueOf(empList.get(position).getEmpId()));
        userEmail.setText(empList.get(position).getEmail());
        userMobile.setText(empList.get(position).getMobile());
        userGender.setText(empList.get(position).getGender());
        userPosition.setText(empList.get(position).getPosition());
        userPassword.setText(empList.get(position).getPassword());

        Button yes = team.findViewById(R.id.yes_btn);
        Button no = team.findViewById(R.id.no_btn);
        TextView text = team.findViewById(R.id.alert_text);


        holder.empId.setText(empList.get(position).getEmpId()+"");
        holder.empName.setText(empList.get(position).getName());
        holder.empPost.setText(empList.get(position).getPosition());

        String status =    empList.get(position).getStatus();
        holder.blockBtn.setSelected(true);


        if (status.equals("active")){
            holder.blockBtn.setText("Active");
            holder.blockBtn.setChecked(true);
            holder.blockBtn.setTextColor(Color.parseColor("#149519"));
        }else{

            holder.blockBtn.setText("Blocked");
            holder.blockBtn.setChecked(false);
            holder.blockBtn.setTextColor(Color.parseColor("#BF3D33"));
        }

       String type =  LocalPreference.getType(context);

        if (type.equals("organiser")){
            holder.editBtn.setVisibility(View.GONE);
        }


        registerListner(holder.blockBtn,empList.get(position));

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setView(team);
                builder.setCancelable(true);
                final AlertDialog dialog = builder.create();

                text.setText("Are you sure to remove "+empList.get(position).getName() + " !!");

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), empList.get(position).getName() +" Removed !!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = String.valueOf(empList.get(position).get_id());
                Toast.makeText(view.getContext(), id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, AddEmployeeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userType", userType.getText().toString());
                intent.putExtra("actionType","update");
                intent.putExtra("id",id);
                intent.putExtra("empId",userEmpId.getText().toString());
                intent.putExtra("name",userName.getText().toString());
                intent.putExtra("gender",userGender.getText().toString());
                intent.putExtra("mobile",userMobile.getText().toString());
                intent.putExtra("email",userEmail.getText().toString());
                intent.putExtra("position",userPosition.getText().toString());
                intent.putExtra("password",userPassword.getText().toString());
                context.startActivity(intent);
            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void EmpStatusChanger(String active, Context context, SwitchCompat switchCompat, EmpListModel model) {
        JSONObject params = new JSONObject();

        if (active.equals("active")){
            try {
                params.put("status", true);
                params.put("empId", model.getEmpId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            try {
                params.put("status", false);
                params.put("empId", model.getEmpId());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.EMP_STATUS_URL,context),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status=    response.getString("status");
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                    Log.d("aa", "onResponse: " +status);

                    EmpListAdapter adapter = new EmpListAdapter(context,empList);

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
                    if(error.networkResponse.statusCode == 500){
                        String errorString = new String(error.networkResponse.data);
                        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){

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
            for (EmpListModel emp : empList)
            {
                if (String.valueOf(emp.getEmpId()).toLowerCase(Locale.getDefault()).contains(charText)  || emp.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    data.add(emp);
                }
            }
        }

        notifyDataSetChanged();
        return data;
    }

    public static class EmpHolder extends RecyclerView.ViewHolder {

        TextView empId,empName,empPost;
        MaterialButton  removeBtn, editBtn;

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