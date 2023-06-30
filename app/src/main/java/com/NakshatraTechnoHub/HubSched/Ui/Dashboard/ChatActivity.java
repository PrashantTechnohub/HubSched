package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Adapters.ChatAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.MessageModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.android.volley.VolleyError;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import pl.droidsonroids.gif.GifImageView;

public class ChatActivity extends  BaseActivity {
    RecyclerView recyclerViewChat;
    private Socket socket;
    private List<MessageModel> messageList;
    EditText msg;
    String meetId, companyId, subject, startTime, endTime, response;

    ChatAdapter chatAdapter;
    ImageView buttonSend, moreBtn;
    MaterialCardView pd;
    GifImageView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        msg = findViewById(R.id.editTextMessage);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        buttonSend = findViewById(R.id.buttonSend);


        pd = findViewById(R.id.pd);
        empty = findViewById(R.id.no_result);


        pd.setVisibility(View.VISIBLE);

        meetId = LocalPreference.get_meetId(this);
        companyId = LocalPreference.get_company_Id(this);

        messageList = new ArrayList<>();

        String getUserId = LocalPreference.get_Id(this);


        chatAdapter = new ChatAdapter(messageList, getUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);

        buttonSend.setOnClickListener(v -> sendMessage());
        findViewById(R.id.back).setOnClickListener(view -> finish());


        getChat();
        startSocketConnection();

    }

    private void getChat() {


        new Receiver(ChatActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null && response.has("result")) {
                    try {
                        JSONArray messagesArray = response.getJSONArray("result");

                        if (messagesArray.length() > 0) {
                            for (int i = 0; i < messagesArray.length(); i++) {
                                JSONObject messageObj = messagesArray.getJSONObject(i);
                                int id = messageObj.getInt("sender_id");
                                String message = messageObj.getString("message");
                                String name = messageObj.getString("sender_name");

                                MessageModel newMessage = new MessageModel(id, message, name);
                                messageList.add(newMessage);
                                pd.setVisibility(View.GONE);
                            }

                            chatAdapter.notifyItemInserted(messageList.size());
                            recyclerViewChat.scrollToPosition(messageList.size() - 1);
                            pd.setVisibility(View.GONE);
                        } else {
                            // Handle case when the messagesArray is empty
                            pd.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        pd.setVisibility(View.GONE);
                        Log.d("TAG", "onResponse: " + e);
                    }
                } else {
                    // Handle case when the response is null or does not contain the "result" key
                    pd.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(VolleyError error) {
                pd.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                ErrorHandler.handleVolleyError(ChatActivity.this, error);
            }
        }).getChat(Integer.parseInt(meetId));

    }


    private void startSocketConnection() {
        try {
            socket = IO.socket(Constant.domain);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.d("Socket", "Connected");

            }).on(Socket.EVENT_DISCONNECT, args -> {
                Log.d("Socket", "Disconnected");

            }).on("chat-" + meetId + "-" + companyId, args -> {

                JSONObject data = (JSONObject) args[0];

                if (data != null && data.has("message")) {
                    try {
                        String message = data.getString("message");
                        int id = data.getInt("sender_id");
                        String name = data.getString("sender_name");

                        MessageModel newMessage = new MessageModel(id, message, name);
                        messageList.add(newMessage);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.notifyItemInserted(messageList.size());
                                recyclerViewChat.smoothScrollToPosition(messageList.size() - 1);
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("ChatError11", "onError: " + e.toString());
                        ErrorHandler.handleException(ChatActivity.this, e);
                    }
                } else {
                    pd.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }

            });

        } catch (URISyntaxException e) {
            Log.d("ChatError11", "onError: " +e.toString());

            ErrorHandler.handleException(ChatActivity.this, e);
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
            ErrorHandler.handleException(ChatActivity.this, e);
            Log.d("ChatError11", "onError: " +e.toString());

        }


        new Receiver(ChatActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {

                if (empty.getVisibility() ==View.VISIBLE){
                    empty.setVisibility(View.GONE);
                }


            }

            @Override
            public void onError(VolleyError error) {
                Log.d("ChatError111", "onError: " +error.toString());

            }
        }).save_sms_to_server(jsonMessage);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
