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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    private ListView cl_lv;
    private ImageButton cl_back;

    private String username;

    private List<friendsMode> friendsListNew=new ArrayList();
    private DatabaseReference databaseRef;
    private List<String> fNameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        username = getIntent().getStringExtra("username");
        cl_lv = (ListView) findViewById(R.id.cl_lv);
        cl_back = findViewById(R.id.cl_backward_btn);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("Chats/"+username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
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
                        friend.setUsername(f_name);
                        friendsListNew.add(friend);

                    }
                    cl_lv.setAdapter(new ChatListAdapter(ChatListActivity.this, friendsListNew));
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

        cl_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friend_username, friend_avatar;
                friend_username = friendsListNew.get(position).getUsername();
                friend_avatar = friendsListNew.get(position).getAvatar();

                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("friend_username", friend_username);
                intent.putExtra("friend_avatar", friend_avatar);

                startActivity(intent);
            }

        });

        cl_back.setOnClickListener(new View.OnClickListener() {
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

}