package com.example.insien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.insien.Activity.HomeActivity;
import com.example.insien.Adapter.StatusAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class chatListActivity extends AppCompatActivity {
    RecyclerView chatrec,statusrec;
    chatlistAdapter chatadapter;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<Users>chatuserArrayList;
    ArrayList<Users>filteredList;
    String sroom,rroom;
    String id;
    ArrayList<UserStatus> userStatuses;
    StatusAdapter statusAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        userStatuses=new ArrayList<>();
        statusAdapter=new StatusAdapter(this,userStatuses);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Window window = chatListActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(chatListActivity.this,R.color.status_bar));
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(chatListActivity.this, DescriptionActivity.class));
            finish();
        }


        chatrec = findViewById(R.id.chatrec);
        statusrec=findViewById(R.id.statusrec);
        statusrec.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        statusrec.setAdapter(statusAdapter);

        DatabaseReference statusreference=database.getReference().child("Status");
        statusreference.keepSynced(true);
        statusreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserStatus status=new UserStatus();
                        status.setName(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        status.setProfileImage(Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString());
                        if(dataSnapshot.child("lastupdated").exists()){
                            status.setLastupdated(dataSnapshot.child("lastupdated").getValue(Long.class));
                        }
                        ArrayList<Status>statuses=new ArrayList<>();
                        for (DataSnapshot dataSnapshot1: dataSnapshot.child("statuses").getChildren()){
                            Status sampleStatus=dataSnapshot1.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        chatuserArrayList=new ArrayList<>();
        DatabaseReference reference = database.getReference().child("user");
        DatabaseReference chatreference=database.getReference().child("chats");
        if(auth.getUid()!=null){
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatuserArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Users users=dataSnapshot.getValue(Users.class);
                        String  sroom=auth.getUid()+ Objects.requireNonNull(users).uid;
                        hasMessage(sroom,users,chatuserArrayList);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }



    }
    private  void hasMessage(String sroom,Users users,ArrayList<Users> list){
        DatabaseReference chatreference=database.getReference().child("chats");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(sroom).hasChild("messages")){
                    list.add(users);
                    chatrec.setLayoutManager(new LinearLayoutManager(chatListActivity.this));
                    chatadapter= new chatlistAdapter(chatListActivity.this, chatuserArrayList);
                    chatrec.setAdapter(chatadapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void updateUsersStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);
        String currentUserID;
        currentUserID = auth.getUid();
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Activity").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }
    public void onBackPressed(){
        super.onBackPressed();
        if(auth.getCurrentUser()!=null){
            updateUsersStatus("offline");
        }

    }
}