package com.yashmehta.teams;

/*

    MOTIVATION ACTIVITY

    It gets a random motivational quote via Quotable API
    and displays it to the user.

    User can request for another quote.
    User can share the quote with others.

 */


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Motivation extends AppCompatActivity {

    // Initializing Variables
    TextView quoteTextView;
    TextView authorTextView;

    String quote = "";
    String author = "";

    // Json to store data from API
    JsonObjectRequest jsonObjectRequest;
    String url = "https://api.quotable.io/random";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivation);

        // Initializing the UI components to display changes
        quoteTextView = findViewById(R.id.quoteTextView);
        authorTextView = findViewById(R.id.authorTextView);

        // Function to get a new quote
        loadQuote();
    }

    // Gets a new quote from GET Method and displays it to the user
    public void loadQuote(){

        // Using volley for API calls
        queue = Volley.newRequestQueue(this);

        // Storing the output of API call in jsonObject
        // Passing GET method and URL of api to fetch data
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            // Getting quote and author from the API
                            quote = response.getString("content");
                            author = response.getString("author");

                            // Displaying quote and author to the user
                            quoteTextView.setText(quote);
                            authorTextView.setText(String.format("- %s", author));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Motivation.this, "Error Occurred",
                                Toast.LENGTH_SHORT).show();

                    }
                });

        queue.add(jsonObjectRequest);

    }

    // To get another quote when user requests
    public void getAnotherQuote(View view){

        loadQuote();

    }

    // Share the quote through share intent with others
    public void shareButtonPressed(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, quote +"\nBy- "+author);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    // Back button pressed
    public void backButtonPressed(View view){
        onBackPressed();
    }




}