package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Sign_up_activity extends AppCompatActivity {
    //The variables used for sign up activity
    private EditText username, emailAddress, password;
    private Button signup;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity);

        //When activity initialized then initialize all required variables
        firebaseAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username_text);
        emailAddress = findViewById(R.id.Email_address_text);
        password = findViewById(R.id.Password_text);
        signup = findViewById(R.id.Sign_up_submit_button);
        databaseRef = FirebaseDatabase.getInstance().getReference();


        //When sign up button is clicked.
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username_Str = username.getText().toString();
                final String email = emailAddress.getText().toString();
                final String password_Str = password.getText().toString();

                if(username_Str.isEmpty()) {
                    username.setError("Please enter username");
                    username.requestFocus();
                }
                else if(email.isEmpty()) {
                    emailAddress.setError("Please enter E-mail address");
                    emailAddress.requestFocus();
                }
                else if(password_Str.isEmpty()) {
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else if(!(username_Str.isEmpty() && email.isEmpty() && password_Str.isEmpty())) {
                    Query checkExistence = databaseRef.child("users").orderByChild("username").equalTo(username_Str);
                    checkExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                username.setError("Username exists");
                                username.requestFocus();
                            }
                            else{
                                firebaseAuth.createUserWithEmailAndPassword(email, password_Str).addOnCompleteListener(Sign_up_activity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(!task.isSuccessful()) {
                                            Toast.makeText(Sign_up_activity.this,"Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            databaseRef.child("users").child(username_Str).setValue(new user(username_Str, email, password_Str));
                                            Toast.makeText(Sign_up_activity.this,"Sign up successful!!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else {
                    Toast.makeText(Sign_up_activity.this,"Error occured. T_T", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}