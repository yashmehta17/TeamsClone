package com.yashmehta.teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // Code to request camera and mic permission from user
    int Request_Code = 123;

    // Firebase Auth and Firebase user initialization
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    // Firebase Realtime Database
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asking permission for Camera and Mic
        askPermission();

        // Getting current user Instance to check if user has logged in or not
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // Updating user status to online and available for calls
        if (firebaseUser != null){
            String myUid = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(myUid).child("isAvailable").setValue("true");
            databaseReference.child(myUid).child("isOnline").setValue("true");
            databaseReference.child(myUid).child("outgoing").setValue("true");
            databaseReference.child(myUid).child("incoming").setValue("true");
        }

    }

    // Asking Permissions for Camera and Mic
    public void askPermission(){

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS}, Request_Code);

        // Timer to go to next screen from Splash Screen
        handler();
    }


    // Going to next activity from splash screen after 2500 milliseconds
    public void handler(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                // If user logged in -> Go to home screen
                if(firebaseUser != null){

                    // Navigate to Home screen
                    Intent intent = new Intent(MainActivity.this,Home.class);
                    startActivity(intent);

                }

                // If user has not logged in -> Go to login screen
                else {

                    // Navigate to Login screen
                    Intent myIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(myIntent);
                    overridePendingTransition(0, 0);

                }
                finish();
            }
        },2500);
    }
}