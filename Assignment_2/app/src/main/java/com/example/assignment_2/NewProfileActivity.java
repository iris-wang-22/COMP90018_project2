package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewProfileActivity extends AppCompatActivity {

    private TextView tv_name;
    private EditText et_age;
    private Button btn_yes;
    private Button btn_no;

    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private String self_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        self_username = getIntent().getStringExtra("username");

        System.out.println("-------------------------------------------");
        System.out.println(self_username);
        System.out.println(databaseRef.getKey());
        System.out.println("-------------------------------------------");

        tv_name = (TextView) findViewById(R.id.new_tv_username);
        tv_name.setText(self_username);

    }
}
//    private DatabaseReference databaseRef;
//    private FirebaseAuth firebaseAuth;
//    private static final String TAG = "FIRE_BASE";
//    private Button SendRequest;
//    private EditText username;
//private EditText selfUsername;

//        final DatabaseReference proRef = databaseRef.child("profile/");
//        final DatabaseReference ageRef = databaseRef.child("profile/age");
//        final DatabaseReference genderRef = databaseRef.child("profile/gender/");
//
//        int x = 25;
//        ageRef.setValue("25");
//        ageRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                tv_01.setText(value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
//
//        tv_02.setText(ageRef.getKey());