package com.example.assignment_2.rank;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment_2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RankActivity extends AppCompatActivity {

    private ListView rank_lv;
    private Button rank_up;
    private ImageButton rank_back;

    private DatabaseReference databaseRef;
    private String username;
    private List<String> fNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        username = getIntent().getStringExtra("username");
        rank_lv = (ListView) findViewById(R.id.rank_lv);
        rank_up = (Button) findViewById(R.id.rank_btn_up);
        rank_back = (ImageButton) findViewById(R.id.rank_btn_back);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        rank_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast1 = Toast.makeText(RankActivity.this,"Friend:"+position,Toast.LENGTH_SHORT);
                toast1.show();
            }

        });

        updateRank();
        rank_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRank();
            }
        });

        rank_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void updateRank(){
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<rankMode> rankModeList = new ArrayList<>();
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rankModeList.clear();

                if (snapshot.child("friends/"+username).getValue() != null){
                    Map<String, Map<?,?>> friendsList = (Map<String, Map<?, ?>>) snapshot.child("friends/"+username).getValue();
                    fNameList =new ArrayList<String>(friendsList.keySet());
                    fNameList.add(username);

                    while (fNameList.size() > 0){
                        String f_name = null;
                        String max_name = null;
                        int f_steps = 0;
                        int max_steps=0;

                        Iterator it = fNameList.iterator();
                        int index = 0;
                        int max_index = 0;
                        while (it.hasNext()){
                            f_name = it.next().toString();
                            if(snapshot.child("current_steps/"+f_name).getValue() != null){
                                f_steps = Integer.parseInt(snapshot.child("current_steps/"+f_name).getValue().toString());
                            } else {
                                f_steps = 0;
                            }

                            if (f_steps>max_steps){
                                max_steps = f_steps;
                                max_name = f_name;
                                max_index = index;
                            } else if (f_steps==max_steps && (max_name != null)&&(f_name.compareTo(max_name)<0)){
                                max_name = f_name;
                                max_index = index;
                            } else if (f_steps==max_steps && (max_name == null)){
                                max_name = f_name;
                                max_index = index;
                            }
                            index += 1;
                        }
                        rankMode rank_f = new rankMode();
                        rank_f.setUsername(max_name);
                        rank_f.setSteps(max_steps);
                        //avatar
                        String f_avatar = (String) snapshot.child("profile/"+max_name+"/avatar").getValue();
                        rank_f.setAvatar(f_avatar);
                        rankModeList.add(rank_f);
                        fNameList.remove(max_name);
                    }
                    rank_lv.setAdapter(new MyRankAdapter(RankActivity.this, rankModeList));
                } else {
                    int user_steps;
                    if(snapshot.child("current_steps/"+username).getValue() != null){
                        user_steps = Integer.parseInt(snapshot.child("current_steps/"+username).getValue().toString());
                    } else {
                        user_steps = 0;
                    }
                    rankMode rank_f = new rankMode();
                    rank_f.setUsername(username);
                    rank_f.setSteps(user_steps);
                    //avatar
                    String f_avatar = (String) snapshot.child("profile/"+username+"/avatar").getValue();
                    rank_f.setAvatar(f_avatar);
                    rankModeList.add(rank_f);

                    rank_lv.setAdapter(new MyRankAdapter(RankActivity.this, rankModeList));
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