package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomErrorDialog {

    public static void mShow(Context context, String errorMessage) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = LayoutInflater.from(context);
        View team = inflater1.inflate(R.layout.cl_error_dialog, null);
        builder.setView(team);
        TextView messageTextView = team.findViewById(R.id.error_text);

        // Set the title and error message
        messageTextView.setText(errorMessage);

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; // Apply custom animation

        dialog.setOnShowListener(dialogInterface -> {
            // Apply fade-in animation to the dialog content
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(300);
            team.startAnimation(alphaAnimation);
        });

        dialog.show();
    }
}
