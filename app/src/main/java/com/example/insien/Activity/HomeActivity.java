package com.example.insien.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insien.DescriptionActivity;
import com.example.insien.R;
import com.example.insien.UserAdapter;
import com.example.insien.Users;
import com.example.insien.chatListActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity{
    FirebaseAuth auth;
    RecyclerView mainrec;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView img_logout,chatlist;
    CircleImageView imgSetting;
    String currentUserID;
    SearchView search;
    String image;
    ImageView reqA;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Window window = HomeActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(HomeActivity.this,R.color.status_bar));




        if(auth.getUid()!=null){
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String token) {
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("FCM Token",token);
                    database.getReference().child("user").child(auth.getUid()).updateChildren(map);
                }
            });
        }



        search=findViewById(R.id.search);
        search.clearFocus();
        chatlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, chatListActivity.class));
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                fileList(newText);
                return true;
            }
        });
        usersArrayList = new ArrayList<>();
        DatabaseReference reference = database.getReference().child("user");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if(!users.getUid().equals(auth.getUid()))
                       usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference.keepSynced(true);
        if(auth.getUid()!=null){
            reference.child(Objects.requireNonNull(auth.getUid())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    image= Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();
                    Picasso.get().load(image).resize(400,400).centerCrop().into(imgSetting);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        mainrec = findViewById(R.id.mainrec);
        mainrec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(HomeActivity.this, usersArrayList);
        mainrec.setAdapter(adapter);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(HomeActivity.this, R.style.Dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                TextView yesBtn, noBtn;
                yesBtn = dialog.findViewById(R.id.yesBtn);
                noBtn = dialog.findViewById(R.id.noBtn);
                updateUsersStatus("offline");
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(HomeActivity.this, loginActivity.class));
                        finish();
                    }
                });
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        updateUsersStatus("online");
                    }
                });
                dialog.show();
            }
        });
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SettingActivity.class));
            }
        });
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, DescriptionActivity.class));
            finish();
        }
    }
    private void fileList(String text ) {
        List<Users> filteredList=new ArrayList<>();
        for(Users users:usersArrayList){
            if(users.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(users);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(this, "No data Found", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(filteredList);
        }
    }
    public void onStart(){
        super.onStart();
        if(auth.getUid()!=null)
        {
            updateUsersStatus("online");
        }
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
        currentUserID = auth.getUid();
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Activity").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }
    public void onBackPressed(){
        super.onBackPressed();
        updateUsersStatus("offline");
    }
    private  void logout(){
        Dialog dialog = new Dialog(HomeActivity.this, R.style.Dialoge);
        dialog.setContentView(R.layout.dialog_layout);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.yesBtn);
        noBtn = dialog.findViewById(R.id.noBtn);
        updateUsersStatus("offline");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(HomeActivity.this, loginActivity.class));
                finish();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                updateUsersStatus("online");
            }
        });
        dialog.show();
    }
}