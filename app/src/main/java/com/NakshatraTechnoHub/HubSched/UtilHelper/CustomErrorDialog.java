package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.PantryDashboard.PantryActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomErrorDialog {

    public static void mShow(Context context, String title, String errorMessage) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View team = inflater1.inflate(R.layout.cl_error_dialog, null);
        builder.setView(team);
        TextView titleTextView = team.findViewById(R.id.error_title);
        TextView messageTextView = team.findViewById(R.id.error_text);

        // Set the title and error message
        titleTextView.setText(title);
        messageTextView.setText(errorMessage);

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}
