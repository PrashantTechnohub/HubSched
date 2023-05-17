package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;

public class LocalPreference {

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

    public static void store_id(Context context,String _id) {

        sharedPref = context.getSharedPreferences("_id",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("_id", _id);
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static String getToken(Context context){
        sharedPref = context.getSharedPreferences("userTypeToken",Context.MODE_PRIVATE);
        String getToken = sharedPref.getString("token", "");
        return getToken;
    }

    public static String getType(Context context){
        sharedPref = context.getSharedPreferences("userTypeToken",Context.MODE_PRIVATE);
        String getType = sharedPref.getString("type", "");
        return getType;
    }

    public static String get_Id(Context context){
        sharedPref = context.getSharedPreferences("_id",Context.MODE_PRIVATE);
        String getId = sharedPref.getString("_id", "");
        return getId;
    }



}
