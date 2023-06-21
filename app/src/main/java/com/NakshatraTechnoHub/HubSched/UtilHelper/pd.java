package com.NakshatraTechnoHub.HubSched.UtilHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.NakshatraTechnoHub.HubSched.R;

public class pd {
    private static Dialog dialog;
    public static boolean isDialogShown = false;

    public static void mShow(Context context) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cl_loading_layout);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setDimAmount(0.3f); // Adjust the transparency level as desired (0.0f - fully transparent, 1.0f - fully opaque)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // Add this line

            // Set the status bar color to match the dialog's background color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.background_page));
            }
        }

        dialog.show();

        if (isDialogShown) {
            // Dialog is already shown, no need to show it again
            return;
        }


        isDialogShown = true;
    }
    public static boolean isDialogShown() {
        return isDialogShown;
    }
    public static void mDismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        isDialogShown = false;
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
