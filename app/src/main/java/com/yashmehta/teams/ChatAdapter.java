package com.yashmehta.teams;

/*

    CHAT ADAPTER

    This is a Recycler View Adapter.

    It gets an arrayList created in chat fragment of all users.
    It takes the items in the list and displays updates UI in chat fragment
    to display names and status of all users.

    It also handles clickListener for items displayed in chat fragment.
    When user clicks username of another user displayed,
    Message activity is started where user can chat with each other

 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;



public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {


    // Initializing Variables
    Context context;
    List<UserList> userList;
    FirebaseUser firebaseUser;
    String myUid;

    // Constructor
    public ChatAdapter(Context context, List<UserList> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // Getting variables from ArrayList into separate Variables
        String userName = userList.get(position).getName();
        String status = userList.get(position).getIsOnline();
        String uid = userList.get(position).getUid();
        String isAvailable = userList.get(position).getIsAvailable();

        holder.userNameTextViewChat.setText(userName);

        // Setting user status
        if (status.equals("true") && isAvailable.equals("true")){
            holder.userStatusTextViewChat.setText("Online");
            holder.userStatusImageChat.setImageResource(R.drawable.online_dot);
        }
        else if (status.equals("false")){
            holder.userStatusTextViewChat.setText("Offline");
            holder.userStatusImageChat.setImageResource(R.drawable.offline_dot);
        } else {
            holder.userStatusTextViewChat.setText("On another call");
            holder.userStatusImageChat.setImageResource(R.drawable.on_call_dot);
        }


        // Chat item click listener to open chat with respective person
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                myUid = firebaseUser.getUid();
                Intent intent = new Intent(context, Message.class);
                intent.putExtra("name", userName);
                intent.putExtra("uid", uid);
                intent.putExtra("myUid", myUid);
                intent.putExtra("onCall", "false");
                context.startActivity(intent);

            }
        });

    }


    // Returns the size of Array
    @Override
    public int getItemCount() {
        return userList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userNameTextViewChat;
        TextView userStatusTextViewChat;
        ImageView userStatusImageChat;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTextViewChat = itemView.findViewById(R.id.userNameTextViewChat);
            userStatusTextViewChat = itemView.findViewById(R.id.userStatusTextViewChat);
            userStatusImageChat = itemView.findViewById(R.id.userStatusImageChat);

        }
    }


}
