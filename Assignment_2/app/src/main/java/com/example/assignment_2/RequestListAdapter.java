package com.example.assignment_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment_2.R;
import com.google.firebase.database.DatabaseReference;


public class RequestListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private DatabaseReference databaseRef;

    RequestListAdapter(Context context){
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

    /////
    static class ViewHolder1 {
        public ImageView imageView;
        public TextView rUsername, tvEmail, tvAge;

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
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder = null;
        if (convertView==null){
            convertView = mLayoutInflater.inflate(R.layout.layout_rlist_item, null);
            holder = new ViewHolder1();
            /*
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_l);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tv_username);
            holder.tvEmail = (TextView) convertView.findViewById(R.id.tv_email);
            holder.tvAge = (TextView) convertView.findViewById(R.id.tv_age);

             */
            holder.rUsername = (TextView) convertView.findViewById(R.id.r_username);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder1) convertView.getTag();
        }

        //给控件赋值
        holder.rUsername.setText("testing");
        //holder.tvEmail.setText("HuaLi@gmail.com");
        //holder.tvAge.setText("18");
        //holder.imageView.setImageResource(R.drawable.pig);
        //Glide.with(mContext).load("https://img2.chinadaily.com.cn/images/201808/09/5b6b8efea310add1c695e853.jpeg").into(holder.imageView);

        /*
        SendRequest.setOnClickListener(new View.OnClickListener(){

        });

         */


        return convertView;
    }
}