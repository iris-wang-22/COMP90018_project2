package com.example.assignment_2.Personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.assignment_2.Login.MainActivity;
import com.example.assignment_2.R;
import com.example.assignment_2.Util.CustomDialog;
import com.example.assignment_2.Util.ToastUtil;
import com.google.firebase.auth.FirebaseAuth;

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
                CustomDialog customDialog = new CustomDialog(SettingActivity.this, R.style.CustomDialog);
                customDialog.setTitle("Warning!").setMessage("Are you sure to delete your account?")
                        .setCancel("No, not sure.", new CustomDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(CustomDialog dialog) {
                                ToastUtil.showMsg(SettingActivity.this,"Good choice!");
                            }
                        }).setConfirm("Yes, sure.", new CustomDialog.IOnConfirmListener() {
                    @Override
                    public void onConfirm(CustomDialog dialog) {
                        Intent intent = new Intent(SettingActivity.this, DeleteAccount.class);
                        startActivity(intent);
                        finish();
                    }
                }).setCancelable(false);
                customDialog.show();
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