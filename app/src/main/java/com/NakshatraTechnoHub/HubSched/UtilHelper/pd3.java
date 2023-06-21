package com.NakshatraTechnoHub.HubSched.UtilHelper;

import static com.NakshatraTechnoHub.HubSched.UtilHelper.pd.isDialogShown;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.app.ProgressDialog;

public class pd3 {
    private static ProgressDialog pd;

    public static void mShow(Context context) {
        mDismiss(); // Dismiss any existing progress dialog

        pd = new ProgressDialog(context);
        pd.setMessage("Loading Please wait..");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        if (isDialogShown) {
            // Dialog is already shown, no need to show it again
            return;
        }


        isDialogShown = true;

    }

    public static void mDismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
            pd = null;
        }
    }

    public static boolean isDialogShown() {
        return isDialogShown;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    public static void handleBackPress(Activity activity, int keyCode) {
        if (isDialogShown && keyCode == KeyEvent.KEYCODE_BACK) {
            mDismiss();
            activity.onBackPressed();
        }
    }
}
