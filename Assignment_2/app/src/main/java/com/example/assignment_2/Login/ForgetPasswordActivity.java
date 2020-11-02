package com.example.assignment_2.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.assignment_2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText et_email;
    private Button yes;
    private ImageButton ib_left;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        et_email = findViewById(R.id.fb_et_email);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        yes = findViewById(R.id.fp_btn_yes);
        ib_left = findViewById(R.id.fp_left_btn);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_email.getText().toString().isEmpty()){
                    et_email.requestFocus();
                    et_email.setError("Cannot be empty");
                }
                else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(et_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgetPasswordActivity.this, "Reset email has been sent!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        et_email.setError("Email is not registered.");
                                        et_email.requestFocus();
                                    }

                                }
                            });
                }
            }
        });

        ib_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //databaseRef.child("user").orderByChild("email").equalTo(et_email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {

//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}