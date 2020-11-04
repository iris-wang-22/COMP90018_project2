package com.example.assignment_2.friendlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static com.example.assignment_2.Util.Base64Util.base64ToBitmap;
import static com.example.assignment_2.Util.BitmapUtils.ChangeBitmap;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    public static final int MSG_TYPE_LEFT_IMAGE=2;
    public static final int MSG_TYPE_RIGHT_IMAGE=3;


    float scaleWidth;
    float scaleHeight;

    Bitmap bm;
    boolean num=false;

    FirebaseUser firebaseUser;

    private Context mContext;
    private List<Chat> mChat;

    MessageAdapter(Context context, List<Chat> mChat){
        this.mContext = context;
        this.mChat = mChat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view,0);
        }else if (viewType == MSG_TYPE_LEFT){
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view,0);
        } else if(viewType == MSG_TYPE_RIGHT_IMAGE){
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right_image, parent, false);
            return new ViewHolder(view,1);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left_image, parent, false);
            return new ViewHolder(view,1);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        if(chat.getImage()==null){
            holder.show_message.setText(chat.getMsg());
        } else{
            bm = base64ToBitmap(chat.getImage());
            Bitmap new_bm = ChangeBitmap(bm);
            holder.show_image.setImageBitmap(new_bm);

            int width = new_bm.getWidth();
            int height = new_bm.getHeight();
            int wid = bm.getWidth();
            int hei = bm.getHeight();
            scaleWidth = ((float) wid) / width;
            scaleHeight = ((float) hei) / height;

            holder.show_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (num == false) {
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        Bitmap newBitmap = Bitmap.createBitmap(new_bm, 0, 0,
                                new_bm.getWidth(), new_bm.getHeight(), matrix, true);
                                holder.show_image.setImageBitmap(newBitmap);
                        num = true;
                    } else {
                        Matrix matrix = new Matrix();
                        matrix.postScale(1.0f, 1.0f);
                        Bitmap newBitmap = Bitmap.createBitmap(new_bm, 0, 0,
                                new_bm.getWidth(), new_bm.getHeight(), matrix, true);
                        holder.show_image.setImageBitmap(newBitmap);
                        num = false;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView show_image;

    public ViewHolder(@NonNull View itemView, int i) {
        super(itemView);
        if(i==0){
            show_message = itemView.findViewById(R.id.show_message);
        } else {
            show_image = itemView.findViewById(R.id.show_image);
        }

    }
}


    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(mChat.get(position).getUsername())){

            if(mChat.get(position).getImage()== null){
                return MSG_TYPE_RIGHT;
            } else{
                return MSG_TYPE_RIGHT_IMAGE;
            }
        }else {
            if(mChat.get(position).getImage()== null){
                return MSG_TYPE_LEFT;
            } else{
                return MSG_TYPE_LEFT_IMAGE;
            }
        }
    }

}