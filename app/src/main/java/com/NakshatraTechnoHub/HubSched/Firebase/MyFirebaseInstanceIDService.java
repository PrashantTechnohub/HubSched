package com.NakshatraTechnoHub.HubSched.Firebase;

import android.util.Log;

import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        if (token == null || token.isEmpty()) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String tokenX = task.getResult();
                    LocalPreference.store_FirebaseToken(this, tokenX);
                } else {
                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                }
            });
        } else {
            LocalPreference.store_FirebaseToken(this, token);
        }
    }
}
