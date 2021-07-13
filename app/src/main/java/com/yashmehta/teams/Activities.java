package com.yashmehta.teams;

/*

    ACTIVITIES ACTIVITY

    It gets a random activity via Bored API
    and displays it to the user.

    User can request for another activity.
    User can share the activity with others.
    User can also add this activity as a task for himself,
    which will be shown in task/notes section.

 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class Activities extends AppCompatActivity {


    // Initializing Variables
    TextView activityTextView;

    String activity = "";

    // Json to store data from API
    JsonObjectRequest jsonObjectRequest;
    String url = "https://www.boredapi.com/api/activity";
    RequestQueue queue;

    // Firebase realtime database variables
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String myUid;

    // Boolean to check if a activity is already added in the task
    boolean alreadyAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        // Text view to display the activity
        activityTextView = findViewById(R.id.activityTextView);

        // Loading and fetching the json data to display the activity
        loadActivity();
    }

    // loadActivity to fetch data of API with volley
    public void loadActivity() {

        queue = Volley.newRequestQueue(this);

        // Passing GET method and URL of api to fetch data
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    // Displaying data to user after it is fetched
                                    activity = response.getString("activity");
                                    activityTextView.setText(activity);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Activities.this, "Error Occurred",
                                Toast.LENGTH_SHORT).show();

                    }
                });

        queue.add(jsonObjectRequest);
    }


    // To get a new random activity
    public void getAnotherActivity(View view){

        loadActivity();
        alreadyAdded = false;

    }

    // Adding activity to tasks
    public void addActivityToTask(View view){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();

        // Creating a object and saving it to database
        if (!activity.equals("") && !alreadyAdded) {

            databaseReference = FirebaseDatabase.getInstance().getReference();
            String key = databaseReference.child("Notes").push().getKey();
            NotesList notesList = new NotesList(activity, "moderate", myUid, key);
            databaseReference.child("Notes").child(key).setValue(notesList);
            alreadyAdded = true;

            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();

        }

        // If the displayed activity is already added then it wont be added again
        else {

            Toast.makeText(this, "Already Added", Toast.LENGTH_SHORT).show();

        }

    }

    // Share the activity with others
    public void activityShareButtonPressed(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, activity) ;
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    // Back button pressed
    public void activityBackButtonPressed(View view){
        onBackPressed();
    }

}