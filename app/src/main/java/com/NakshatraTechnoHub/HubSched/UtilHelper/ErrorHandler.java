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

        if (error instanceof NetworkError) {
            errorMessage = "Network error occurred. Please check your connection.";
            pd.mDismiss();
        } else if (error instanceof ServerError) {
            errorMessage = "Server error occurred: ";
            if (error.networkResponse != null) {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 500) {
                    String serverErrorMessage = new String(error.networkResponse.data);
                    errorMessage += serverErrorMessage;
                    pd.mDismiss();
                } else {
                    errorMessage += "Unexpected response code " + statusCode;
                    pd.mDismiss();
                }
            }
        } else if (error instanceof AuthFailureError) {
            errorMessage = "Authentication failure error occurred. Please try again.";
            pd.mDismiss();
        } else if (error instanceof ParseError) {
            errorMessage = "Error occurred while parsing data. Please try again.";
            pd.mDismiss();
        } else if (error instanceof NoConnectionError) {
            errorMessage = "No connection error occurred. Please check your internet connection.";
            pd.mDismiss();
        } else if (error instanceof TimeoutError) {
            errorMessage = "Request timeout error occurred. Please try again.";
            pd.mDismiss();
        }

        Log.e(TAG, "Volley Error: " + errorMessage);
        CustomErrorDialog.mShow(context, errorMessage);
        pd.mDismiss();
    }

    public static void handleException(Context context, Exception exception) {
        String errorMessage = "An error occurred. Please try again later.";
        String errorTitle = "Error";

        if (exception instanceof NetworkError) {
            errorTitle = "Server Connectivity Error";
            errorMessage = "Server Connectivity error occurred. try again later.";
            pd.mDismiss();
        } else if (exception instanceof ServerError) {
            errorTitle = "Server Error";
            errorMessage = "Server error occurred. Please try again later.";
            pd.mDismiss();
        } else if (exception instanceof AuthFailureError) {
            errorTitle = "Authentication Failure";
            errorMessage = "Authentication failure error occurred. Please try again.";
            pd.mDismiss();
        } else if (exception instanceof ParseError) {
            errorTitle = "Parsing Error";
            errorMessage = "Error occurred while parsing data. Please try again.";
            pd.mDismiss();
        } else if (exception instanceof NoConnectionError) {
            errorTitle = "No Connection";
            errorMessage = "No connection error occurred. Please check your internet connection.";
            pd.mDismiss();
        } else if (exception instanceof TimeoutError) {
            errorTitle = "Timeout Error";
            errorMessage = "Request timeout error occurred. Please try again.";
            pd.mDismiss();

        } else if (exception instanceof JSONException) {
            errorTitle = "JSON Error";
            errorMessage = "JSON error occurred. Please check the data format.";
            pd.mDismiss();

        } else {
            // Generic catch-all case for all other exceptions
            errorMessage = "An unexpected error occurred. Please try again later.";
            pd.mDismiss();

        }

        Log.e(TAG, "Exception: " + errorMessage);
        CustomErrorDialog.mShow(context,  errorMessage);
        pd.mDismiss();

    }
}
