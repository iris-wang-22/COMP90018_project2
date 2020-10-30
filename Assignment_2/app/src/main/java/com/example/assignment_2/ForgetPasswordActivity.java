package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        et_email = findViewById(R.id.fb_et_email);
        et_new = findViewById(R.id.fb_et_new);
    }
}