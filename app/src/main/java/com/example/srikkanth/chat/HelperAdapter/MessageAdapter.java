package com.example.srikkanth.chat.HelperAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.srikkanth.chat.HelperClass.ChatMessage;
import com.example.srikkanth.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikkanth on 13/6/18.
 */

public class MessageAdapter extends ArrayAdapter<ChatMessage>{
    private Context context;
    private ImageView photoImageView;
    private List<ChatMessage> messageList=new ArrayList<>();
    public MessageAdapter(Context context, int resource, ArrayList<ChatMessage> objects) {
        super(context, resource, objects);
        this.context=context;
        messageList=objects;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView usernameTextView=(TextView)convertView.findViewById(R.id.username_text_view);
        RelativeLayout relativeLayout=(RelativeLayout)convertView.findViewById(R.id.item_rel_layout);
        ChatMessage chatMessage = getItem(position);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        boolean isPhoto =chatMessage.getPhotoUrl()!=null;
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            usernameTextView.setText(chatMessage.getUsername());
            if(chatMessage.getUsername().equals(mAuth.getCurrentUser().getDisplayName())) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeLayout.setLayoutParams(layoutParams);
                relativeLayout.setBackgroundResource(R.drawable.round_border_listview);
            }
            else{
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeLayout.setLayoutParams(layoutParams);
                relativeLayout.setBackgroundResource(R.drawable.list_view_green_round);
            }
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(chatMessage.getPhotoUrl())
                    .apply(new RequestOptions().override(600,600).centerCrop())
                    .into(photoImageView);
        } else {
            if(chatMessage.getUsername().equals(mAuth.getCurrentUser().getDisplayName())) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeLayout.setLayoutParams(layoutParams);
                relativeLayout.setBackgroundResource(R.drawable.round_border_listview);
            }
            else{
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeLayout.setLayoutParams(layoutParams);
                relativeLayout.setBackgroundResource(R.drawable.list_view_green_round);
            }
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText(chatMessage.getMessage());
            usernameTextView.setText(chatMessage.getUsername());
        }
        return convertView;
    }

}