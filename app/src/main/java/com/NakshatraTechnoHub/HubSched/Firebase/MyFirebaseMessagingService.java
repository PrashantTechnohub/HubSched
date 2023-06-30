package com.NakshatraTechnoHub.HubSched.Firebase;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    private static final String CHANNEL_ID = "channelId";
    private static final String CHANNEL_NAME = "Default channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("message");

            if (hasRequiredPermissions()) {
                showNotification(title, body);
            } else {
                Log.e(TAG, "Required permission is not granted");
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic("test").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Subscribed to topic: test", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to subscribe to topic: test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.INTERNET);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void showNotification(String title, String body) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Date myDate = new Date();
        int myNotificationId = Integer.parseInt(new SimpleDateFormat("ddhhmmss", Locale.US).format(myDate));
        notificationManager.notify(myNotificationId, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        if (token == null || token.isEmpty()) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String tokenX = task.getResult();
                    LocalPreference.store_FirebaseToken(this, tokenX);
                } else {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                }
            });
        } else {
            LocalPreference.store_FirebaseToken(this, token);
        }
    }
}
