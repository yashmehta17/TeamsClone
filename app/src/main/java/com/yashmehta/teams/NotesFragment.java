package com.yashmehta.teams;

/*
    NOTES FRAGMENT

    This fragment displays all notes/tasks created by user.

    If a user taps on one of the note/task displayed here,
    then user can edit that note/task.

    To delete a note, user needs to longPress that particular note
    and an alert box will appear and will ask for confirmation to delete that note.

    User can set priority of note as = low, moderate and top.

 */

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class NotesFragment extends Fragment {

    // Initializing Variables
    static RecyclerView notesRecyclerView;
    NotesAdapter notesAdapter;
    List<NotesList> allNotesList;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String myUid;

    // Initializing these variables as static because they are used in NotesAdapter also
    static FloatingActionButton addNoteButton;
    static RelativeLayout addNoteLayout;
    static EditText taskEditText;
    ImageView lowPriority,
            moderatePriority,
            topPriority;
    Button noteSaveButton;

    String priority = "low";

    InputMethodManager inputMethodManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        // Initializing the UI components to display changes
        notesRecyclerView = view.findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        addNoteButton = view.findViewById(R.id.addNoteButton);
        addNoteLayout = view.findViewById(R.id.addNoteLayout);
        taskEditText = view.findViewById(R.id.taskEditText);
        lowPriority = view.findViewById(R.id.lowPriority);
        moderatePriority = view.findViewById(R.id.moderatePriority);
        topPriority = view.findViewById(R.id.topPriority);
        noteSaveButton = view.findViewById(R.id.noteSaveButton);

        allNotesList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();


        notesAdapter = new NotesAdapter(getActivity(),allNotesList);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Setting adapter to recycler view
        notesRecyclerView.setAdapter(notesAdapter);

        // Gets all the notes/task from firebase
        getAllNotes();

        // Floating button pressed to add a new note/task
        setAddNoteButton();

        // To save a new note/task created
        setNoteSaveButton();

        // Sets the initial priority of each task as low
        setPriority();

        return view;
    }

    // Gets all the notes/task from firebase
    public void getAllNotes(){

        databaseReference = FirebaseDatabase.getInstance().getReference("Notes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allNotesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NotesList notesList = dataSnapshot.getValue(NotesList.class);

                    if(notesList.getUid().equals(myUid)){

                        allNotesList.add(notesList);

                    }

                }

                // Sorting all notes in order to their priority
                sortNotesList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database Error",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Sorting all notes in order to their priority from top to low
    private void sortNotesList(){

        Collections.sort(allNotesList, new Comparator<NotesList>() {
            @Override
            public int compare(NotesList notesList, NotesList notesList2) {

                String note1 = notesList.getPriority().toLowerCase();
                String note2 = notesList2.getPriority().toLowerCase();

                return note1.compareTo(note2);
            }
        });
        Collections.reverse(allNotesList);
        notesAdapter.notifyDataSetChanged();
    }

    // Floating button pressed to add a new note/task
    public void setAddNoteButton(){

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesRecyclerView.setVisibility(View.GONE);
                addNoteLayout.setVisibility(View.VISIBLE);
                addNoteButton.setVisibility(View.GONE);

            }
        });
    }



    // Function to handle save note/task button
    public void setNoteSaveButton(){

        noteSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Gets the note/task typed by the user
                String task = taskEditText.getText().toString().trim();

                // if task is empty -> returns null
                if (task.isEmpty()){
                    return;
                }

                // else -> saves the note/task to firebase realtime database
                databaseReference = FirebaseDatabase.getInstance().getReference();
                String key = databaseReference.child("Notes").push().getKey();
                NotesList notesList = new NotesList(task,priority,myUid,key);
                databaseReference.child("Notes").child(key).setValue(notesList);

                // If user is editing a previously created note ->
                // previously created note will be deleted
                // and new note will be created with the change applied by the user.
                if (!NotesAdapter.editNoteId.equals("")){
                    databaseReference = FirebaseDatabase.getInstance().getReference("Notes");
                    databaseReference.child(NotesAdapter.editNoteId).removeValue();
                    NotesAdapter.editNoteId = "";
                }

                taskEditText.setText("");

                // Setting the priority as low
                priority = "low";
                lowPriority.setImageResource(R.drawable.done_icon);
                moderatePriority.setImageResource(0);
                topPriority.setImageResource(0);

                notesRecyclerView.setVisibility(View.VISIBLE);
                addNoteLayout.setVisibility(View.GONE);
                addNoteButton.setVisibility(View.VISIBLE);

                // Hiding keyboard after use
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            }
        });

    }

    // While creating the note/task, to set the priority of the note/task
    public void setPriority(){

        // To set priority as low
        lowPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                priority = "low";
                lowPriority.setImageResource(R.drawable.done_icon);
                moderatePriority.setImageResource(0);
                topPriority.setImageResource(0);

            }
        });

        // To set priority as moderate
        moderatePriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                priority = "moderate";
                lowPriority.setImageResource(0);
                moderatePriority.setImageResource(R.drawable.done_icon);
                topPriority.setImageResource(0);

            }
        });

        // To set priority as top
        topPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                priority = "top";
                lowPriority.setImageResource(0);
                moderatePriority.setImageResource(0);
                topPriority.setImageResource(R.drawable.done_icon);

            }
        });

    }

}
