package com.example.insien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insien.Activity.ChatActivity;
import com.example.insien.Activity.HomeActivity;
import com.example.insien.Adapter.BlockAdapter;
import com.example.insien.ModelClass.Blocked;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PrivacyActivity extends AppCompatActivity {

    ArrayList<Blocked> blocklist;
    FirebaseDatabase database;
    BlockAdapter adapter;
    FirebaseAuth auth;
    RecyclerView brec;
    TextView blocknum,profile,about,lastseen,online;
    LinearLayout ll_block,ll_rec;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        ll_block=findViewById(R.id.ll_block);
        ll_rec=findViewById(R.id.ll_rec);
        lastseen=findViewById(R.id.lastseen);
        about=findViewById(R.id.about);
        profile=findViewById(R.id.profile);
        online=findViewById(R.id.online);


        blocknum=findViewById(R.id.blocknum);
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Show", "Hide"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivacyActivity.this);
                builder.setTitle("Choose an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {

                            updateprivacy1("Show");
                        }
                        if (i == 1) {
                            updateprivacy1("Hide");
                        }


                    }
                });
                builder.create().show();
            }
        });
        lastseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Show", "Hide"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivacyActivity.this);
                builder.setTitle("Choose an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {

                            updateprivacy2("Show");
                        }
                        if (i == 1) {
                            updateprivacy2("Hide");
                        }


                    }
                });
                builder.create().show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Show", "Hide"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivacyActivity.this);
                builder.setTitle("Choose an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {

                            updateprivacy3("Show");
                        }
                        if (i == 1) {
                            updateprivacy3("Hide");
                        }


                    }
                });
                builder.create().show();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Show", "Hide"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivacyActivity.this);
                builder.setTitle("Choose an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {

                            updateprivacy4("Show");
                        }
                        if (i == 1) {
                            updateprivacy4("Hide");
                        }


                    }
                });
                builder.create().show();
            }
        });

        ll_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_rec.setVisibility(View.VISIBLE);
                ll_block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_rec.setVisibility(View.GONE);
                    }
                });
            }
        });
        blocklist = new ArrayList<>();
        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid())).child("BlockList");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    blocklist.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Blocked blocked = dataSnapshot.getValue(Blocked.class);
                        blocklist.add(blocked);

                    }
                    blocknum.setText(""+blocklist.size()+" "+"blocked person");
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        brec = findViewById(R.id.brec);
        brec.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapter = new BlockAdapter(PrivacyActivity.this, blocklist);
        brec.setAdapter(adapter);
    }
    private void updateprivacy1(String value) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("Online",value);
        database.getReference().child("Privacy").child(auth.getUid()).updateChildren(hashMap);
    }private void updateprivacy2(String value) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("LastSeen",value);
        database.getReference().child("Privacy").child(auth.getUid()).updateChildren(hashMap);
    }private void updateprivacy3(String value) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("Profile_Image",value);
        database.getReference().child("Privacy").child(auth.getUid()).updateChildren(hashMap);
    }private void updateprivacy4(String value) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("About",value);
        database.getReference().child("Privacy").child(auth.getUid()).updateChildren(hashMap);
    }
}