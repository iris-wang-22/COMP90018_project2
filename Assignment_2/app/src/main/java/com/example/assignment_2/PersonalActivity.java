package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.assignment_2.basepedo.config.Constant;
import com.example.assignment_2.basepedo.service.StepService;
import com.example.assignment_2.basepedo.ui.bActivity;
import com.example.assignment_2.friendlist.FriendsListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PersonalActivity extends AppCompatActivity implements Handler.Callback{

    private String username, age, gender;
    private DatabaseReference databaseRef;

    private ImageButton iBtn_01;
    private ImageButton iBtn_02;
    private TextView p_quit;
    private ImageButton p_fl;
    private ImageButton p_map;

    private TextView tv_userName;
    private TextView tv_age;
    private TextView tv_gender;
    //basepedo
    private final String TAG = bActivity.class.getSimpleName();
    private long TIME_INTERVAL = 500;
    private TextView text_step;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        init();
        startServiceForStrategy();

        iBtn_01 = (ImageButton) findViewById(R.id.p_ib_01);
        iBtn_02 = (ImageButton) findViewById(R.id.p_ib_02);
        p_fl = findViewById(R.id.p_btn_friends);
        p_map = findViewById(R.id.p_btn_map);
        p_quit = findViewById(R.id.p_tv_quit);
        setListeners();

        username = getIntent().getStringExtra("username");

        tv_userName = (TextView) findViewById(R.id.p_tv_username);
        tv_age = (TextView) findViewById(R.id.p_tv_age);
        tv_gender = (TextView) findViewById(R.id.p_tv_gender);

        tv_userName.setText(username);

        String user_path = "users/"+username;
        databaseRef = FirebaseDatabase.getInstance().getReference(user_path);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("+++++++++++++++++++++++++++++++++");

                //Object document = snapshot.getValue();
                //Map pro = snapshot.child("profile").getValue(Map.class);
                //snapshot.child("profile").getValue().getClass();
                age = snapshot.child("profile/age").getValue(String.class);
                gender = snapshot.child("profile/gender").getValue(String.class);
                tv_age.setText("Age: "+age);
                tv_gender.setText(gender);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO Auto-generated method stub
                System.out.println("This didn't work");
            }
        });


    }




    //basepedo
    private Handler delayHandler;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                text_step.setText(msg.getData().getInt("step") + "");
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
        }
        return false;
    }

    @SuppressLint("RestrictedApi")

    //basepedo
    private void startServiceForStrategy() {
        if (!isServiceWork(this, StepService.class.getName())) {
            setupService(true);
        } else {
            setupService(false);
        }
    }

    private void init() {
        text_step = findViewById(R.id.p_text_step);
        delayHandler = new Handler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void setupService(boolean flag) {
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        if (flag) {
            startService(intent);
        }
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM =
                (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    //button onClick
    private void setListeners(){
        OnClick onclick = new OnClick();
        iBtn_01.setOnClickListener(onclick);
        iBtn_02.setOnClickListener(onclick);
        p_quit.setOnClickListener(onclick);
        p_fl.setOnClickListener(onclick);
        p_map.setOnClickListener(onclick);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.p_ib_01:
                    finish();
                    break;
                case R.id.p_ib_02:
                    Intent intent2 = null;
                    intent2 = new Intent(PersonalActivity.this, NewProfileActivity.class);
                    intent2.putExtra("username", username);
                    startActivity(intent2);
                    finish();
                    break;

                case R.id.p_tv_quit:
                    Intent intent_main = new Intent();
                    intent_main.setClass(PersonalActivity.this, MainActivity.class);
                    intent_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
                    startActivity(intent_main);
                    finish();
                    break;
                case R.id.p_btn_friends:
                    Intent intent_f = new Intent(PersonalActivity.this, FriendsListActivity.class);
                    intent_f.putExtra("username", username);
                    startActivity(intent_f);
                    finish();
                    break;
                case R.id.p_btn_map:
                    Intent intent_m = new Intent(PersonalActivity.this, MapsActivity.class);
                    intent_m.putExtra("username", username);
                    startActivity(intent_m);
                    finish();
                    break;
            }
        }
    }

}

