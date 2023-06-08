package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Adapters.ChatAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.MessageModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;

public class InMeetingActivity extends AppCompatActivity {

    private Socket socket;
    private CountDownTimer countDownTimer;
    private List<MessageModel> messageList;

    EditText msg;
     ChatAdapter chatAdapter;
    ImageView buttonSend;
    TextView timeLeft, subjectView;
    RecyclerView recyclerViewChat;

    String meetId, companyId, subject, startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_meeting);


        timeLeft = findViewById(R.id.time_left);
        msg = findViewById(R.id.editTextMessage);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        buttonSend = findViewById(R.id.buttonSend);
        subjectView = findViewById(R.id.subject);

        messageList = new ArrayList<>();

        String getUserId = LocalPreference.get_Id(InMeetingActivity.this);

        chatAdapter = new ChatAdapter(messageList, getUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true); // Set reverse layout
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);
        Intent intent = getIntent();


        meetId = intent.getStringExtra("meetId");
        companyId = intent.getStringExtra("companyId");
        subject = intent.getStringExtra("subject");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        subjectView.setText(subject);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        startCountdownTimer(startTime, endTime); // Start the countdown timer with 30 minutes duration

        try {
            socket = IO.socket("http://192.168.0.182:5000/");
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, args -> {
                // Connected to the server
                Log.d("Socket", "Connected");

            }).on(Socket.EVENT_DISCONNECT, args -> {
                // Disconnected from the server
                Log.d("Socket", "Disconnected");


            }).on("chat-" +meetId +"-"+ companyId, args -> {

                JSONObject data = (JSONObject) args[0];

                if (data.has("message")) {
                    try {
                        String message = data.getString("message");
                        int id = data.getInt("sender_id");
                        String name = data.getString("sender_name");

                        MessageModel newMessage = new MessageModel(id, message, name);

                        messageList.add(newMessage);


                        runOnUiThread(() -> {
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerViewChat.smoothScrollToPosition(messageList.size() - 1);
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }






    private void startCountdownTimer(String startTimeString, String endTimeString) {

// Define the date format for parsing the time strings
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        try {
            // Parse the start time string
            Date startTime = timeFormat.parse(startTimeString);

            // Parse the end time string
            Date endTime = timeFormat.parse(endTimeString);

            // Get the current time
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();

            // Calculate the remaining time in milliseconds
            long remainingTimeMillis = endTime.getTime() - currentTime.getTime();

            // Create a CountDownTimer for the remaining time
            new CountDownTimer(remainingTimeMillis, 1000) {
                public void onTick(long millisUntilFinished) {
                    long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    String remainingTime = String.format(Locale.getDefault(), "%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60);
                    timeLeft.setText(remainingTime);
                }

                public void onFinish() {
                    timeLeft.setText("00:00");
                }
            }.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = msg.getText().toString().trim();
        if (!message.isEmpty()) {
            saveMessageToServer(message);
            msg.setText("");
        }
    }

    private void saveMessageToServer(String message) {

        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("meetId",meetId );
            jsonMessage.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CHAT_URL, getApplicationContext()), jsonMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MSG11", "onResponse: Done");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MSG11", "onResponse: " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}