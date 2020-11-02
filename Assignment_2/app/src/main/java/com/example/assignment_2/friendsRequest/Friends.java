package com.example.assignment_2.friendsRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.assignment_2.Util.Base64Util.base64ToBitmap;

public class Friends extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private Button SendRequest;
    private EditText username;
    //private String uEmail;
    private String self_username;
    private String avatar;
    private Bitmap avatar_bit;

    private TextView self_name_tv;
    private ImageView self_avatar_iv;
    private Button my_friend_request_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        self_username = getIntent().getStringExtra("username");
        avatar = getIntent().getStringExtra("avatar");

        self_name_tv = findViewById(R.id.fs_tv_username);
        self_avatar_iv = findViewById(R.id.fs_avatar);

        self_name_tv.setText(self_username);
        if (avatar != null){
            avatar_bit = base64ToBitmap(avatar);
            self_avatar_iv.setImageBitmap(avatar_bit);
        }
        
        /*
        System.out.println("-------------------------------------------");
        System.out.println(self_username);
        System.out.println(databaseRef.getKey());
        System.out.println("-------------------------------------------");


         */
        /*
        //
        List<String> data = new ArrayList<>();
        data.add("friend1");
        data.add("friend2");
        data.add("friend3");
        //Map<String, Double> coordinate = new HashMap<String, Double>();
        //coordinate.put("latitude", 0.0);
        //coordinate.put("landtitude", 0.12);

        //databaseRef.child("friends").child("user1").setValue(data);
        //databaseRef.child("coordinates").child("user1").setValue(coordinate);
        //Toast.makeText(Friends.this, "Save successful!!", Toast.LENGTH_SHORT).show();
        //

         */

        firebaseAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.SearchedPersonName);
        SendRequest = findViewById(R.id.Send_friend_request_button);
        //FirebaseUser user = firebaseAuth.getCurrentUser();  //可先不要


        SendRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String user_name = username.getText().toString();


                if (user_name.isEmpty()){
                    username.setError("Please enter the username you want to search");
                    username.requestFocus();
                }
                else if(!(user_name.isEmpty())) {
                    Query checkUsernameExistence = databaseRef.child("users").orderByChild("username").equalTo(user_name);
                    checkUsernameExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!(snapshot.exists())){
                                username.setError("Username not exists");
                                username.requestFocus();
                            }
                            else{

                                Query checkFriendExistence = databaseRef.child("friends").child(self_username).orderByChild("username").equalTo(user_name);
                                checkFriendExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            username.setError("The user is your friend already");
                                            username.requestFocus();
                                        }
                                        else{
                                            //這邊可能要改，看資料庫
                                            Map<String, String> request_details = new HashMap<String, String>();
                                            request_details.put("requestFrom", self_username);
                                            request_details.put("requestTo", user_name);
                                            request_details.put("status", "waiting");

                                            databaseRef.child("friend request").child(user_name).child(self_username).setValue(request_details);

                                            Toast.makeText(Friends.this, "Request successfully sent!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else {
                    Toast.makeText(Friends.this,"Error occured. T_T", Toast.LENGTH_SHORT).show();
                }
            }
        });

        my_friend_request_btn = findViewById(R.id.request_to_me_button);
        my_friend_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                openMyRequestActivity();
            }
        });

    }

//    private void openMyRequestActivity() {
//        Intent intent = new Intent(this, MyRequestActivity.class); //要改成一個新的activity class
//        intent.putExtra("selfUsername", self_username);
//        startActivity(intent);
//    }


}

