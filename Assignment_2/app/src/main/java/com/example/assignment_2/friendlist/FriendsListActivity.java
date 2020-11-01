package com.example.assignment_2.friendlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.PersonalActivity;
import com.example.assignment_2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsListActivity extends AppCompatActivity {

    private ListView firend_lv;
    private ImageButton fl_p;
    private ImageButton fl_map;

    private List<friendsMode> friendsListNew=new ArrayList();



    private DatabaseReference databaseRef;
    private String username;
    private List<String> fNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        username = getIntent().getStringExtra("username");
        firend_lv = (ListView) findViewById(R.id.f_lv);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String f_name, f_age, f_gender, f_avatar;
                Integer num;
                if (snapshot.child("friends/"+username).getValue() != null){
                    Map<String,Map<?,?>> friendsList = (Map<String, Map<?, ?>>) snapshot.child("friends/"+username).getValue();
                    fNameList =new ArrayList<String>(friendsList.keySet());

                    for(int i=0; i<fNameList.size(); i++){
                        f_name = fNameList.get(i);
                        Object obj = friendsList.get(f_name).get("number");
                        num = Integer.parseInt(obj.toString());

                        Map<String,String> friendsProfile = (Map<String, String>) snapshot.child("users/"+fNameList.get(i)+"/profile").getValue();

                        if(friendsProfile != null){
                            if (friendsProfile.containsKey("age")){
                                f_age = friendsProfile.get("age");
                            } else{
                                f_age = null;
                            }
                            if (friendsProfile.containsKey("gender")){
                                f_gender = friendsProfile.get("gender");
                            } else{
                                f_gender =null;
                            }
                            if (friendsProfile.containsKey("avatar")){
                                f_avatar = friendsProfile.get("avatar");
                            } else{
                                f_avatar = null;
                            }
                            friendsMode friend=new friendsMode();
                            friend.setNumber(num);
                            friend.setUsername(f_name);
                            friend.setAge(f_age);
                            friend.setGender(f_gender);
                            friend.setAvatar(f_avatar);
                            friendsListNew.add(friend);

                        } else{
                            f_age = null;
                            f_gender = null;
                            f_avatar = null;
                            friendsMode friend=new friendsMode();
                            friend.setNumber(num);
                            friend.setUsername(f_name);
                            friend.setAge(f_age);
                            friend.setGender(f_gender);
                            friend.setAvatar(f_avatar);
                            friendsListNew.add(friend);
                        }
                    }

                    firend_lv.setAdapter(new MyListAdapter(FriendsListActivity.this, friendsListNew));
                } else {

                    Toast toast1 = Toast.makeText(FriendsListActivity.this,"You don't have friends",Toast.LENGTH_LONG);
                    toast1.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO Auto-generated method stub
                System.out.println("This didn't work");
            }
        });

        firend_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(FriendsListActivity.this,"Friend:"+position,Toast.LENGTH_SHORT);
                toast1.show();
            }

        });

        fl_p = findViewById(R.id.fl_btn_personal);
        fl_map = findViewById(R.id.fl_btn_map);
        fl_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_p = new Intent(FriendsListActivity.this, PersonalActivity.class);
                intent_p.putExtra("username", username);
                startActivity(intent_p);
                finish();
            }
        });

        fl_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}