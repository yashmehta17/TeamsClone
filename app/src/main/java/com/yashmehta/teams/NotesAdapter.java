package com.yashmehta.teams;

/*

    NOTES ADAPTER

    This is a Recycler View Adapter to display notes/tasks.

    It gets an arrayList created in notes fragment of all notes/tasks created by user.
    It takes the items in the list and displays updates UI in notes fragment.

    It also handles clickListener for items displayed in notes fragment.
    When user clicks any of the tasks/notes created,
    then user can edit that task.

    If user long press a task then alert message will appear to delete that task/note.

 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    // Initializing Variables
    Context context;
    List<NotesList> notesList;
    DatabaseReference databaseReference;

    static String editNoteId = "";

    // Constructor
    public NotesAdapter(Context context, List<NotesList> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item,parent,false);

        return new NotesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // Getting note/task from notesList to display in notes Fragment
        String task = notesList.get(position).getTask();
        String priority = notesList.get(position).getPriority();
        String noteId = notesList.get(position).getNoteId();


        // Updating UI
        holder.taskTextView.setText(task);

        if (priority.equals("low")){
            holder.priorityImageView.setImageResource(R.drawable.circle_green);
        }
        else if (priority.equals("moderate")){
            holder.priorityImageView.setImageResource(R.drawable.circle_yellow);
        }
        else {
            holder.priorityImageView.setImageResource(R.drawable.circle_red);
        }


        // Click handler to edit the note/task
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotesFragment.notesRecyclerView.setVisibility(View.GONE);
                NotesFragment.addNoteLayout.setVisibility(View.VISIBLE);
                NotesFragment.addNoteButton.setVisibility(View.GONE);
                NotesFragment.taskEditText.setText(task);
                editNoteId = noteId;
            }
        });

        // Long Click handler to delete the task/note
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // Showing alert dialogue to confirm deletion of task/note
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Task")
                        .setMessage("Do you want to delete this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseReference = FirebaseDatabase.getInstance().getReference("Notes");
                                databaseReference.child(noteId).removeValue();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();

                return true;
            }
        });

    }

    // Returns the size of Array
    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // View holder for note/task
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView taskTextView;
        ImageView priorityImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTextView = itemView.findViewById(R.id.taskTextView);
            priorityImageView = itemView.findViewById(R.id.priorityImageView);

        }
    }
}
