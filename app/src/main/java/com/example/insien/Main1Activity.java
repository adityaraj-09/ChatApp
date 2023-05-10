package com.example.insien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class Main1Activity extends AppCompatActivity {

    BottomNavigationView nav;
    FrameLayout frame;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        Objects.requireNonNull(getSupportActionBar()).hide();
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

        Window window = Main1Activity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Main1Activity.this,R.color.status_bar));
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(Main1Activity.this, DescriptionActivity.class));
            finish();
        }

        nav=findViewById(R.id.nav);
        frame=findViewById(R.id.frame);
        replacefragment(new HomeFragment());
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Home:
                        replacefragment(new HomeFragment());
                        break;
                    case R.id.profile:
                        replacefragment(new ProfileFragment());
                        break;
                    case R.id.Setting:
                        replacefragment(new UsersFragment());
                        break;

                }
                return true;
            }
        });
    }
    private void replacefragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();
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
        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Activity").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(auth.getCurrentUser()!=null){
            updateUsersStatus("offline");
        }
    }
}