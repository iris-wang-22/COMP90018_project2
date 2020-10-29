package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PersonalActivity extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "FIRE_BASE";
//    private Button SendRequest;
//    private EditText username;

    //private EditText selfUsername;
    private ImageButton iBtn_01;
    private ImageButton iBtn_02;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        iBtn_01 = (ImageButton) findViewById(R.id.p_ib_01);
        iBtn_02 = (ImageButton) findViewById(R.id.p_ib_02);
        setListeners();

    }

    private void setListeners(){
        OnClick onclick = new OnClick();
        iBtn_01.setOnClickListener(onclick);
        iBtn_02.setOnClickListener(onclick);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = null;
            switch (v.getId()){
                case R.id.p_ib_01:
                    intent = new Intent(PersonalActivity.this,MapsActivity.class);
                    break;
                case R.id.p_ib_02:
                    intent = new Intent(PersonalActivity.this, NewProfileActivity.class);
                    break;
            }
            startActivity(intent);
        }
    }
}
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
