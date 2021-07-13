package com.yashmehta.teams;

/*
    CALL FRAGMENT

    This fragment gets all the registered user and
    displays it on the screen.

    All except the current signed in user name will be present in the list.

    If a user taps on one of the names displayed here, then video call will start
    if the user is online.

    If user is offline then toast message will display "User is offline".
    If user is on another call then toast message will display "User is on another call".

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CallFragment extends Fragment {

    // Initializing Variables
    RecyclerView callRecyclerView;
    CallAdapter callAdapter;
    List<UserList> allUserList;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        // Initializing the UI components to display changes
        callRecyclerView = view.findViewById(R.id.callRecyclerView);
        callRecyclerView.setHasFixedSize(true);
        callRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Reference to firebase database
        allUserList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

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

                    // Store all user details except the signed in user on this device
                    if(!userListObject.getUid().equals(firebaseUser.getUid())){
                        allUserList.add(userListObject);
                    }

                }

                // adapter
                callAdapter = new CallAdapter(getActivity(),allUserList);

                // Setting adapter to recycler view
                callRecyclerView.setAdapter(callAdapter);

                // Sorting user list in ascending order.
                sortUserList();
            }

            // Error occurred while connecting to database
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database Error",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Sorting the given array
    private void sortUserList(){

        Collections.sort(allUserList, new Comparator<UserList>() {
            @Override
            public int compare(UserList userList, UserList userList2) {

                String user1 = userList.getName().toLowerCase();
                String user2 = userList2.getName().toLowerCase();

                return user1.compareTo(user2);
            }
        });
        callAdapter.notifyDataSetChanged();
    }


}