package com.example.assignment_2;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class requestDetail {

    public String requestFrom;
    public String requestTo;
    public String status;

    public requestDetail() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public requestDetail(String requestFrom, String requestTo, String status) {
        this.requestFrom = requestFrom;
        this.requestTo = requestTo;
        this.status = status;

    }
}
