package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewProfileActivity extends AppCompatActivity {

    private TextView tv_name;
    private EditText et_age;
    private RadioGroup rp_gender;
    private RadioButton rBtn_gender;
    private Button btn_yes;
    private Button btn_no;

    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private String username, age, gender;

    private static final String TAG = "FIRE_BASE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        username = getIntent().getStringExtra("username");

        initView();
        database();

        tv_name.setText(username);

        rp_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rBtn_gender = (RadioButton) findViewById(checkedId);

            }
        });


    }


    //初始化
    private void initView() {
        tv_name = (TextView) findViewById(R.id.new_tv_username);
        et_age = (EditText) findViewById(R.id.new_ev_age);
        rp_gender = (RadioGroup) findViewById(R.id.new_rp_gender);
        btn_yes = (Button) findViewById(R.id.new_btn_yes);
        btn_no = (Button) findViewById(R.id.new_btn_no);
    }

    /**
     * 实时数据库功能
     */
    private void database() {
        //获取数据库实例
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //获取Reference  "message"为存储值的key
        String age_path = "users/"+username+"/profile/age";
        String gender_path = "users/"+username+"/profile/gender";
        final DatabaseReference ageRef = database.getReference(age_path);
        final DatabaseReference genderRef = database.getReference(gender_path);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                age = et_age.getText().toString().trim();
                gender = rBtn_gender.getText().toString().trim();

                if (TextUtils.isEmpty(age)) {
                    Toast.makeText(NewProfileActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(NewProfileActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewProfileActivity.this, PersonalActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }
                //为实时数据库存值
                ageRef.setValue(age);
                genderRef.setValue(gender);
            }
        });

        btn_no.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

//        //注册当值发生改变时的事件监听
//        ageRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String age = dataSnapshot.getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Toast.makeText(NewProfileActivity.this, "获取异常", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, error.toException().toString());
//            }
//        });
    }

}