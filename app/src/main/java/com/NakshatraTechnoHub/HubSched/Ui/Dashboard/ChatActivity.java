package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Adapters.ChatAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Models.MessageModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerViewChat;
    private Socket socket;
    private List<MessageModel> messageList;
    EditText msg;
    String meetId, companyId, subject, startTime, endTime, response;

    ChatAdapter chatAdapter;
    ImageView buttonSend, moreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pd.mShow(this);

        msg = findViewById(R.id.editTextMessage);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        buttonSend = findViewById(R.id.buttonSend);

        Intent intent = getIntent();

        meetId = intent.getStringExtra("meetId");
        companyId = intent.getStringExtra("companyId");

        messageList = new ArrayList<>();

        String getUserId = LocalPreference.get_Id(this);


        chatAdapter = new ChatAdapter(messageList, getUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);

        buttonSend.setOnClickListener(v -> sendMessage());

        getChat();
        startSocketConnection();

    }

    private void getChat() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constant.withToken(Constant.GET_CHAT_URL + "/" + meetId, getApplicationContext()), null,
                response -> {
                    try {
                        JSONArray messagesArray = response.getJSONArray("result");

                        for (int i = 0; i < messagesArray.length(); i++) {
                            JSONObject messageObj = messagesArray.getJSONObject(i);
                            int id = messageObj.getInt("sender_id");
                            String message = messageObj.getString("message");
                            String name = messageObj.getString("sender_name");

                            MessageModel newMessage = new MessageModel(id, message, name);
                            messageList.add(newMessage);
                        }

                        chatAdapter.notifyItemInserted(messageList.size());
                        recyclerViewChat.scrollToPosition(messageList.size() - 1);
                        pd.mDismiss();

                    } catch (JSONException e) {
                        ErrorHandler.handleException(getApplicationContext(), e);
                    }
                },
                error -> {
                    pd.mDismiss();
                    ErrorHandler.handleVolleyError(getApplicationContext(), error);

                });


        VolleySingleton.getInstance(this).addToRequestQueue(request);

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

                if (data.has("message")) {
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
                                pd.mDismiss();
                            }
                        });

                        pd.mDismiss();

                    } catch (JSONException e) {
                        pd.mDismiss();
                        ErrorHandler.handleException(getApplicationContext(), e);
                    }

                }
            });

        } catch (URISyntaxException e) {
            pd.mDismiss();
            ErrorHandler.handleException(getApplicationContext(), e);
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
            ErrorHandler.handleException(getApplicationContext(), e);

        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CHAT_URL, getApplicationContext()), jsonMessage, response -> Log.d("MSG11", "onResponse: Done"), error -> {
            ErrorHandler.handleVolleyError(getApplicationContext(), error);

        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
