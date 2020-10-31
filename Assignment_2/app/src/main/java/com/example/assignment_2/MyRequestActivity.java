package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.friendlist.FriendsListActivity;
import com.example.assignment_2.friendlist.MyListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyRequestActivity extends AppCompatActivity {

    private String self_username;
    private DatabaseReference databaseRef;
    private DatabaseReference mReference;
    private ListView requestList;

    private String request_from;
    private String request_to;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        self_username = getIntent().getStringExtra("selfUsername");

        requestList = (ListView) findViewById(R.id.r_lv);
        requestList.setAdapter(new RequestListAdapter(MyRequestActivity.this));


        /*
        mReference = FirebaseDatabase.getInstance().getReference().child(self_username).child("hhh");

        //ValueEventListener postListener = ;
        //會直接關掉
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                requestDetail detail = dataSnapshot.getValue(requestDetail.class);
                // [START_EXCLUDE]

                request_from = detail.requestFrom;
                request_to = detail.requestTo;
                status = detail.status;

                Toast.makeText(MyRequestActivity.this, request_from+"////"+request_to+"////"+status, Toast.LENGTH_SHORT).show();


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


    }

    /*
    @Override
    protected void onStart(){
        super.onStart();


    }

     */

}