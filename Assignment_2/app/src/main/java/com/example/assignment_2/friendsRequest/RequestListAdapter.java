package com.example.assignment_2.friendsRequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.assignment_2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<requestDetail> mData;
    private ViewHolder1 holder = null;

    //private Button btn_accept;
    //private Button btn_deny;
    //private Object item;

    /////
    //List<Map<String, String>> my_item_list;

    private List<String> request_from1 = new ArrayList<String>();
    private List<String> request_to1 = new ArrayList<String>();
    private List<String> status1 = new ArrayList<String>();

    private DatabaseReference databaseRef;

    private List<String> friendList11;
    private List<String> friendList22;


    public RequestListAdapter(Context context, List data){
        this.mContext = context;

        /////
        //my_item_list = item_list;//this???

        this.mData = data;
        mLayoutInflater = LayoutInflater.from(context);


    }

    @Override
    public int getCount() {
        /////
        //return my_item_list.size();
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    /////
    static class ViewHolder1 {
        //public ImageView imageView;
        //public TextView tvEmail, tvAge;
        public TextView rUsername;
        public Button viewBtn_accept;
        public Button viewBtn_deny;

        /*
        public Button btn_accept, btn_deny;

        public ViewHolder1(){
            btn_accept = findViewById(R.id.r_accept);
        }

         */

        /////
        /*
        //public Button btn_accept, btn_deny;

        public ViewHolder1(@NonNull View itemView){
            super(itemView);
            btn_accept = (Button) itemView.findViewById(R.id.r_accept);
            btn_deny = (Button) itemView.findViewById(R.id.r_deny);

        }


         */


    }

    /////這邊還沒改！！！
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null){
            convertView = mLayoutInflater.inflate(R.layout.layout_rlist_item, null);
            requestDetail details = mData.get(position);

            holder = new ViewHolder1();
            holder.rUsername = (TextView) convertView.findViewById(R.id.r_username);

            holder.viewBtn_accept = (Button) convertView.findViewById(R.id.r_accept);
            holder.viewBtn_deny = (Button) convertView.findViewById(R.id.r_deny);


            holder.rUsername.setText(details.getRequestFrom());

            request_from1.add(details.getRequestFrom());
            request_to1.add(details.getRequestTo());
            status1.add(details.getStatus());

            convertView.setTag(holder);



        } else{
            holder = (ViewHolder1) convertView.getTag();
        }

        holder.viewBtn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRef = FirebaseDatabase.getInstance().getReference();
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //更新好友邀請的status
                        databaseRef.child("friend request").child(request_to1.get(position)).child(request_from1.get(position)).child("status").setValue("replied");

                        //更新requestTo的好友
                        String friendNum;
                        //找exist
                        if(!(snapshot.child("friends/"+request_to1.get(position)).exists())){
                            friendNum = "0";
                        }
                        else {
                            Map<String,Map<?,?>> friend_list11 = (Map<String, Map<?, ?>>) snapshot.child("friends/"+request_to1.get(position)).getValue();
                            friendList11 =new ArrayList<String>(friend_list11.keySet());
                            int friend_num = friendList11.size();//看他總共有幾個好友
                            friendNum = String.valueOf(friend_num);
                        }
                        Map<String, String> friendInfo = new HashMap<String, String>();
                        friendInfo.put("number", friendNum);
                        friendInfo.put("username", request_from1.get(position));
                        databaseRef.child("friends").child(request_to1.get(position)).child(request_from1.get(position)).setValue(friendInfo);

                        //更新requestFrom的好友
                        String friendNum2;
                        if(!(snapshot.child("friends/"+request_from1.get(position)).exists())){
                            friendNum2 = "0";
                        }
                        else {
                            Map<String,Map<?,?>> friend_list22 = (Map<String, Map<?, ?>>) snapshot.child("friends/"+request_from1.get(position)).getValue();
                            friendList22 =new ArrayList<String>(friend_list22.keySet());
                            int friend_num2 = friendList22.size();//看他總共有幾個好友
                            friendNum2 = String.valueOf(friend_num2);
                        }
                        Map<String, String> friendInfo2 = new HashMap<String, String>();
                        friendInfo2.put("number", friendNum2);
                        friendInfo2.put("username", request_to1.get(position));
                        databaseRef.child("friends").child(request_from1.get(position)).child(request_to1.get(position)).setValue(friendInfo2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast toast1 = Toast.makeText(mContext,"The request from "+request_from1.get(position)+" has been accepted",Toast.LENGTH_LONG);
                toast1.show();


            }
        });
        holder.viewBtn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRef = FirebaseDatabase.getInstance().getReference();
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //更新好友邀請的status
                        databaseRef.child("friend request").child(request_to1.get(position)).child(request_from1.get(position)).child("status").setValue("replied");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast toast2 = Toast.makeText(mContext,"The request from "+request_from1.get(position)+" has been denied",Toast.LENGTH_LONG);
                toast2.show();
            }
        });

        return convertView;
    }
}