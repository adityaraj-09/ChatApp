package com.example.insien;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.insien.Activity.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public  class chatlistAdapter extends RecyclerView.Adapter<chatlistAdapter.ChatViewHolder>{
    Context context;
    ArrayList<Users>  chatuserArrayList;
    FirebaseAuth auth;


    public chatlistAdapter(Context context,ArrayList<Users>chatuserArrayList){
        this.context=context;
        this.chatuserArrayList= chatuserArrayList;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_user_row,parent,false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder  holder,  int position) {
        Users users1=chatuserArrayList.get(position);
        Picasso.get().load(users1.imageUri).resize(400,400).centerCrop().into(holder.user_profile);
        holder.user_name.setText(users1.name);
        holder.user_status.setText(users1.status);
        auth=FirebaseAuth.getInstance();
        FirebaseDatabase database;
        database=FirebaseDatabase.getInstance();
        DatabaseReference statusref=database.getReference().child("Status").child(Objects.requireNonNull(users1.uid));
        statusref.keepSynced(true);
        ArrayList<Status>statuses=new ArrayList<>();
        statusref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    statuses.clear();
                    for (DataSnapshot dataSnapshot1: snapshot.child("statuses").getChildren()){
                        Status sampleStatus=dataSnapshot1.getValue(Status.class);
                        statuses.add(sampleStatus);
                    }
                    holder.circular_status_view.setVisibility(View.VISIBLE);
                    holder.circular_status_view.setPortionsCount(statuses.size());
                    holder.user_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<MyStory> myStories = new ArrayList<>();
                            for(Status status1:statuses){
                                long timeStamp=status1.getTimeStamp();
                                DateFormat simple=new SimpleDateFormat("dd hh:mm aa");
                                Date result=new Date(timeStamp);
                                myStories.add(new MyStory(status1.getImageUrl(),result));
                            }
                            new StoryView.Builder(((Main1Activity)context).getSupportFragmentManager())
                                    .setStoriesList(myStories) // Required
                                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                    .setTitleText(users1.name) // Default is Hidden
                                    .setSubtitleText("INSIEN") // Default is Hidden
                                    .setTitleLogoUrl(users1.imageUri) // Default is Hidden
                                    .setStoryClickListeners(new StoryClickListeners() {
                                        @Override
                                        public void onDescriptionClickListener(int position) {
                                        }
                                        @Override
                                        public void onTitleIconClickListener(int position) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("chats").child(FirebaseAuth.getInstance().getUid()+users1.getUid())
                .child("messages").orderByChild("timeStamp")
                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                if(Objects.requireNonNull(snapshot1.child("type").getValue()).toString().equals("text")){
                                    holder.user_status.setText(Objects.requireNonNull(snapshot1.child("message").getValue()).toString()+"...");
                                }
                                else{
                                    holder.user_status.setText("Photo");
                                }

                            }
                        }else{
                            holder.user_status.setText(users1.getStatus());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.chatl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("user").child(users1.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent=new Intent(context, ChatActivity.class);
                        if(snapshot.hasChild("FCM Token")){
                            String  token= Objects.requireNonNull(snapshot.child("FCM Token").getValue()).toString();
                            intent.putExtra("token",token);
                        }
                        intent.putExtra("name",users1.getName());
                        intent.putExtra("ReceiverImage",users1.getImageUri());
                        intent.putExtra("uid",users1.getUid());
                        context.startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            }
        });
        }

    @Override
    public int getItemCount() {
        return  chatuserArrayList==null?0 :chatuserArrayList.size();
    }


    static class ChatViewHolder extends RecyclerView.ViewHolder{
         final CircleImageView user_profile;
         final TextView user_name;
         final TextView user_status;
        LinearLayout chatl;
        CircularStatusView circular_status_view;

         public ChatViewHolder(@NonNull View itemView) {
             super(itemView);
             user_profile=itemView.findViewById(R.id.user_img);
             user_status=itemView.findViewById(R.id.user_status);
             user_name=itemView.findViewById(R.id.user_name);
             chatl=itemView.findViewById(R.id.chatl);
             circular_status_view=itemView.findViewById(R.id.circular_status_view);
         }
     }
}



