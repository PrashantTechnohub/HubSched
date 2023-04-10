package com.example.meethall.UtilHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.meethall.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MyHelper {

   static MaterialAlertDialogBuilder builder;

    public static void ShowResponseDialog(String msg, Context context) {
        builder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.response_dialog_layout, null);

        builder.setView(view);

        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        TextView textAlert = view.findViewById(R.id.alert_text);
        textAlert.setText(msg);

        dialog.show();
    }
}
