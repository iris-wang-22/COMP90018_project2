package com.example.assignment_2.friendlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.assignment_2.Personal.PersonalActivity;
import com.example.assignment_2.R;
import com.example.assignment_2.Util.CustomDialog;
import com.example.assignment_2.Util.ToastUtil;
import com.example.assignment_2.friendsRequest.Friends;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.assignment_2.Maps.MapsActivity.ds;

public class ChatListActivity extends AppCompatActivity {
    private ListView cl_lv;
    private ImageButton fl_ib;

    private ImageButton fl_p;
    private ImageButton fl_map;

    private String username;

    private List<friendsMode> friendsListNew=new ArrayList();
    private DatabaseReference databaseRef;
    private List<String> fNameList = new ArrayList<>();
    private List<String> userList = new ArrayList<>();

    private List<String> fN;
    private Map<String, Integer> receiveNum = new HashMap<>();
    private Map<String, Integer> receiveNum_ = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        username = getIntent().getStringExtra("username");
        cl_lv = (ListView) findViewById(R.id.cl_lv);
        fl_ib = findViewById(R.id.fl);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        update();


        cl_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friend_username, friend_avatar;
                friend_username = friendsListNew.get(position).getUsername();
                friend_avatar = friendsListNew.get(position).getAvatar();

                friendsListNew.get(position).setRemand(0);
                userList.remove(friend_username);

                cl_lv.setAdapter(new ChatListAdapter(ChatListActivity.this, friendsListNew));
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("friend_username", friend_username);
                intent.putExtra("friend_avatar", friend_avatar);


                startActivity(intent);
            }

        });

        fl_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this, FriendsListActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
                //finish();
            }
        });

        fl_p = findViewById(R.id.fl_btn_personal);
        fl_map = findViewById(R.id.fl_btn_map);
        fl_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_p = new Intent(ChatListActivity.this, PersonalActivity.class);
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

    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    public void update(){
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i=0; i < friendsListNew.size(); i++){
                    if (friendsListNew.get(i).getRemand() == 1&&(!userList.contains(friendsListNew.get(i).getUsername()))){
                        userList.add(friendsListNew.get(i).getUsername());
                    }
                }
                friendsListNew.clear();

                //test
                if(ds != null){
                    fN = new ArrayList<>();
                    for(DataSnapshot snapshot: ds.child("Chats/"+username).getChildren()) {
                        Map<String, String> chatMap = (Map<String, String>) snapshot.getValue();
                        String receiver = chatMap.get("receiver");
                        String sender = chatMap.get("sender");
                        if ((!sender.equals(username)) && (receiver.equals(username))) {
                            fN.add(sender);
                        } else if ((!receiver.equals(username)) && (sender.equals(username))) {
                            fN.add(receiver);
                        }
                    }
                    fN = removeDuplicate(fN);
                    Map<String,Map<String, String>> test = (Map<String,Map<String, String>>) ds.child("Chats/" + username).getValue();

                    String f_name_;
                    if(fN != null){
                        for(int i=0; i<fN.size(); i++){
                            int rNum = 0;
                            f_name_ = fN.get(i);

                            Iterator<Map<String, String>> iterator = test.values().iterator();
                            while(iterator.hasNext()) {
                                if (iterator.next().get("sender").equals(f_name_)) {
                                    rNum += 1;
                                }
                            }
                            receiveNum_.put(f_name_, rNum);
                        }
                    }
                }
                ////////////////////////////////////////////////////////////////


                for(DataSnapshot snapshot: dataSnapshot.child("Chats/"+username).getChildren()) {
                    Map<String, String> chatMap = (Map<String, String>) snapshot.getValue();
                    String receiver = chatMap.get("receiver");
                    String sender = chatMap.get("sender");
                    if ((!sender.equals(username)) && (receiver.equals(username))) {
                        fNameList.add(sender);
                    } else if ((!receiver.equals(username)) && (sender.equals(username))) {
                        fNameList.add(receiver);
                    }
                }
                fNameList = removeDuplicate(fNameList);

                String f_name, f_avatar;
                if(fNameList != null){
                    for(int i=0; i<fNameList.size(); i++){
                        f_name = fNameList.get(i);
                        Map<String,String> friendsProfile = (Map<String, String>) dataSnapshot.child("profile/"+f_name).getValue();
                        friendsMode friend=new friendsMode();
                        if(friendsProfile != null){
                            if (friendsProfile.containsKey("avatar")){
                                f_avatar = friendsProfile.get("avatar");
                            } else{
                                f_avatar = null;
                            }
                            friend.setAvatar(f_avatar);
                        }

                        /////////
                        int f_remand=0;
                        if(ds==null){
                            f_remand = 0;
                        } else if(ds!=null){
                            if(fN != null && fN.size()== fNameList.size()){
                                Map<String,Map<String, String>> test = (Map<String,Map<String, String>>) dataSnapshot.child("Chats/" + username).getValue();
                                Iterator<Map<String, String>> iterator_ = test.values().iterator();
                                int rNum = 0;
                                while(iterator_.hasNext()) {
                                    if (iterator_.next().get("sender").equals(f_name)) {
                                        rNum += 1;
                                    }
                                }
                                receiveNum.put(f_name, rNum);

                                if(receiveNum.get(f_name) != receiveNum_.get(f_name)){
                                    f_remand = 1;
                                }
                            } else if(receiveNum_.get(f_name)==null){
                                f_remand = 1;
                            }
                            if(userList!=null){
                                for(int j=0; j<userList.size();j++){
                                    if(f_name.equals(userList.get(j))){
                                        f_remand = 1;
                                    }
                                }
                            }
                            System.out.println("===========");
                            System.out.println(receiveNum);
                            System.out.println(receiveNum_);
                            System.out.println(userList);
                        }
                        ////////

                        friend.setUsername(f_name);
                        if(f_remand==1){
                            friend.setRemand(f_remand);
                        }

                        friendsListNew.add(friend);

                    }
                    cl_lv.setAdapter(new ChatListAdapter(ChatListActivity.this, friendsListNew));
                    ds =dataSnapshot;
                } else {
                    CustomDialog customDialog = new CustomDialog(ChatListActivity.this, R.style.CustomDialog);
                    customDialog.setTitle("Tips").setMessage("No chats now.")
                            .setCancel("Fine.", new CustomDialog.IOnCancelListener() {
                                @Override
                                public void onCancel(CustomDialog dialog) {
                                    finish();
                                }
                            }).setCancelable(false);
                    customDialog.show();
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