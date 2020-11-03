package com.example.assignment_2.Personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.R;

import com.example.assignment_2.friendlist.friendsMode;//import因為用friendMode

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



public class DeleteFriendActivity extends AppCompatActivity {

    private String self_username;
    private DatabaseReference databaseRef;
    private ImageButton btn_backward;

    private ListView showFriendList;

    private List<friendsMode> friendsListNew=new ArrayList();
    private List<String> fNameList;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_friend);

        showFriendList = (ListView) findViewById(R.id.df_lv);
        timer = new Timer();
        timer.schedule(new updateFriends(), 0, 5000);


        ////可用
        showFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(DeleteFriendActivity.this,"Friend: "+position,Toast.LENGTH_SHORT);
                toast1.show();
            }

        });

        btn_backward = findViewById(R.id.df_backward_btn);//要改
        btn_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                finish();
            }
        });
    }

    public class updateFriends extends TimerTask {
        public void run(){
            databaseRef = FirebaseDatabase.getInstance().getReference();
            self_username = getIntent().getStringExtra("username");
            /////
            if (friendsListNew.size() != 0){
                friendsListNew.clear();
            }

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String friend_name;


                    if (snapshot.child("friends/"+self_username).getValue() != null){
                        Map<String,Map<?,?>> friend_list = (Map<String, Map<?, ?>>) snapshot.child("friends/"+self_username).getValue();
                        fNameList =new ArrayList<String>(friend_list.keySet());

                        for(int i=0; i<fNameList.size(); i++){
                            friend_name = fNameList.get(i);

                            String fromAvatar = (String) snapshot.child("profile/"+fNameList.get(i)+"/avatar").getValue();

                            friendsMode friend=new friendsMode();
                            friend.setUsername(friend_name);
                            friend.setAvatar(fromAvatar);
                            friend.setNumber(null);
                            friend.setAge(null);
                            friend.setGender(null);
                            friendsListNew.add(friend);

                        }
                        showFriendList.setAdapter(new DeleteFriendAdapter(DeleteFriendActivity.this, friendsListNew, self_username));//要改
                    }
                    else {
                        showFriendList.setAdapter(new DeleteFriendAdapter(DeleteFriendActivity.this, friendsListNew, self_username));
                        Toast toast3 = Toast.makeText(DeleteFriendActivity.this,"You don't have any friends",Toast.LENGTH_SHORT);
                        toast3.show();

                    }
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