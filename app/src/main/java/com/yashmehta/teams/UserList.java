package com.yashmehta.teams;

public class UserList {

    // Initializing variables
    public String name, email, uid,
            isOnline, isAvailable,
            incoming, outgoing;

    // Empty Constructor
    public UserList(){

    }

    // Constructor
    public UserList(String name, String email, String uid, String isOnline, String isAvailable, String incoming, String outgoing) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.isOnline = isOnline;
        this.isAvailable = isAvailable;
        this.incoming = incoming;
        this.outgoing = outgoing;
    }

    //    Getter and Setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIncoming() {
        return incoming;
    }

    public void setIncoming(String incoming) {
        this.incoming = incoming;
    }

    public String getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(String outgoing) {
        this.outgoing = outgoing;
    }
}
