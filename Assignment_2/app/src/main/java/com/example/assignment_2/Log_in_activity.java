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

public class Log_in_activity extends AppCompatActivity {
    private EditText emailAddress, password;
    private Button login;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_activity);

        emailAddress = findViewById(R.id.Email_address_text);
        password = findViewById(R.id.Password_text);
        login = findViewById(R.id.Log_in_submit_button);
        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailAddress.getText().toString();
                String password_Str = password.getText().toString();

                if(email.isEmpty()) {
                    emailAddress.setError("Please enter email address");
                    emailAddress.requestFocus();
                }
                else if(password_Str.isEmpty()) {
                    password.setError("Please enter password");
                    emailAddress.requestFocus();
                }
                else if(!(email.isEmpty() && password_Str.isEmpty())) {
                    firebaseAuth.signInWithEmailAndPassword(email, password_Str).addOnCompleteListener(Log_in_activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(Log_in_activity.this, "Successful!!!!.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Log_in_activity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}