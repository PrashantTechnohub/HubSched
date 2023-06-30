package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalPreference {

    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    public static void storeUserDetail(Context context,String type, String token, String id, String CompanyId) {

        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("type", type);
        editor.putString("token", token);
        editor.putString("companyId", CompanyId);
        editor.putString("_id", id);
        editor.commit();

    }



    public static void LogOutUser(Context context, String out, Dialog dialog){
        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (out.equals("out")){
            dialog.dismiss();
            pd.mShow(context);
           logOutUser(context, Constant.withToken(Constant.LOGOUT_URL, context));

        }
        else if (out.equals("outPantry")) {
            dialog.dismiss();
            pd.mShow(context);
            logOutUser(context, Constant.withToken(Constant.LOGOUT_PANTRY_URL, context));

        }
    }

    private static void logOutUser(Context context, String s) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, s, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rsp = response.getString("message");
                    editor.clear();
                    editor.commit();
                    Toast.makeText(context, rsp, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    pd.mDismiss();

                } catch (JSONException e) {
                    pd.mDismiss();
                    ErrorHandler.handleException(context, e);

                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.mDismiss();
                ErrorHandler.handleVolleyError(context, error);

            }
        });


        VolleySingleton.getInstance(context).addToRequestQueue(request);
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

    public static void store_meetId(Context context,String meetId) {

        sharedPref = context.getSharedPreferences("meetId",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("id", meetId);
        editor.commit();

    }


    public static String get_meetId(Context context){

        sharedPref = context.getSharedPreferences("meetId",Context.MODE_PRIVATE);
        String id = sharedPref.getString("id", "");
        return id;
    }

    public static void store_total_employees(Context context,String total) {

        sharedPref = context.getSharedPreferences("admin",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("totalEmployees", total);
        editor.commit();

    }

    public static String get_total_employees(Context context){

        sharedPref = context.getSharedPreferences("admin",Context.MODE_PRIVATE);
        String total = sharedPref.getString("totalEmployees", "");
        return total;
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


    public static String get_company_Id(Context context){
        sharedPref = context.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        String cmp = sharedPref.getString("companyId", "");
        return cmp;
    }
}
