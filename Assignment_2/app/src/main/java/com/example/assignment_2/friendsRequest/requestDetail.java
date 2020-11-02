package com.example.assignment_2.friendsRequest;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


public class requestDetail {

    private String requestFrom;
    private String requestTo;
    private String status;
    private String fromAvatar;

    public String getRequestFrom() {
        return requestFrom;
    }
    public void setRequestFrom(String username) {
        this.requestFrom = username;
    }

    public String getRequestTo() {
        return requestTo;
    }
    public void setRequestTo(String username1) {
        this.requestTo = username1;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    public String getFromAvatar() {
        return fromAvatar;
    }
    public void setFromAvatar(String fromAvatar) {
        this.fromAvatar = fromAvatar;
    }

    /*
    public requestDetail() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public requestDetail(String requestFrom, String requestTo, String status) {
        this.requestFrom = requestFrom;
        this.requestTo = requestTo;
        this.status = status;

    }

     */
}
