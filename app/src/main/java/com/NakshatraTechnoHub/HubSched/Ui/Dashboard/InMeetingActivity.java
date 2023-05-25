package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.NakshatraTechnoHub.HubSched.Adapters.MessageAdapter;
import com.NakshatraTechnoHub.HubSched.R;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.socket.client.Socket;

public class InMeetingActivity extends AppCompatActivity {

    private Socket socket;
    private CountDownTimer countDownTimer;

    private List<String> messages;
    private MessageAdapter messageAdapter;

    TextView timeLeft;
    RecyclerView recyclerViewChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_meeting);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);

        timeLeft = findViewById(R.id.time_left);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        RecyclerView recyclerViewChat = findViewById(R.id.recyclerViewChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // Set reverse layout
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(messageAdapter);


        startCountdownTimer(30 * 60 * 1000); // Start the countdown timer with 30 minutes duration

        try {
            socket = IO.socket("http://your_server_url"); // Replace with your server URL
            socket.connect();

            // Set up event listeners for receiving messages
            socket.on("chat message", onChatMessageReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ImageView buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }


    private void startCountdownTimer(long durationMillis) {
        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = millisUntilFinished / 1000 % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

                timeLeft.setText("Left Time : " +timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                timeLeft.setText("00:00");
                // Perform any actions when the timer finishes
            }
        };

        countDownTimer.start();
    }

    private Emitter.Listener onChatMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String message = (String) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messages.add(message);
                    messageAdapter.notifyDataSetChanged();
                    recyclerViewChat.scrollToPosition(messages.size() - 1);

                }
            });
        }
    };

    private void sendMessage() {
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        String message = editTextMessage.getText().toString().trim();

        if (!message.isEmpty()) {
//            socket.emit("chat message", message);
//            editTextMessage.setText("");

            messages.add(message);
            messageAdapter.notifyDataSetChanged();
            recyclerViewChat.scrollToPosition(messages.size() - 1);
            editTextMessage.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}