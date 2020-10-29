package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.assignment_2.basepedo.ui.Activity;

public class MainActivity extends AppCompatActivity {
    private Button sign_up;
    private Button log_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void openLogInActivity() {
        //Intent intent = new Intent(this, Log_in_activity.class);
        Intent intent = new Intent(this, Log_in_activity.class);
        startActivity(intent);
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this, Sign_up_activity.class);
        startActivity(intent);
    }
}