package com.NakshatraTechnoHub.HubSched.Ui.StartActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.NakshatraTechnoHub.HubSched.R;

public class SplashScreen extends AppCompatActivity {

    Handler h =  new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//
        ///
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        h.postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        },3700);
    }
}