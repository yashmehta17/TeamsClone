package com.yashmehta.teams;

/*

    MESSAGE ADAPTER

    This is a Recycler View Adapter to display messages.

    It gets an arrayList created in Message Activity.

    It differentiates between sender and receive messages

    It takes the messages in the list and displays updates UI in Message Activity.

 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter {

    // Initializing Variables
    Context context;
    List<MessageModal> messageModalList;
    int SENDER = 0;
    int RECEIVER = 1;

    FirebaseUser firebaseUser;

    // Constructor
    public MessagesAdapter(Context context, List<MessageModal> messageModalList) {
        this.context = context;
        this.messageModalList = messageModalList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Assigns the UI elements for sender and receiver messages
        if (viewType == SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_chat, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Getting message and timestamp from messageModalList
        String message = messageModalList.get(position).getMessage();
        long date = messageModalList.get(position).getDate();

        // Formatting timestamp to display with each message
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM hh:mm aa");
        String timeStamp = dateFormat.format(date);

        // Updating UI to display messages
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.senderTextView.setText(message);
            viewHolder.senderTimeView.setText(timeStamp);

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.receiverTextView.setText(message);
            viewHolder.receiverTimeView.setText(timeStamp);

        }

    }

    // Returns the total number of elements in messageModalList
    @Override
    public int getItemCount() {
        return messageModalList.size();
    }


    // This function returns the type of message,
    // It separates the messages as sender and receiver messages
    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (messageModalList.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return SENDER;
        }
        else
        {
            return RECEIVER;
        }

    }

    // ViewHolder for Sender Messages
    class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderTextView;
        TextView senderTimeView;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderTextView = itemView.findViewById(R.id.senderTextView);
            senderTimeView = itemView.findViewById(R.id.senderTimeView);


        }
    }

    // ViewHolder for Receiver Messages
    class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverTextView;
        TextView receiverTimeView;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverTextView = itemView.findViewById(R.id.receiverTextView);
            receiverTimeView = itemView.findViewById(R.id.receiverTimeView);

        }
    }

}
