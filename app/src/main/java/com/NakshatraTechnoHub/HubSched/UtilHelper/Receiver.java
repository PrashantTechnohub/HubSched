package com.NakshatraTechnoHub.HubSched.UtilHelper;


import static com.NakshatraTechnoHub.HubSched.Api.Constant.CHAT_URL;
import static com.NakshatraTechnoHub.HubSched.Api.Constant.CREATE_EMP_URL;
import static com.NakshatraTechnoHub.HubSched.Api.Constant.CREATE_ROOM_URL;
import static com.NakshatraTechnoHub.HubSched.Api.Constant.MEET_REQUEST_URL;
import static com.NakshatraTechnoHub.HubSched.Api.Constant.REMOVE_EMP_URL;
import static com.NakshatraTechnoHub.HubSched.Api.Constant.REMOVE_ROOM_URL;
import static com.NakshatraTechnoHub.HubSched.Api.Constant.UPDATE_PROFILE_URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.RoomListActivity;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Receiver {
    private static final String TAG = "Receiver";
    Context context;
    ApiListener listener;
    ListListener listListener;
    RequestQueue requestQueue;


    public Receiver(Context context, ApiListener listener) {
        this.context = context;
        this.listener = listener;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public Receiver(Context context, ListListener listener) {
        this.context = context;
        this.listListener = listener;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    private void getdata(int method, String url, JSONObject object) {
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response);
                listener.onResponse(response);
                pd.mDismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                
                listener.onError(error);
                try {
                    
                    Log.e(TAG, new String(error.networkResponse.data));
                    ErrorHandler.handleVolleyError(context, error);
                    Toast.makeText(context, "something went wrong " + error, Toast.LENGTH_SHORT).show();
                    pd.mDismiss();
                } catch (Exception e) {
                    
                    Log.e(TAG, error.toString());
                    ErrorHandler.handleException(context, e);
                    Toast.makeText(context, "something went wrong " + error, Toast.LENGTH_SHORT).show();
                    pd.mDismiss();
                }
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return object == null ? null : object.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {

                    return null;
                }
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    JSONObject result = null;

                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }


        };

        requestQueue.add(jsonObjectRequest);
    }


    private void getList(int method, String url, JSONObject object) {

        pd.mShow(context);
        
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(method, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                Log.d(TAG, "onResponse: " + array);
                listListener.onResponse(array);
                pd.mDismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                
                try {
                    listListener.onError(error);
                    Log.e(TAG, new String(error.networkResponse.data));
                    String err = new String(error.networkResponse.data);
                    CustomErrorDialog.mShow(context, "Error", err);
                    Toast.makeText(context, "" + err, Toast.LENGTH_SHORT).show();
                    pd.mDismiss();

                } catch (Exception e) {
                    Log.e(TAG, error.toString());
                    ErrorHandler.handleException(context, error);
                    Toast.makeText(context, "something went wrong " + error, Toast.LENGTH_SHORT).show();
                    pd.mDismiss();
                }
            }
        }) {


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return object == null ? null : object.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }


        };

        requestQueue.add(jsonArrayRequest);
    }


    //---------------------------------------------------------------------------


    public void loginUser(JSONObject params ) {
        getdata(Request.Method.POST, Constant.LOGIN_URL, params);
    }


    public void getMeetingList() {
        getList(Request.Method.GET,  Constant.withToken(Constant.MEETING_LIST_URL, context), new JSONObject());
    }
    public void getBookedSlotList(JSONObject param) {
        getdata(Request.Method.POST,  Constant.withToken(Constant.MEETS_FOR_DATE_URL, context), param);
    }

    public void getProfileDetail() {
        getdata(Request.Method.GET,  Constant.withToken(Constant.EMP_PROFILE_URL, context), new JSONObject());
    }
    public void post_item_to_pantry() {
        getdata(Request.Method.POST,  Constant.withToken(Constant.PANTRY_REQUEST_URL, context), new JSONObject());
    }
    public void emp_list_for_meeting(JSONObject object) {
        getdata(Request.Method.POST,  Constant.withToken(Constant.EMPLOYEE_LIST_FOR_MEET_URL, context),object);
    }


    public void getRoomList() {
        getList(Request.Method.GET,  Constant.withToken(Constant.MEET_ROOMS_URL, context), new JSONObject());
    }

    public void accept_denied_meeting(JSONObject params ) {
        getdata(Request.Method.POST, Constant.withToken(MEET_REQUEST_URL, context), params);
    }

    public void remove_room(Integer id ) {
        getdata(Request.Method.DELETE, Constant.withToken(REMOVE_ROOM_URL+(id+""), context), new JSONObject());
    }

    public void remove_emp(Integer id ) {
        getdata(Request.Method.DELETE, Constant.withToken(REMOVE_EMP_URL+(id+""), context), new JSONObject());
    }
    public void create_room(JSONObject params ) {
        getdata(Request.Method.POST, Constant.withToken(CREATE_ROOM_URL, context), params);
    }
    public void update_profile(JSONObject params ) {
        getdata(Request.Method.PUT, Constant.withToken(UPDATE_PROFILE_URL, context), params);
    }

    public void add_emp(JSONObject params ) {
        getdata(Request.Method.POST, Constant.withToken(CREATE_EMP_URL, context), params);
    }
    public void save_sms_to_server(JSONObject params ) {
        getdata(Request.Method.POST, Constant.withToken(CHAT_URL, context), params);
    }
    public void create_meeting(JSONObject params ) {
        getdata(Request.Method.POST, Constant.withToken(Constant.CREATE_MEETING_URL, context), params);
    }

    public void getEmpList() {
        getdata(Request.Method.GET, withToken(Constant.EMP_LIST_URL), new JSONObject());
    }
    public void getChat(int meetId) {
        getdata(Request.Method.GET, withToken(Constant.GET_CHAT_URL+ "/" + meetId), new JSONObject());
    }


    public void getMyOrderList(int meetId) {
        getList(Request.Method.GET, withToken(Constant.GET_TRIGGER_PANTRY + "/" + meetId), new JSONObject());
    }



    private String withToken(String url) {
        String token = LocalPreference.getToken(context);
        if (token == "") {
            Toast.makeText(context, "token not found", Toast.LENGTH_SHORT).show();
        }
        return url + "?token=" + token;
    }

    public interface ApiListener {
        void onResponse(JSONObject object);

        void onError(VolleyError error);
    }

    public interface ListListener {
        void onResponse(JSONArray object);

        void onError(VolleyError error);
    }

}
