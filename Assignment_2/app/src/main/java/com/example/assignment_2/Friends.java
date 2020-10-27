package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Friends extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private Button SendRequest;
    private EditText username;

    private String self_username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        //
        List<String> data = new ArrayList<>();
        data.add("friend1");
        data.add("friend2");
        data.add("friend3");
        databaseRef.child("friends").child("user1").setValue(data);
        Toast.makeText(Friends.this, "Save successful!!", Toast.LENGTH_SHORT).show();
        //


        firebaseAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.SearchedPersonName);
        SendRequest = findViewById(R.id.Send_friend_request_button);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //可用來檢測有沒有登入
        if (user!=null){
            self_username = user.getDisplayName();
        }


        SendRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String user_name = username.getText().toString();

                if (user_name.isEmpty()){
                    username.setError("Please enter the username you want to search");
                    username.requestFocus();
                }
                else if(!(user_name.isEmpty())) {
                    Query checkUsernameExistence = databaseRef.child("users").orderByChild("username").equalTo(user_name);
                    checkUsernameExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!(snapshot.exists())){
                                username.setError("Username not exists");
                                username.requestFocus();
                            }
                            else{

                                Query checkEmailExistence = databaseRef.child("friends").child(self_username).equalTo(user_name);
                                checkEmailExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            username.setError("The user is your friend already");
                                            username.requestFocus();
                                        }
                                        else{

                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

    }


}