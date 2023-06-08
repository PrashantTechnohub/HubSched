package com.NakshatraTechnoHub.HubSched.Adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.MessageModel;
import com.NakshatraTechnoHub.HubSched.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<MessageModel> messageList;
    private String currentUserId;

    public ChatAdapter(List<MessageModel> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            // Outgoing message layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_outgoing_message_item, parent, false);
        } else {
            // Incoming message layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_incomming_message_item, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        holder.messageTextView.setText(message.getMessage());

        // Customize the layout based on the sender and receiver of the message
        if (String.valueOf(message.getId()).equals(currentUserId)) {
            // Current user sent the message
            holder.messageTextView.setBackgroundResource(R.drawable.chat_bubble2);
        } else {
            // Message received from other user
            holder.textViewName.setText(message.getSenderName());
            holder.messageTextView.setBackgroundResource(R.drawable.chat_bubble);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageList.get(position);
        if (String.valueOf(message.getId()).equals(currentUserId)) {
            // Outgoing message
            return 0;
        } else {
            // Incoming message
            return 1;
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, textViewName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textViewMessage);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
}
