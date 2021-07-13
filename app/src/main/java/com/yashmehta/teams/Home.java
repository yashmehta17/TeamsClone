 package com.yashmehta.teams;

/*

    HOME ACTIVITY

    It contains left navigation panel to navigate to other features of app.
    It also has bottom navigation to navigate between chat, call and tasks/notes section

    Web view for video call is also integrated here.
    It contains incoming and outgoing call listener

 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Rational;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yashmehta.teams.databinding.ActivityHomeBinding;



public class Home extends AppCompatActivity {


    // Initializing Variables
    ActivityHomeBinding binding;
    MaterialToolbar materialToolbar;
    DrawerLayout drawerLayout;
    NavigationView leftNavigationView;
    FirebaseUser firebaseUser;
    RelativeLayout relativeLayout;

    // NavigationView Menu Variables Initialized
    View headerView;
    ImageView profilePic;
    TextView userNameTextView;
    TextView userEmailTextView;

    // Incoming Call Screen
    RelativeLayout incomingCallRelativeLayout;
    TextView incomingCallTextView;
    ImageView incomingCallAccept;
    ImageView incomingCallReject;

    // Web View used for Video Call
    RelativeLayout webViewRelativeLayout;
    WebView webView;
    ImageView toggleVideoButton;
    ImageView callEndButton;
    ImageView toggleMicButton;

    // Camera and mic toggle variables
    boolean cameraEnabled = true;
    boolean micEnabled = true;
    boolean onPeerConnected;
    boolean backPressedWhileCall = false;

    // Firebase Realtime Database
    DatabaseReference databaseReference;
    String myUid;
    String callerID;
    String callerName;

    // Picture in Picture Mode while calling
    private PictureInPictureParams.Builder pictureInPictureParams;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Restricted Orientation to portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initializing the UI components to display changes
        materialToolbar = findViewById(R.id.materialToolBar);
        drawerLayout = findViewById(R.id.drawerLayout);
        leftNavigationView = findViewById(R.id.leftNavigationView);
        relativeLayout = findViewById(R.id.relativeLayout);

        headerView = leftNavigationView.getHeaderView(0);
        profilePic = headerView.findViewById(R.id.profilePic);
        userNameTextView = headerView.findViewById(R.id.userNameTextView);
        userEmailTextView = headerView.findViewById(R.id.userEmailTextView);

        incomingCallRelativeLayout = findViewById(R.id.incomingCallRelativeLayout);
        incomingCallTextView = findViewById(R.id.incomingCallTextView);
        incomingCallAccept = findViewById(R.id.incomingCallAccept);
        incomingCallReject = findViewById(R.id.incomingCallReject);

        webViewRelativeLayout = findViewById(R.id.webViewRelativeLayout);
        webView = findViewById(R.id.webView);
        toggleVideoButton = findViewById(R.id.toggleVideoButton);
        callEndButton = findViewById(R.id.callEndButton);
        toggleMicButton = findViewById(R.id.toggleMicButton);

        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();

        // Sets the initial fragment as chat fragment
        setInitialFragment();

        // Sets user status to online and available for calls
        setStatusToOnline();

        // Open's Left Navigation Menu
        leftNavigationDrawer();

        // Left Menu Item Click Listeners
        leftMenuItemClickListener();

        // Bottom Navigation Bar Click Listener
        fragmentClickListener();

        // Outgoing and Incoming Call Observer
        outgoingCall();
        incomingCall();

        // Web View setup for video call
        webViewSetup();

        // Left Navigation Header Setup
        setLeftNavigationHeader();

        // Asking permission for Camera and Mic
        askPermission();


    }

    // Sets user status to online and available for calls
    private void setStatusToOnline(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                // Sets user status to online
                databaseReference.child(myUid).child("isOnline").setValue("true");
            }
        },1500);
    }


    // Open's Left Navigation Menu
    public void leftNavigationDrawer(){

        // Left Navigation bar on clicking
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        materialToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    // Left Menu Item Click Listeners
    public void leftMenuItemClickListener(){

        leftNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        int itemId = item.getItemId();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        Intent intent;
                        switch (itemId){

                            case R.id.profileItem:
                                intent = new Intent(Home.this,Profile.class);
                                startActivity(intent);
                                break;

                            case R.id.motivationItem:
                                intent = new Intent(Home.this,Motivation.class);
                                startActivity(intent);
                                break;

                            case R.id.activitiesItem:
                                intent = new Intent(Home.this,Activities.class);
                                startActivity(intent);
                                break;

                            case R.id.githubRepositoryItem:
                                Uri uriRepo = Uri.parse("https://github.com/yashmehta17/Teams-Clone");
                                intent = new Intent(Intent.ACTION_VIEW, uriRepo);
                                startActivity(intent);
                                break;

                            case R.id.shareItem:
                                Uri uri = Uri.parse("https://github.com/yashmehta17/Teams-Clone/tree/main/APK");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                break;

                            case R.id.logoutItem:
                                logout();
                                break;

                        }
                        return true;
                    }
                }
        );
    }


    // Fragment/Bottom Navigation Menu Item Click Listener
    private void fragmentClickListener(){

        binding.bottomNavigationBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener(){

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                        switch (item.getItemId()){
                            case R.id.ChatMenuItem:
                                fragmentTransaction.replace(R.id.fragmentFrameLayout, new ChatFragment());
                                break;
                            case R.id.CallMenuItem:
                                fragmentTransaction.replace(R.id.fragmentFrameLayout, new CallFragment());
                                break;
                            case R.id.NotesMenuItem:
                                fragmentTransaction.replace(R.id.fragmentFrameLayout, new NotesFragment());
                                break;
                        }
                        fragmentTransaction.commit();

                        return true;
                    }
                }
        );
    }

    // Function for Outgoing Call
    // Changes UI and Database when user calls another user
    public void outgoingCall(){

        databaseReference.child(myUid).child("outgoing").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // if call is accepted -> Starting call
                if (!snapshot.getValue().equals("true") && !snapshot.getValue().equals("false")) {
                    Toast.makeText(Home.this, "Connecting Call", Toast.LENGTH_LONG).show();
                    relativeLayout.setVisibility(View.GONE);
                    webViewRelativeLayout.setVisibility(View.VISIBLE);
                    callerID = snapshot.getValue().toString();
                    getCallerName();
                    startCall(snapshot.getValue().toString());
                }

                // if call is rejected
                else if(snapshot.getValue().equals("false")){
                    Toast.makeText(Home.this, "Call Rejected", Toast.LENGTH_SHORT).show();
                    databaseReference.child(myUid).child("isAvailable").setValue("true");
                    databaseReference.child(myUid).child("outgoing").setValue("true");
                    databaseReference.child(myUid).child("incoming").setValue("true");
                }
            }

            // Error occurred while connecting to database
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function for incoming call
    // Changes the UI and also makes changes to database as
    // user accepts or rejects the call
    public void incomingCall(){

        databaseReference.child(myUid).child("incoming").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Observes Incoming call
                if (!snapshot.getValue().equals("true") && !snapshot.getValue().equals("callEnd")) {
                    callerID = snapshot.getValue().toString();
                    getCallerName();
                    relativeLayout.setVisibility(View.GONE);
                    incomingCallRelativeLayout.setVisibility(View.VISIBLE);

                }

                // Observes if call has ended or not
                if (snapshot.getValue().equals("callEnd")){
                    Toast.makeText(Home.this, "Call Ended", Toast.LENGTH_SHORT).show();
                    webViewRelativeLayout.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);

                    // Ending call
                    callJavascriptFunction("javascript:endCall()");

                    databaseReference.child(myUid).child("isAvailable").setValue("true");
                    databaseReference.child(myUid).child("outgoing").setValue("true");
                    databaseReference.child(myUid).child("incoming").setValue("true");
                    webViewSetup();
                }
            }

            // Error occurred while connecting to database
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Database Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Display name of user who is calling when call received
    public void getCallerName(){

        databaseReference.child(callerID).child("name").addValueEventListener
                (new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callerName = snapshot.getValue().toString();
                        incomingCallTextView.setText(String.format("%s calling..",callerName));
                    }

                    // Error occurred while connecting to database
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Home.this, "Database Error",
                                Toast.LENGTH_SHORT).show();
                    }
        });
    }

    // Button to accept incoming call
    public void setIncomingCallAccept(View view){

        databaseReference.child(callerID).child("outgoing").setValue(myUid);
        incomingCallRelativeLayout.setVisibility(View.GONE);
        webViewRelativeLayout.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Connecting Call. Please Wait",
                Toast.LENGTH_SHORT).show();

    }

    // Button to reject incoming call
    public void setIncomingCallReject(View view){

        databaseReference.child(myUid).child("isAvailable").setValue("true");
        databaseReference.child(myUid).child("outgoing").setValue("true");
        databaseReference.child(myUid).child("incoming").setValue("true");
        databaseReference.child(callerID).child("outgoing").setValue("false");
        incomingCallRelativeLayout.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

    }

    // Button to open chat with call
    public void setOpenChatButton(View view){

        Intent intent= new Intent(Home.this,Message.class);
        intent.putExtra("name", callerName);
        intent.putExtra("uid", callerID);
        intent.putExtra("myUid", myUid);
        intent.putExtra("onCall", "true");
        startActivity(intent);

    }


    // Switch Web Camera on and off
    public void setToggleVideoButton(View view){

        cameraEnabled = !cameraEnabled;
        callJavascriptFunction("javascript:toggleVideo(\""+cameraEnabled+"\")");
        if (cameraEnabled){
            toggleVideoButton.setImageResource(R.drawable.videocam_enabled);
        } else {
            toggleVideoButton.setImageResource(R.drawable.videocam_disabled);
        }

    }

    // Switch Mic on and off
    public void setToggleMicButton(View view){

        micEnabled = !micEnabled;
        callJavascriptFunction("javascript:toggleAudio(\""+micEnabled+"\")");
        if (micEnabled){
            toggleMicButton.setImageResource(R.drawable.mic_enabled);
        } else {
            toggleMicButton.setImageResource(R.drawable.mic_disabled);
        }

    }

    // End an ongoing call
    public void setCallEndButton(View view){

        relativeLayout.setVisibility(View.VISIBLE);
        webViewRelativeLayout.setVisibility(View.GONE);

        // Ending call
        callJavascriptFunction("javascript:endCall()");

        databaseReference.child(myUid).child("isAvailable").setValue("true");
        databaseReference.child(myUid).child("outgoing").setValue("true");
        databaseReference.child(myUid).child("incoming").setValue("true");
        databaseReference.child(callerID).child("incoming").setValue("callEnd");

        // Web View reloaded after call
        webViewSetup();

    }


    // On pressing home button, Screen will enter
    // Picture in Picture if call is in process
    @Override
    protected void onUserLeaveHint() {

        if (webViewRelativeLayout.getVisibility()==View.VISIBLE){
            Rational ratio = new Rational(webViewRelativeLayout.getWidth(),
                    webViewRelativeLayout.getHeight());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pictureInPictureParams = new PictureInPictureParams.Builder();
                pictureInPictureParams.setAspectRatio(ratio);
                enterPictureInPictureMode(pictureInPictureParams.build());
            }
        }
    }

    // Web View setup for Video Call
    public void webViewSetup() {

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JavascriptInterface(),"Android");

        // Loading web page in background
        loadWebPage();

    }

    // Web page loading
    public void loadWebPage() {

        // Declaring path of the page to be loaded
        String filePath = "file:android_asset/call.html";
        webView.loadUrl(filePath);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Initializing peer with unique id to setup video calling
                initializePeer();
            }
        });

    }

    // Calling javascript function to connect to peerjs server
    public void initializePeer(){

        callJavascriptFunction("javascript:init(\""+myUid+"\")");

    }

    // Connecting a user for video call
    public void startCall(String outgoingUid){

        callJavascriptFunction("javascript:startCall(\""+outgoingUid+"\")");

    }

    // To call javascript function from android
    public void callJavascriptFunction(String functionName){

        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(functionName,null);
            }
        });

    }

    // Javascript Interface to call android function from javascript
    public class JavascriptInterface{

        @android.webkit.JavascriptInterface
        public void PeerConnection(){
            onPeerConnected();
        }

    }

    // Function is called from javascript when peer is connected successfully
    public void onPeerConnected(){
        onPeerConnected=true;
    }

    // Sets the initial fragment as chat fragment
    private void setInitialFragment(){
        FragmentTransaction chatTransaction = getSupportFragmentManager().beginTransaction();
        chatTransaction.replace(R.id.fragmentFrameLayout, new ChatFragment());
        chatTransaction.commit();
    }

    // Updating UI of left navigation bar header
    public void setLeftNavigationHeader(){
        userNameTextView.setText(firebaseUser.getDisplayName());
        userEmailTextView.setText(firebaseUser.getEmail());
        String imageUrl = firebaseUser.getPhotoUrl().toString();
        Glide.with(this).load(imageUrl).into(profilePic);
    }

    // Asking Permissions for Camera and Mic
    public void askPermission(){
        ActivityCompat.requestPermissions(Home.this, new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS}, 123);

    }

    // Logout the current logged in user
    private void logout(){

        UserList userList = new UserList(
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                firebaseUser.getUid(),
                "false",
                "false",
                "true",
                "true"
        );
        databaseReference.child(myUid).setValue(userList);

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Home.this,Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (webViewRelativeLayout.getVisibility()==View.VISIBLE){
            onUserLeaveHint();
        }
        else {
            super.onBackPressed();
        }

    }


    // User open this app from recent apps then status of user is set to online
    @Override
    protected void onResume() {
        super.onResume();
        setStatusToOnline();
    }

    // User close this app then online status of user is set to offline
    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.child(myUid).child("isOnline").setValue("false");
    }
}