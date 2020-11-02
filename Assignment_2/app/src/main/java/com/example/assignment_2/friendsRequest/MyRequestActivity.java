package com.example.assignment_2.friendsRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyRequestActivity extends AppCompatActivity {

    private String self_username;
    private DatabaseReference databaseRef;
    private Button btn_backward;
    //private DatabaseReference mReference;

    private ListView requestList;

    

    private List<requestDetail> requestListNew=new ArrayList();//requestDetail要修改！
    private List<String> userRequestList;

    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        /*
        databaseRef = FirebaseDatabase.getInstance().getReference();
        self_username = getIntent().getStringExtra("selfUsername");

         */

        requestList = (ListView) findViewById(R.id.r_lv);

        timer = new Timer();
        timer.schedule(new keepUpdate(), 0, 5000);

        /*
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String request_from_user;

                if (snapshot.child("friend request/"+self_username).getValue() != null){
                    Map<String,Map<?,?>> request_list = (Map<String, Map<?, ?>>) snapshot.child("friend request/"+self_username).getValue();
                    userRequestList =new ArrayList<String>(request_list.keySet());

                    for(int i=0; i<userRequestList.size(); i++){ //userRequestList.size()
                        request_from_user = userRequestList.get(i);

                        Map<String,String> request_details = (Map<String, String>) snapshot.child("friend request/"+self_username+"/"+userRequestList.get(i)).getValue();

                        if (request_details != null) {
                            if (request_details.containsValue("waiting")){
                                requestDetail request_detail=new requestDetail();
                                request_detail.setRequestFrom(request_from_user); //requestDetail要加
                                request_detail.setRequestTo(request_details.get("requestTo"));
                                request_detail.setStatus(request_details.get("status"));
                                requestListNew.add(request_detail);
                            }

                        }
                    }
                    requestList.setAdapter(new RequestListAdapter(MyRequestActivity.this, requestListNew));//要改
                }
                else {
                    Toast toast1 = Toast.makeText(MyRequestActivity.this,"You don't have any friend requests",Toast.LENGTH_LONG);
                    toast1.show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO Auto-generated method stub
                System.out.println("This didn't work");
            }
        });

         */


        ////可用
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(MyRequestActivity.this,"Friend request: "+position,Toast.LENGTH_SHORT);
                toast1.show();
            }

        });

        btn_backward = findViewById(R.id.backward_btn);
        btn_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                finish();
            }
        });


    }
    
    public class keepUpdate extends TimerTask {
        public void run(){
            databaseRef = FirebaseDatabase.getInstance().getReference();
            self_username = getIntent().getStringExtra("selfUsername");
            /////
            if (requestListNew.size() != 0){
                requestListNew.clear();
            }

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String request_from_user;

                    Query checkRequestExistence = databaseRef.child("friend request").child(self_username).orderByChild("status").equalTo("waiting");
                    checkRequestExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!(snapshot.exists())) {
                                Toast toast1 = Toast.makeText(MyRequestActivity.this,"You don't have any friend requests",Toast.LENGTH_LONG);
                                toast1.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    if (snapshot.child("friend request/"+self_username).getValue() != null){
                        Map<String,Map<?,?>> request_list = (Map<String, Map<?, ?>>) snapshot.child("friend request/"+self_username).getValue();
                        userRequestList =new ArrayList<String>(request_list.keySet());

                        for(int i=0; i<userRequestList.size(); i++){ //userRequestList.size()
                            request_from_user = userRequestList.get(i);

                            Map<String,String> request_details = (Map<String, String>) snapshot.child("friend request/"+self_username+"/"+userRequestList.get(i)).getValue();

                            if (request_details != null) {
                                if (request_details.containsValue("waiting")){
                                    requestDetail request_detail=new requestDetail();
                                    request_detail.setRequestFrom(request_from_user); //requestDetail要加
                                    request_detail.setRequestTo(request_details.get("requestTo"));
                                    request_detail.setStatus(request_details.get("status"));
                                    requestListNew.add(request_detail);
                                }

                            }
                        }
                        requestList.setAdapter(new RequestListAdapter(MyRequestActivity.this, requestListNew));//要改
                    }
                    /*
                    else {
                        Toast toast1 = Toast.makeText(MyRequestActivity.this,"You don't have any friend requests",Toast.LENGTH_LONG);
                        toast1.show();

                    }

                     */
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // TODO Auto-generated method stub
                    System.out.println("This didn't work");
                }
            });
        }
    }





}