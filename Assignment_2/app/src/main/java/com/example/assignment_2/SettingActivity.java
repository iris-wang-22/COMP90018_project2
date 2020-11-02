package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    private TextView tv_d_self;
    private TextView tv_d_friend;
    private TextView tv_about;

    private ImageButton ib_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tv_d_self = (TextView) findViewById(R.id.set_tv_delete_self);
        tv_d_friend = (TextView) findViewById(R.id.set_tv_delete_friends);
        tv_about = (TextView) findViewById(R.id.set_tv_about);
        ib_back = (ImageButton) findViewById(R.id.set_btn_back);

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_d_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_d_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}