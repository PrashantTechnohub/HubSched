package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class NetworkReceiver extends BroadcastReceiver {
    AlertDialog alertDialog;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (isOnline(context)) {
                Log.e("net", "Online Connect Intenet ");
                if(alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
            } else {
                dialog(false, context);
                Log.e("net", "Connectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void dialog(Boolean value, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Make sure you turned on Wifi or Data connection");

        builder.setTitle("No Internet");

        builder.setCancelable(false);

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isOnline(context)) {
                    dialog(true, context);
                    Toast.makeText(context, "Internet Connection is available", Toast.LENGTH_SHORT).show();
                } else {
                    dialog(false, context);
                }
            }
        });

         alertDialog = builder.create();
        alertDialog.show();

        if (value.equals(true)){
            alertDialog.dismiss();
        }
    }
}
