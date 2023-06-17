package com.NakshatraTechnoHub.HubSched.UtilHelper;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;

public class ErrorHandler {
    public static void handleVolleyError(Context context, VolleyError error) {
        String errorMessage = "An error occurred. Please try again later.";
        String errorTitle = null;

        if (error instanceof NetworkError) {
            errorTitle = "NetworkError";
            errorMessage = "Network error occurred. Please check your connection.";
        } else if (error instanceof ServerError) {
            errorTitle = "ServerError";

            errorMessage = "Server error occurred. Please try again later.";
            if (error.networkResponse != null) {
                int statusCode = error.networkResponse.statusCode;
                // Check the status code for more specific error handling
                switch (statusCode) {
                    case 400:
                        errorTitle = "Bad request";

                        errorMessage = "Bad request error occurred.";
                        break;
                    case 401:
                        errorTitle = "Unauthorized error";

                        errorMessage = "Unauthorized error occurred.";
                        break;
                    case 404:
                        errorTitle = "Unauthorized error";

                        errorMessage = "Resource not found error occurred.";
                        break;
                    case 500:
                        errorTitle = "Internal error";
                        errorMessage = "Internal server error occurred.";

                        try{
                            errorMessage = new String(error.networkResponse.data);
                        }catch (Exception e){
                        }

                        break;
                    // Add more status code cases as needed
                }
            }
        } else if (error instanceof AuthFailureError) {
            errorTitle = "Authentication failure";

            errorMessage = "Authentication failure error occurred. Please try again.";
        } else if (error instanceof ParseError) {
            errorTitle = "Parse Error";
            errorMessage = "Error occurred while parsing data. Please try again.";
        } else if (error instanceof NoConnectionError) {
            errorTitle = "No connection";

            errorMessage = "No connection error occurred. Please check your internet connection.";
        } else if (error instanceof TimeoutError) {
            errorTitle = "TimeoutError";

            errorMessage = "Request timeout error occurred. Please try again.";
        }

        Log.e(TAG, "Volley Error: " + errorMessage);

        CustomErrorDialog.mShow(context, errorTitle, errorMessage);
    }

    public static void handleException(Context context, Exception exception) {
        String errorMessage = "An error occurred. Please try again later.";
        String errorTitle = "Error";

        if (exception instanceof NetworkError) {
            errorTitle = "Server Connectivity Error";
            errorMessage = "Server Connectivity error occurred. try again later.";
        } else if (exception instanceof ServerError) {
            errorTitle = "Server Error";
            errorMessage = "Server error occurred. Please try again later.";
        } else if (exception instanceof AuthFailureError) {
            errorTitle = "Authentication Failure";
            errorMessage = "Authentication failure error occurred. Please try again.";
        } else if (exception instanceof ParseError) {
            errorTitle = "Parsing Error";
            errorMessage = "Error occurred while parsing data. Please try again.";
        } else if (exception instanceof NoConnectionError) {
            errorTitle = "No Connection";
            errorMessage = "No connection error occurred. Please check your internet connection.";
        } else if (exception instanceof TimeoutError) {
            errorTitle = "Timeout Error";
            errorMessage = "Request timeout error occurred. Please try again.";
        } else if (exception instanceof JSONException) {
            errorTitle = "JSON Error";
            errorMessage = "JSON error occurred. Please check the data format.";
        } else {
            // Generic catch-all case for all other exceptions
            errorMessage = "An unexpected error occurred. Please try again later.";
        }

        Log.e(TAG, "Exception: " + errorMessage);
        CustomErrorDialog.mShow(context, errorTitle, errorMessage);
    }
}
