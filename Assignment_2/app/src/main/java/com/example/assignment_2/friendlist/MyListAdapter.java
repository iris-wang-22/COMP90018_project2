package com.example.assignment_2.friendlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.assignment_2.R;

import java.util.List;

public class MyListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<friendmode> mData;


    MyListAdapter(Context context, List data){
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
        public ImageView imageView;
        public TextView tvUsername, tvEmail, tvAge;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = mLayoutInflater.inflate(R.layout.layout_flist_item, null);

            friendmode friend = mData.get(position);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_l);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tv_username);
            holder.tvEmail = (TextView) convertView.findViewById(R.id.tv_gender);
            holder.tvAge = (TextView) convertView.findViewById(R.id.tv_age);

            holder.tvUsername.setText(friend.getNickname());
            holder.tvEmail.setText(friend.getDescribe());

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        //给控件赋值
//        holder.tvUsername.setText(friend.getNickname());
//        holder.tvEmail.setText(friend.getDescribe());
        holder.tvAge.setText("18");
        holder.imageView.setImageResource(R.drawable.pig);
        //Glide.with(mContext).load("https://img2.chinadaily.com.cn/images/201808/09/5b6b8efea310add1c695e853.jpeg").into(holder.imageView);

        return convertView;
    }


    public class friendmode {
        private int avatar;
        private String nickname;
        private String describe;

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getAvatar() {
            return avatar;
        }

        public void setAvatar(int avatar) {
            this.avatar = avatar;
        }


    }

}