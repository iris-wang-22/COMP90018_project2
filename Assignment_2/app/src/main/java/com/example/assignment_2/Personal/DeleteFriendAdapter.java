package com.example.assignment_2.Personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.assignment_2.Login.MainActivity;
import com.example.assignment_2.Personal.PersonalActivity;
import com.example.assignment_2.R;
import com.example.assignment_2.Util.CustomDialog;
import com.example.assignment_2.Util.ToastUtil;
import com.example.assignment_2.friendlist.friendsMode;
//import com.example.assignment_2.friendsRequest.RequestListAdapter;
//import com.example.assignment_2.friendsRequest.requestDetail;
import com.example.assignment_2.friendsRequest.RequestListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.example.assignment_2.Util.Base64Util.base64ToBitmap;

public class DeleteFriendAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private DatabaseReference databaseRef;

    private List<friendsMode> mData;
    private String self_username;
    private ViewHolder1 holder = null;

    private List<String> deleted_username = new ArrayList<String>();

    public DeleteFriendAdapter(Context context, List data, String self_username){
        this.mContext = context;

        this.mData = data;
        this.self_username = self_username;
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

    static class ViewHolder1 {
        //public ImageView imageView;
        //public TextView tvEmail, tvAge;
        public TextView df_username;
        public ImageView df_Avatar;
        public Button delete_btn;



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView==null){
            convertView = mLayoutInflater.inflate(R.layout.layout_df_list_item, null);
            friendsMode friends = mData.get(position);

            holder = new ViewHolder1();
            holder.df_username = (TextView) convertView.findViewById(R.id.df_username);
            holder.df_Avatar = (ImageView) convertView.findViewById(R.id.df_avatar);

            holder.delete_btn = (Button) convertView.findViewById(R.id.df_delete);

            holder.df_username.setText(friends.getUsername());
            if (friends.getAvatar() != null){
                holder.df_Avatar.setImageBitmap(base64ToBitmap(friends.getAvatar()));
            }

            deleted_username.add(friends.getUsername());
            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder1) convertView.getTag();
        }

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(mContext, R.style.CustomDialog);
                customDialog.setTitle("Tips").setMessage("Are you sure to delete?")
                        .setCancel("No, not sure.", new CustomDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(CustomDialog dialog) {
                                ToastUtil.showMsg(mContext,"Good choice!");
                            }
                        }).setConfirm("Yes, sure.", new CustomDialog.IOnConfirmListener() {
                    @Override
                    public void onConfirm(CustomDialog dialog) {
                        //mData.remove(position);

                        databaseRef = FirebaseDatabase.getInstance().getReference();
                        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //更新好友邀請的status
                                databaseRef.child("friends").child(self_username).child(deleted_username.get(position)).setValue(null);
                                databaseRef.child("friends").child(deleted_username.get(position)).child(self_username).setValue(null);
                                Toast toast2 = Toast.makeText(mContext,deleted_username.get(position)+" has been deleted",Toast.LENGTH_SHORT);
                                toast2.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }).setCancelable(false);
                customDialog.show();


            }
        });

        return convertView;
    }
}
