package com.NakshatraTechnoHub.HubSched.UtilHelper;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
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

    ProgressDialog progressBar;


    public Receiver(Context context, ApiListener listener) {
        this.context = context;
        this.listener = listener;
        this.requestQueue = Volley.newRequestQueue(context);
        this.progressBar = new ProgressDialog(context);
    }

    public Receiver(Context context, ListListener listener) {
        this.context = context;
        this.listListener = listener;
        this.requestQueue = Volley.newRequestQueue(context);
        this.progressBar = new ProgressDialog(context);
    }

    private void getdata(int method, String url, JSONObject object) {
        pd.mShow(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response);
                pd.mDismiss();

                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.mDismiss();
                listener.onError(error);
                try {
                    Log.e(TAG, new String(error.networkResponse.data));
                    ErrorHandler.handleVolleyError(context, error);
                    Toast.makeText(context, "something went wrong " + error, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e(TAG, error.toString());
                    ErrorHandler.handleException(context, e);
                    Toast.makeText(context, "something went wrong " + error, Toast.LENGTH_SHORT).show();

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
                pd.mDismiss();
                listListener.onResponse(array);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.mDismiss();
                listListener.onError(error);

                try {
                    Log.e(TAG, new String(error.networkResponse.data));
                    String err = new String(error.networkResponse.data);
                    CustomErrorDialog.mShow(context, "Error", err);
                    Toast.makeText(context, "" + err, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e(TAG, error.toString());
                    ErrorHandler.handleException(context, e);
                    Toast.makeText(context, "something went wrong " + error, Toast.LENGTH_SHORT).show();
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


    public void getAddress(double lat, double lng) {
        getdata(Request.Method.GET, "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lng + "&zoom=18&addressdetails=1", new JSONObject());
        progressBar.dismiss();
    }

    public void getEmpList() {
        getdata(Request.Method.GET, withToken(Constant.EMP_LIST_URL), new JSONObject());
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
