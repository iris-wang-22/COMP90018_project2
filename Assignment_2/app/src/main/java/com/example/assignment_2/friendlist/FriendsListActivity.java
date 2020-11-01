package com.example.assignment_2.friendlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.Friends;
import com.example.assignment_2.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private ListView firendlist;
    private ImageView fl_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        firendlist = (ListView) findViewById(R.id.f_lv);
        firendlist.setAdapter(new MyListAdapter(FriendsListActivity.this, getData()));
        firendlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(FriendsListActivity.this,"Friend:"+position,Toast.LENGTH_SHORT);
                toast1.show();


            }
        });
    }

    private List<friendmode> getData() {

        List<friendmode> list=new ArrayList();
        friendmode friend1=new friendmode();
        friend1.setAvatar(R.drawable.pig);
        friend1.setNickname("老王");
        friend1.setDescribe("隔壁老王");
        friendmode friend2=new friendmode();
        friend2.setAvatar(R.drawable.pig);
        friend2.setNickname("老zhang");
        friend2.setDescribe("隔壁老zhang");
        friendmode friend3=new friendmode();
        friend3.setAvatar(R.drawable.pig);
        friend3.setNickname("老李");
        friend3.setDescribe("隔壁老李");
        list.add(friend1);
        list.add(friend2);
        list.add(friend3);

        return list;
    }
}