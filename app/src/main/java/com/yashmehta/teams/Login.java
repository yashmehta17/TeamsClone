package com.yashmehta.teams;

/*

    LOGIN ACTIVITY

    It uses Sign In with google
    User can sign in with google

 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    // Initializing Variables
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;


    // Check if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        // if user already logged in -> navigate to home screen
        if(firebaseUser != null){
            Intent intent = new Intent(Login.this,Home.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Google Sign In Requested
        createRequest();
        mAuth = FirebaseAuth.getInstance();

        // Check if permissions for camera and mic are granted or ask again
        checkPermission();

    }

    // Checks if permissions for camera and mic are granted or ask again
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission
             (Login.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                ||
            ContextCompat.checkSelfPermission
              (Login.this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
        {

            // Asking permissions again if permission not granted
            ActivityCompat.requestPermissions(Login.this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.MODIFY_AUDIO_SETTINGS}, 123);
        }
    }

    // Google Sign In Requested
    public void createRequest(){

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Returns the gmail account selected by the user to log in
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Signing in with credential provided by user
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            onSuccessfulSignIn();

                        } else {

                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Sorry, Authentication Failed",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    // On Successful Sign In user details saved to database
    public void onSuccessfulSignIn(){

        FirebaseUser user = mAuth.getCurrentUser();

        // Saving User details to firebase realtime database
        UserList userList = new UserList(
                user.getDisplayName(),
                user.getEmail(),
                user.getUid(),
                "true",
                "true",
                "true",
                "true"
        );

        FirebaseDatabase.getInstance().getReference("Users")
                .child(user.getUid())
                .setValue(userList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });

        // Navigating to Home activity
        Intent intent = new Intent(Login.this,Home.class);
        startActivity(intent);
        finish();
    }

}