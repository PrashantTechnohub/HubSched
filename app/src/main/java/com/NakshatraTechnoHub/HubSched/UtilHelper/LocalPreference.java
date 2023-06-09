package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Firebase.MyFirebaseMessagingService;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.DashboardActivity;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Constant.withToken(Constant.LOGOUT_URL, context), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rsp = response.getString("message");
                        editor.clear();
                        editor.commit();
                        dialog.cancel();
                        Toast.makeText(context, rsp, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    

                    try {
                        if (error.networkResponse!= null){
                            if(error.networkResponse.statusCode == 500){
                                String errorString = new String(error.networkResponse.data);
                                Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(context, "Something went wrong or have a server issues", Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        Log.e("CreateEMP", "onErrorResponse: ", e );
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            VolleySingleton.getInstance(context).addToRequestQueue(request);

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
