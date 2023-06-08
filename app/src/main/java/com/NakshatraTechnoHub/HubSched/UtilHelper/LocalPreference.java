package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.NakshatraTechnoHub.HubSched.Firebase.MyFirebaseMessagingService;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;

public class LocalPreference {

    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    public static void storeUserDetail(Context context,String type, String token, String id) {

        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("type", type);
        editor.putString("token", token);
        editor.putString("_id", id);
        editor.commit();

    }

    public static void store_id(Context context,String _id) {

        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("_id", _id);
        editor.commit();

    }


    public static void LogOutUser(Context context, String out, Dialog dialog){
        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
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
        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        String getToken = sharedPref.getString("token", "");
        return getToken;
    }

    public static void store_FirebaseToken(Context context,String firebaseToken) {

        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("firebaseToken", firebaseToken);
        editor.commit();

    }
    public static String getFirebaseToken(Context context){

        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        String firebaseToken = sharedPref.getString("firebaseToken", "");
        return firebaseToken;
    }

    public static String getType(Context context){
        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        String getType = sharedPref.getString("type", "");
        return getType;
    }

    public static String get_Id(Context context){
        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        String getId = sharedPref.getString("_id", "");
        return getId;
    }
    


}
