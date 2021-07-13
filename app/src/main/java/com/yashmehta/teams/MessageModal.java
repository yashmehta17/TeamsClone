package com.yashmehta.teams;

/*

    MESSAGE MODAL CLASS

    To store messages to firebase
    or to retrieve messages from firebase
    this class is used.
    MessageModal Object is created and used for interacting with firebase

*/


public class MessageModal {


    String message;
    String sender;
    String receiver;
    long date;

    // Empty Constructor
    public MessageModal() {
    }

    // Constructor
    public MessageModal(String message, String sender, String receiver, long date) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
