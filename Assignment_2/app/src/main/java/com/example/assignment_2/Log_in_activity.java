package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Log_in_activity extends AppCompatActivity {
    private EditText emailAddress, password;
    private TextView forgotPassword;
    private Button login;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private Button turn_signup;
    private ImageButton arrow_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_activity);

        emailAddress = findViewById(R.id.Email_address_text);
        password = findViewById(R.id.Password_text);
        login = findViewById(R.id.Log_in_submit_button);
        forgotPassword = findViewById(R.id.forgot_password);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailAddress.getText().toString();
                final String password_Str = password.getText().toString();

                if(email.isEmpty()) {
                    emailAddress.setError("Please enter email address");
                    emailAddress.requestFocus();
                }
                else if(password_Str.isEmpty()) {
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else if(!(email.isEmpty() && password_Str.isEmpty())) {
                    firebaseAuth.signInWithEmailAndPassword(email, password_Str).addOnCompleteListener(Log_in_activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                databaseRef.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String username = null;
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            username = child.getKey();
                                        }
                                        Toast.makeText(Log_in_activity.this, "Successful!!!!.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Log_in_activity.this, MapsActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password_Str);
                                        intent.putExtra("username", username);
                                        startActivity(intent);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                Toast.makeText(Log_in_activity.this, "Authentication failed. Check your email/password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        turn_signup = (Button) findViewById(R.id.Turn_To_Sign_Button);
        turn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Log_in_activity.this, Sign_up_activity.class);
                startActivity(intent);
            }
        });

        arrow_left = (ImageButton) findViewById(R.id.Arrow_left_btn);
        arrow_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Log_in_activity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}