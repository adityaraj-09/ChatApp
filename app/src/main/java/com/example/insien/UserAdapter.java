package com.example.insien;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.insien.Activity.ChatActivity;
import com.example.insien.Activity.HomeActivity;
import com.example.insien.Activity.Requests;
import com.example.insien.Activity.SettingActivity;
import com.example.insien.Adapter.MessagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {
    Context homeActivity;
    ArrayList<Users> usersArrayList;
    String frequest,name,status,image;
    FirebaseAuth auth;


    public UserAdapter(Context homeActivity, ArrayList<Users> usersArrayList) {
        this.homeActivity=homeActivity;
        this.usersArrayList=usersArrayList;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(List<Users> filteredList){
        this.usersArrayList= (ArrayList<Users>) filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Users users=usersArrayList.get(position);
        holder.user_name.setText(users.name);
        Picasso.get().load(users.imageUri).resize(400,400).centerCrop().into(holder.user_profile);
        FirebaseDatabase database;
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();


        if(!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(users.uid)){
            auth=FirebaseAuth.getInstance();
            FirebaseDatabase.getInstance().getReference().child("chats").child(FirebaseAuth.getInstance().getUid()+users.getUid())
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
                                holder.user_status.setText(users.getStatus());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            holder.chatl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference().child("user").child(users.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent intent=new Intent(homeActivity, ChatActivity.class);
                            if(snapshot.hasChild("FCM Token")){
                                String  token= Objects.requireNonNull(snapshot.child("FCM Token").getValue()).toString();
                                intent.putExtra("token",token);
                            }
                            intent.putExtra("name",users.getName());
                            intent.putExtra("ReceiverImage",users.getImageUri());
                            intent.putExtra("uid",users.getUid());
                            homeActivity.startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });





                }
            });

        }else{
            holder.user_status.setText("#LOGGED IN");
        }
    }
    @Override
    public int getItemCount() {
        return  usersArrayList.size();
    }
    class Viewholder extends RecyclerView.ViewHolder{
        CircleImageView user_profile;
        TextView user_name;
        TextView user_status;
        LinearLayout chatl;
        CircularStatusView circular_status_view;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            user_profile=itemView.findViewById(R.id.user_img);
            user_status=itemView.findViewById(R.id.user_status);
            user_name=itemView.findViewById(R.id.user_name);
            chatl=itemView.findViewById(R.id.chatl);
            circular_status_view=itemView.findViewById(R.id.circular_status_view);



        }
    }
}
