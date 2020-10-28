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

public class MyListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    MyListAdapter(Context context){
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
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
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_l);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tv_username);
            holder.tvEmail = (TextView) convertView.findViewById(R.id.tv_email);
            holder.tvAge = (TextView) convertView.findViewById(R.id.tv_age);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        //给控件赋值
        holder.tvUsername.setText("Hua Li");
        holder.tvEmail.setText("HuaLi@gmail.com");
        holder.tvAge.setText("18");
        holder.imageView.setImageResource(R.drawable.pig);
        //Glide.with(mContext).load("https://img2.chinadaily.com.cn/images/201808/09/5b6b8efea310add1c695e853.jpeg").into(holder.imageView);

        return convertView;
    }
}