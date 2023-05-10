package com.example.insien.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insien.Main1Activity;
import com.example.insien.R;
import com.example.insien.Status;
import com.example.insien.UserStatus;
import com.example.insien.chatListActivity;
import com.example.insien.databinding.ItemStatusBinding;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;
    public StatusAdapter (Context context,ArrayList<UserStatus> userStatuses){
        this.context=context;
        this.userStatuses=userStatuses;

    }


    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        UserStatus userStatus=userStatuses.get(position);
        if(userStatus.getStatuses().size()>0){
            Status lastStatus=userStatus.getStatuses().get(userStatus.getStatuses().size()-1);
            Picasso.get().load(userStatus.getProfileImage()).into(holder.binding.image);
            holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());
            holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<MyStory> myStories = new ArrayList<>();
                    for(Status status:userStatus.getStatuses()){
                        long timeStamp=status.getTimeStamp();
                        DateFormat simple=new SimpleDateFormat("dd hh:mm aa");
                        Date result=new Date(timeStamp);
                        myStories.add(new MyStory(status.getImageUrl(),result));
                    }
                    new StoryView.Builder(((Main1Activity)context).getSupportFragmentManager())
                            .setStoriesList(myStories) // Required
                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(userStatus.getName()) // Default is Hidden
                            .setSubtitleText("INSIEN") // Default is Hidden
                            .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {

                                }

                                @Override
                                public void onTitleIconClickListener(int position) {

                                }
                            })
                            .build() // Must be called before calling show method
                            .show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return userStatuses==null?0 :userStatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{

        ItemStatusBinding binding;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemStatusBinding.bind(itemView);
        }
    }
}
