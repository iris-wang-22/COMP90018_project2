package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.friendlist.FriendsListActivity;
import com.example.assignment_2.friendlist.MyListAdapter;
import com.example.assignment_2.friendlist.friendsMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map;

public class MyRequestActivity extends AppCompatActivity {

    private String self_username;
    private DatabaseReference databaseRef;
    private DatabaseReference mReference;

    private ListView requestList;

    private String request_from;
    private String request_to;
    private String status;

    private List<requestDetail> requestListNew=new ArrayList();//requestDetail要修改！
    private List<String> userRequestList;

    /*
    ////
    private static final String userName = "username";
    private static final String theName = "The name";
    /////

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        self_username = getIntent().getStringExtra("selfUsername");

        requestList = (ListView) findViewById(R.id.r_lv);

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

        /*
        /////
        List<Map<String, String>> item_list = new ArrayList<Map<String, String>>();
        String[] username11 = {"username", "username", "username"};
        String[] theName11 = {"hhh", "yyy", "eee"};

        for (int i = 0; i < username11.length; i++){
            Map<String, String> item = new HashMap<String, String>();
            item.put(userName, username11[i]);
            item.put(theName, theName11[i]);
            item_list.add(item);
        }
        /////

         */



        //requestList.setAdapter(new RequestListAdapter(MyRequestActivity.this, item_list));


        //mReference = databaseRef.child("friend request").child("waiting").child(self_username);




        //mReference = FirebaseDatabase.getInstance().getReference().child(self_username).child("hhh");

        //ValueEventListener postListener = ;
        /*
        //會直接關掉
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                requestDetail request_detail = dataSnapshot.getValue(requestDetail.class);
                // [START_EXCLUDE]

                //request_from = detail.requestFrom;
                //request_to = detail.requestTo;
                //status = detail.status;

                Toast.makeText(MyRequestActivity.this, "okok", Toast.LENGTH_SHORT).show();


                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         */






        //這邊似乎不用？
        /*
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //這邊放接受或拒絕的動作？
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(MyRequestActivity.this,"Friend:"+position,Toast.LENGTH_SHORT);
                toast1.show();


            }
        });

         */

        ////可用
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(MyRequestActivity.this,"Friend:"+position,Toast.LENGTH_SHORT);
                toast1.show();
            }

        });


    }





    /*
    @Override
    protected void onStart(){
        super.onStart();


    }

     */

}