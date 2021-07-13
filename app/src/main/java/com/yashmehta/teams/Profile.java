package com.yashmehta.teams;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    // Declaring firebase user variable to get logged in user details
    FirebaseUser firebaseUser;

    // Image and text view to display user details
    CircleImageView profileImage;
    TextView profileUserName,
            profileEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Getting current user details
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initializing the UI components to display changes
        profileImage = findViewById(R.id.profileImage);
        profileUserName = findViewById(R.id.profileUserName);
        profileEmail = findViewById(R.id.profileEmail);

        // Function to retrieve and display user details
        getUserDetails();

    }


    // Getting user details from firebase auth and displaying on profile page
    private void getUserDetails() {

        // Loading user image
        String imageUrl = firebaseUser.getPhotoUrl().toString();
        Glide.with(this).load(imageUrl).into(profileImage);

        // Loading user name and email
        profileUserName.setText(String.format("Name: %s", firebaseUser.getDisplayName()));
        profileEmail.setText(String.format("Email: %s", firebaseUser.getEmail()));
    }

    // On Back button pressing
    public void backPressed(View view){
        onBackPressed();
    }

}