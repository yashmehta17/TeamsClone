package com.yashmehta.teams;

/*

    CALL ADAPTER

    This is a Recycler View Adapter to display list of all registered users.

    It gets an arrayList created in Call Fragment.

    On Clicking the name of another user,
    if another user is offline -> Toast message will be displayed that user is offline
    else if another user is on another call -> Toast message saying user is on another call
    else if another user is online -> That user will be called,
    and if he accepts the call, call would be connected.

 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.MyViewHolder> {

    // Initializing Variables
    Context context;
    List<UserList> userList;
    FirebaseUser firebaseUser;
    String myUid;

    // Constructor to get activity context and UserList
    public CallAdapter(Context context, List<UserList> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.call_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // Initializing Variables to update the UI
        String userName = userList.get(position).getName();
        String status = userList.get(position).getIsOnline();
        String uid = userList.get(position).getUid();
        String isAvailable = userList.get(position).getIsAvailable();

        // Setting name of user
        holder.userNameTextView.setText(userName);

        // Setting status of user
        if (status.equals("true") && isAvailable.equals("true")){
            holder.userStatusTextView.setText("Online");
            holder.userStatusImage.setImageResource(R.drawable.online_dot);
        }
        else if (status.equals("false")){
            holder.userStatusTextView.setText("Offline");
            holder.userStatusImage.setImageResource(R.drawable.offline_dot);
        } else {
            holder.userStatusTextView.setText("On another call");
            holder.userStatusImage.setImageResource(R.drawable.on_call_dot);
        }

        // When a user makes a call to another user
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If user is offline -> Display Toast that user is offline
                // Else if on another call -> Display Toast that user is on another call
                if (!status.equals("true")){
                    Toast.makeText(context, "User is offline", Toast.LENGTH_SHORT).show();
                }
                else if (!isAvailable.equals("true") && status.equals("true")){
                    Toast.makeText(context, "User on another call", Toast.LENGTH_SHORT).show();
                }


                // If user is online and not on another call
                if (status.equals("true") && isAvailable.equals("true")){

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    myUid = firebaseUser.getUid();
                    DatabaseReference databaseReference =
                            FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(uid).child("incoming").setValue(myUid);
                    databaseReference.child(uid).child("isAvailable").setValue("false");
                    databaseReference.child(myUid).child("isAvailable").setValue("false");
                    Toast.makeText(context, "Calling "+userName, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    // Returns the size of the list
    @Override
    public int getItemCount() {
        return userList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userNameTextView;
        TextView userStatusTextView;
        ImageView userStatusImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userStatusTextView = itemView.findViewById(R.id.userStatusTextView);
            userStatusImage = itemView.findViewById(R.id.userStatusImage);

        }
    }

}
