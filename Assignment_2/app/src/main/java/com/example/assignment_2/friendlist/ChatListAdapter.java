package com.example.assignment_2.friendlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment_2.R;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;


    private List<friendsMode> mData;
    private ViewHolder holder = null;

    ChatListAdapter(Context context, List data){
        this.mContext = context;
        this.mData = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
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

    static class ViewHolder{
        public ImageView ivAvatar;
        public TextView tvUsername, tvRemand;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = mLayoutInflater.inflate(R.layout.layout_chatlist_item, null);
            friendsMode friends = mData.get(position);

            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tv_username);
            holder.tvRemand = (TextView) convertView.findViewById(R.id.tv_remand);

            holder.tvUsername.setText(friends.getUsername());

            if (friends.getAvatar() != null){
                holder.ivAvatar.setImageBitmap(base64ToBitmap(friends.getAvatar()));
            }

//            if(friends.getRemand() != 0){
//                holder.tvRemand.setVisibility(View.VISIBLE);
//            }

            convertView.setTag(holder);

        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}