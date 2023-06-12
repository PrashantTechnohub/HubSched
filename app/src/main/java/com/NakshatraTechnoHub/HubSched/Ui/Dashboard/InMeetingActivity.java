package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Adapters.ChatAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Models.MessageModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.QRCodeGeneratorUtil;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
    private List<MessageModel> messageList;
    EditText msg;
    ChatAdapter chatAdapter;
    ImageView buttonSend, moreBtn;
    TextView timeLeft, subjectView;
    RecyclerView recyclerViewChat;
    String meetId, companyId, subject, startTime, endTime,response;

    //__________________________________
    ImageView qrCodeView;
    RelativeLayout inMeetingLayout, qrCodeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_meeting);

        timeLeft = findViewById(R.id.time_left);
        msg = findViewById(R.id.editTextMessage);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        buttonSend = findViewById(R.id.buttonSend);
        subjectView = findViewById(R.id.subject);
        qrCodeView = findViewById(R.id.qr_code_image);
        moreBtn = findViewById(R.id.more_btn);

        inMeetingLayout = findViewById(R.id.InMeetingRL);
        qrCodeLayout = findViewById(R.id.qrCodeRL);



        messageList = new ArrayList<>();

        String getUserId = LocalPreference.get_Id(InMeetingActivity.this);

        chatAdapter = new ChatAdapter(messageList, getUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);

        Intent intent = getIntent() ;
        meetId = intent.getStringExtra("meetId");
        companyId = intent.getStringExtra("companyId");
        subject = intent.getStringExtra("subject");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        response = getIntent().getStringExtra("response");


        subjectView.setText(subject);

        buttonSend.setOnClickListener(v -> sendMessage());
        moreBtn.setOnClickListener(view -> moreOptions());
        generateQrCode();
        startSocketConnection();
        startCountdownTimer( endTime, timeLeft);
    }

    private void startCountdownTimer(String endTime, final TextView timeLeftTextView) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date endDate = format.parse(endTime);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(System.currentTimeMillis());

            // Set the date part of the current time to match the end time
            currentCalendar.set(Calendar.YEAR, endCalendar.get(Calendar.YEAR));
            currentCalendar.set(Calendar.MONTH, endCalendar.get(Calendar.MONTH));
            currentCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH));

            if (currentCalendar.after(endCalendar)) {
                timeLeftTextView.setText("End time has already passed.");
                return;
            }

            long endTimeMillis = endCalendar.getTimeInMillis();
            long currentTimeMillis = currentCalendar.getTimeInMillis();

            long remainingTimeMillis = endTimeMillis - currentTimeMillis;

            CountDownTimer countDownTimer = new CountDownTimer(remainingTimeMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = (seconds / 60) % 60;
                    long hours = (seconds / 3600) % 24;

                    String remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d sec",
                            hours, minutes, seconds);

                    if (minutes >= 60) {
                        long totalMinutes = minutes + (hours * 60);
                        remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d sec",
                                totalMinutes / 60, totalMinutes % 60, seconds);
                    }

                    if (seconds >= 60) {
                        long totalSeconds = seconds + (minutes * 60) + (hours * 3600);
                        remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d sec",
                                totalSeconds / 3600, (totalSeconds / 60) % 60, totalSeconds % 60);
                    }

                    timeLeftTextView.setText(remainingTime);
                }

                @Override
                public void onFinish() {
                    // Countdown timer finished, handle any necessary actions
                    timeLeftTextView.setText("Time's up!");
                }
            };

            countDownTimer.start();
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle any parsing errors
        }
    }


    private void moreOptions() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(InMeetingActivity.this, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater) InMeetingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View team = inflater1.inflate(R.layout.cl_in_meeting_more_option, null);
        builder.setView(team);

        TextView showQrBtn = team.findViewById(R.id.show_qr_menu_btn);
        TextView panetryBtn = team.findViewById(R.id.panetry_menu_btn);

        AlertDialog dialog = builder.create();
        dialog.show();



        showQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inMeetingLayout.setVisibility(View.GONE);
                qrCodeLayout.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        panetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InMeetingActivity.this, PantryOrderActivity.class);
                intent.putExtra("meetId", meetId);
                intent.putExtra("companyId", companyId);

                startActivity(intent);
                dialog.dismiss();
            }
        });





    }

    private void generateQrCode() {

        if (this.response != null){
            Bitmap code =  QRCodeGeneratorUtil.generateQRCode(this.response);
            qrCodeView.setImageBitmap(code);
        }else {
            Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show();
        }

    }

    private void startSocketConnection() {
        try {
            socket = IO.socket("http://192.168.0.182:5000/");
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.d("Socket", "Connected");

            }).on(Socket.EVENT_DISCONNECT, args -> {
                Log.d("Socket", "Disconnected");
                
            }).on("chat-" + meetId + "-" + companyId, args -> {

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
            }).on("qr-verification",args -> {
                
                qrCodeLayout.setVisibility(View.GONE);
                inMeetingLayout.setVisibility(View.VISIBLE);
                
                
            });

        } catch (URISyntaxException e) {
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
            jsonMessage.put("meetId", meetId);
            jsonMessage.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CHAT_URL, getApplicationContext()), jsonMessage, response -> Log.d("MSG11", "onResponse: Done"), error -> Log.d("MSG11", "onResponse: " + error.getMessage()));

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}