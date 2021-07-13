package com.yashmehta.teams;

public class NotesList {

    // Initializing variables
    String task, priority, uid, noteId;

    // Empty constructor
    public NotesList(){

    }

    // Constructor
    public NotesList(String task, String priority, String uid, String noteId) {
        this.task = task;
        this.priority = priority;
        this.uid = uid;
        this.noteId = noteId;
    }

    // Getters and setters
    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
}
