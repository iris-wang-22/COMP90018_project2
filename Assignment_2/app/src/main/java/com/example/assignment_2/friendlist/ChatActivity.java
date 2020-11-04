package com.example.assignment_2.friendlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.assignment_2.Login.user;
import com.example.assignment_2.Personal.NewProfileActivity;
import com.example.assignment_2.R;
import com.example.assignment_2.Util.BitmapUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.assignment_2.Util.Base64Util.base64ToBitmap;
import static com.example.assignment_2.Util.Base64Util.bitMapToBase64;


public class ChatActivity extends AppCompatActivity {



    private TextView userText;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private String username;
    private String friend_username;
    private String friend_avatar;

    ImageButton btn_send;
    EditText text_send;

    private ImageButton btn_image;
    protected static final int CHOOSE_PICTURE = 1;
    protected static final int TAKE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private Bitmap image;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    private TextView friendUsername;
    private ImageView friendAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView =findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        userText = findViewById(R.id.username_text);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        friendUsername = findViewById(R.id.chat_tv);
        friendAvatar = findViewById(R.id.chat_iv);
        btn_image = findViewById(R.id.btn_image);

        username = getIntent().getStringExtra("username");
        friend_username = getIntent().getStringExtra("friend_username");
        friend_avatar = getIntent().getStringExtra("friend_avatar");

        friendUsername.setText(friend_username);
        if(friend_avatar !=null){
            friendAvatar.setImageBitmap(base64ToBitmap(friend_avatar));
        }

        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(username,friend_username,msg,null);
                    text_send.setText("");
                }else
                {
                    Toast.makeText(ChatActivity.this,"Cannot Send empty Message",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //firebaseUser.getUid(),friend_username
        //readMessage();

        reference = FirebaseDatabase.getInstance().getReference("users").child(username);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                readMessage();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message, String image){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> HashMap = new HashMap<>();
        HashMap.put("sender",sender);
        HashMap.put("receiver",receiver);
        HashMap.put("message", message);
        HashMap.put("image", image);

        reference.child("Chats").push().setValue(HashMap);
        //readMessage();

    }

    //String myid, String userid
    private void readMessage(){
        //mChat = new ArrayList<>();
        mChat=new ArrayList();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Map<String,String> chatMap = (Map<String, String>) snapshot.getValue();
                    String receiver = chatMap.get("receiver");
                    String sender = chatMap.get("sender");
                    String message = chatMap.get("message");
                    String image = chatMap.get("image");
                    if((receiver.equals(username) && sender.equals(friend_username))||(receiver.equals(friend_username)&&sender.equals(username))){
                        Chat chat = new Chat();
                        chat.setReceiver(receiver);
                        chat.setSender(sender);
                        chat.setMsg(message);
                        chat.setUsername(username);
                        chat.setImage(image);

                        mChat.add(chat);
                    }
                }
                messageAdapter  = new MessageAdapter(ChatActivity.this,mChat);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("How to get the image?");
        String[] items = { "Take a picture", "Choose a picture"};
        builder.setNegativeButton("Cancel", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        File imageFile=new File(getFilesDir(),"image.jpg");
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri= FileProvider.getUriForFile(ChatActivity.this,"com.example.assignment_2.provider",imageFile);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent;
                        if (Build.VERSION.SDK_INT<19){
                            openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            openAlbumIntent.setType("image/*");
                        } else{
                            openAlbumIntent = new Intent(Intent.ACTION_PICK, null);
                            openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        }
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap bitmap = null;
            switch (requestCode){
                case TAKE_PICTURE:
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), tempUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case CHOOSE_PICTURE:
                    try {
                        //data.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //data.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            sendMessage(username,friend_username,null,bitMapToBase64(bitmap));
        }

    }
    protected String setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        String base64I = null;
        if (extras != null) {
            image = extras.getParcelable("data");
            base64I = bitMapToBase64(image);

            //saveImage(photo);
            //iv_image.setImageDrawable(null);
            //iv_image.setImageBitmap(image);
        }
        return base64I;
    }


}