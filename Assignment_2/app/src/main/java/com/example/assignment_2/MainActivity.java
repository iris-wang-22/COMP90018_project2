package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private Button sign_up;
    private Button log_in;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedpreferences = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        String username = sharedpreferences.getString("username", null);
        String password = sharedpreferences.getString("password", null);
        String email = sharedpreferences.getString("email", null);
        if(username != null){

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();;
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                intent.putExtra("email", sharedpreferences.getString("email", null));
                                intent.putExtra("password", sharedpreferences.getString("password", null));
                                intent.putExtra("username", sharedpreferences.getString("username", null));
                                startActivity(intent);
                            }
                        }
            });
        }
        else
        {
            setContentView(R.layout.activity_main);
            sign_up = findViewById(R.id.Sign_up_Button);
            sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSignUpActivity();
                }
            });
            log_in = findViewById(R.id.Log_in_Button);
            log_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openLogInActivity();
                }
            });
        }
    }

    private void openLogInActivity() {
        //Intent intent = new Intent(this, Log_in_activity.class);
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this, Sign_up_activity.class);
        startActivity(intent);
    }
}