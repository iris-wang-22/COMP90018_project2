package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friends extends AppCompatActivity {
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        List<String> data = new ArrayList<>();
        data.add("friend1");
        data.add("friend2");
        data.add("friend3");
        Map<String, Double> coordinate = new HashMap<String, Double>();
        coordinate.put("lattitude", 0.0);
        coordinate.put("landtitude", 0.12);

        //databaseRef.child("friends").child("user1").setValue(data);
        databaseRef.child("coordinates").child("user1").setValue(coordinate);
        Toast.makeText(Friends.this, "Save successful!!", Toast.LENGTH_SHORT).show();
    }


}