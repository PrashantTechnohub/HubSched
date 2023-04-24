package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Locale;

public class EmpListAdapter extends RecyclerView.Adapter<EmpListAdapter.EmpHolder> {

    Context context;
    ArrayList<EmpListModel> data= null;
    ArrayList<EmpListModel> empList;





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

        builder.setView(team);

        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();



        Button yes = team.findViewById(R.id.yes_btn);
        Button no = team.findViewById(R.id.no_btn);
        TextView text = team.findViewById(R.id.alert_text);


        holder.empId.setText(empList.get(position).getEmp_id());
        holder.empName.setText(empList.get(position).getEmp_name());
        holder.empPost.setText(empList.get(position).getEmp_post());

        holder.blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text.setText("Are you sure to block "+empList.get(position).getEmp_name() + " !!");

                dialog.show();

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), empList.get(position).getEmp_name() +" blocked !!", Toast.LENGTH_SHORT).show();
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

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text.setText("Are you sure to remove "+empList.get(position).getEmp_name() + " !!");

                dialog.show();

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), empList.get(position).getEmp_name() +" Removed !!", Toast.LENGTH_SHORT).show();
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
        }
        else
        {
            for (EmpListModel emp : empList)
            {
                if (emp.getEmp_id().toLowerCase(Locale.getDefault()).contains(charText)  || emp.getEmp_name().toLowerCase(Locale.getDefault()).contains(charText))
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
        MaterialButton blockBtn, removeBtn, editBtn;


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