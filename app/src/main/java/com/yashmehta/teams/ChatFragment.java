package com.yashmehta.teams;

/*
    CHAT FRAGMENT

    This fragment gets all the registered user and
    displays it on the screen.

    All except the current signed in user name will be present in the list.

    If a user taps on one of the names displayed here,
    then Message activity will open and users can chat
    with each other.

    User status is shown below every user name as
    Online, Offline, On another call

 */

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    // Initializing Variables
    RecyclerView chatRecyclerView;
    ChatAdapter chatAdapter;
    List<UserList> allUserList;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initializing the UI components to display changes
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        allUserList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        chatAdapter = new ChatAdapter(getActivity(),allUserList);

        // Setting adapter to recycler view
        chatRecyclerView.setAdapter(chatAdapter);

        // Gets all the users from firebase
        getAllUsers();



        return view;
    }



    // Gets and stores users into a list
    public void getAllUsers(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUserList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UserList userListObject = dataSnapshot.getValue(UserList.class);

                    // Store all user except the signed in user on this device
                    if(!userListObject.getUid().equals(firebaseUser.getUid())){
                        allUserList.add(userListObject);
                    }

                }

                // adapter
                chatAdapter.notifyDataSetChanged();

            }

            // Error occurred while connecting to database
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database Error",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


}

