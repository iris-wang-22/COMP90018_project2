package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_up_activity extends AppCompatActivity {
    //The variables used for sign up activity
    private EditText username, emailAddress, password;
    private Button signup;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private Button turn_login;

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
                else if(password_Str.length()<6) {
                    password.setError("Password length has to be at least 6.");
                    password.requestFocus();
                }
                else if(!(username_Str.isEmpty() && email.isEmpty() && password_Str.isEmpty())) {
                    // Check if username exists.
                    Query checkUsernameExistence = databaseRef.child("users").orderByChild("username").equalTo(username_Str);
                    checkUsernameExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                username.setError("Username exists");
                                username.requestFocus();
                            }
                            else{
                                // Check if email has been registered.
                                Query checkEmailExistence = databaseRef.child("users").orderByChild("email").equalTo(email);
                                checkEmailExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                                      if(snapshot.exists()) {
                                          emailAddress.setError("E-mail exists");
                                          emailAddress.requestFocus();
                                      }
                                      else{
                                          // Create account on Firebase authentication.
                                          firebaseAuth.createUserWithEmailAndPassword(email, password_Str).addOnCompleteListener(Sign_up_activity.this, new OnCompleteListener<AuthResult>() {
                                              @Override
                                              public void onComplete(@NonNull Task<AuthResult> task) {
                                                  if (!task.isSuccessful()) {
                                                      Toast.makeText(Sign_up_activity.this, "Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                                                  } else {
                                                      databaseRef.child("users").child(username_Str).setValue(new user(username_Str, email, password_Str));
                                                      Toast.makeText(Sign_up_activity.this, "Sign up successful!!", Toast.LENGTH_SHORT).show();
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

        turn_login = (Button) findViewById(R.id.Turn_To_Login_Button);
        turn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_up_activity.this, Log_in_activity.class);
                startActivity(intent);
            }
        });
    }
}