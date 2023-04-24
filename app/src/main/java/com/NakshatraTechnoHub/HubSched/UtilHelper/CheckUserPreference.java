package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;

public class CheckUserPreference {

    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    static String token;
    public static void checkTypeToken(Context context,String type, String token) {

        sharedPref = context.getSharedPreferences("userTypeToken",Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString("type", type);
        editor.putString("token", token);
        editor.commit();

    }

    public static void LogOutUser(Context context, String out, Dialog dialog){
        sharedPref = context.getSharedPreferences("userTypeToken",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (out.equals("out")){
            editor.clear();
            editor.commit();
            dialog.cancel();

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


}
