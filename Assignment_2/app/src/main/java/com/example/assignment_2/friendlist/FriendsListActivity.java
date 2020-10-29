package com.example.assignment_2.friendlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.Friends;
import com.example.assignment_2.R;

public class FriendsListActivity extends AppCompatActivity {

    private ListView firendlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        firendlist = (ListView) findViewById(R.id.f_lv);
        firendlist.setAdapter(new MyListAdapter(FriendsListActivity.this));
        firendlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(FriendsListActivity.this,"Friend:"+position,Toast.LENGTH_SHORT);
                toast1.show();


            }
        });
    }
}