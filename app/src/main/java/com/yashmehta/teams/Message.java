package com.yashmehta.teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message extends AppCompatActivity {

    // Initializing Variables
    MaterialToolbar chatToolBar;
    RecyclerView chattingRecyclerView;
    List<MessageModal> messageModalList;
    MessagesAdapter messagesAdapter;

    TextView chattingUserName;
    TextView chattingStatus;
    TextView returnToMeeting;

    ImageView chattingStatusDot;

    String receiverUserName;
    String receiverUid;
    String myUid;
    String onCall;

    EditText messageEditText;
    CardView sendButton;

    // Firebase Realtime Database
    DatabaseReference databaseReferenceUsers;
    DatabaseReference databaseReferenceMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Initializing the UI components to display changes
        chatToolBar = findViewById(R.id.chatToolBar);
        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        chattingUserName = findViewById(R.id.chattingUserName);
        chattingStatus = findViewById(R.id.chattingStatus);
        chattingStatusDot = findViewById(R.id.chattingStatusDot);

        returnToMeeting = findViewById(R.id.returnToMeeting);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        receiverUserName = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        myUid = getIntent().getStringExtra("myUid");
        onCall = getIntent().getStringExtra("onCall");

        // Recycler View to display messages between users
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(linearLayoutManager);

        // Getting database reference for Users and Messages
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceMessages = FirebaseDatabase.getInstance().getReference("Messages");

        // ArrayList of messages between users
        messageModalList = new ArrayList<>();

        // Sets the user name of receiver
        chattingUserName.setText(receiverUserName);

        // Gets all messages between two users
        getAllMessage();

        // Sets logged in user status to online
        setStatusToOnline();

        // Sets user status to online/offline or on another call
        setUserStatus();

        // Back button pressed present on tool bar
        backButtonPressed();

        // Check if user is on call or not
        checkOnCall();

        // Check if user is available to receive calls
        checkAvailability();


    }

    // Gets all messages between two users
    public void getAllMessage(){

        // Adding all messages between two users to messageModalList
        databaseReferenceMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModalList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MessageModal messages = dataSnapshot.getValue(MessageModal.class);

                    if(messages.getSender().equals(myUid) && messages.getReceiver().equals(receiverUid)
                            || messages.getSender().equals(receiverUid) && messages.getReceiver().equals(myUid)){

                        messageModalList.add(messages);

                    }

                }

                if (messageModalList.size()>0){

                    // if messages are present between two users,
                    // then messagesAdapter is notified to update the UI
                    messagesAdapter = new MessagesAdapter(Message.this, messageModalList);
                    chattingRecyclerView.setAdapter(messagesAdapter);
                    messagesAdapter.notifyDataSetChanged();

                }

            }

            // Error occurred while connecting to database
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Message.this, "Database Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Sets logged in user status to online
    private void setStatusToOnline(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                // Sets user status to online
                databaseReferenceUsers.child(myUid).child("isOnline").setValue("true");

            }
        },1500);
    }



    // Sets the receiver status to online/offline/on another call
    private void setUserStatus(){

        databaseReferenceUsers.child(receiverUid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserList userList = snapshot.getValue(UserList.class);

                        String status = userList.getIsOnline();
                        String isAvailable = userList.getIsAvailable();
                        if (status.equals("true") && isAvailable.equals("true")){
                            chattingStatus.setText("Online");
                            chattingStatusDot.setImageResource(R.drawable.online_dot);
                        }
                        else if (status.equals("false")){
                            chattingStatus.setText("Offline");
                            chattingStatusDot.setImageResource(R.drawable.offline_dot);
                        } else {
                            chattingStatus.setText("On another call");
                            chattingStatusDot.setImageResource(R.drawable.on_call_dot);
                        }
                    }

                    // Error occurred while connecting to database
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Message.this, "Database Error",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }


    // Send button click listener updates the database with message sent
    public void setSendButton(View view){

        // Getting the message typed by user
        String message = messageEditText.getText().toString().trim();

        // Checking if its empty
        if (message.isEmpty()){
            return;
        }

        // if message not empty then creating a messageModal item to store message to firebase
        messageEditText.setText("");
        Date date = new Date();
        MessageModal messageModal = new MessageModal(message,myUid,receiverUid,date.getTime());

        // Storing message to firebase
        DatabaseReference databaseReferenceNewMessage = FirebaseDatabase.getInstance().getReference();
        databaseReferenceNewMessage.child("Messages").push().setValue(messageModal);

    }

    // Checks if a user is already on a call
    public void checkOnCall(){

        if (onCall.equals("true")){
            // Updating UI of top bar to return to meeting
            returnToMeeting.setVisibility(View.VISIBLE);
        }

        else {

            // Updating UI of top bar to return to meeting
            returnToMeeting.setVisibility(View.GONE);

            // Function to receive incoming call
            incomingCall();
        }

    }

    // Function called when return to meeting pressed to go back to meeting
    public void setReturnToMeeting(View view){
        onBackPressed();
    }

    // Check if user is available to receive calls
    public void checkAvailability(){

        databaseReferenceUsers.child(myUid).child("isAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue().equals("true")){
                    incomingCall();
                    onCall = "false";
                    returnToMeeting.setVisibility(View.GONE);
                }
            }

            // Error occurred while connecting to database
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Message.this, "Database Error",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Function to receive incoming call
    public void incomingCall(){

        if (onCall.equals("false")) {
            databaseReferenceUsers.child(myUid).child("incoming").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Observes Incoming call
                    if (!snapshot.getValue().equals("true") && !snapshot.getValue().equals("callEnd")) {
                        Intent intent = new Intent(Message.this, Home.class);
                        startActivity(intent);
                    }

                }

                // Error occurred while connecting to database
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Message.this, "Database Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    // Back button pressed present on tool bar
    public void backButtonPressed(){
        chatToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    // User open this app from recent apps then status of user is set to online
    @Override
    protected void onResume() {
        super.onResume();
        databaseReferenceUsers.child(myUid).child("isOnline").setValue("true");
    }

    // User close this app then online status of user is set to offline
    @Override
    protected void onStop() {
        super.onStop();
        databaseReferenceUsers.child(myUid).child("isOnline").setValue("false");
    }


}