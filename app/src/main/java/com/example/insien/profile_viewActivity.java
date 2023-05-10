package com.example.insien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_viewActivity extends AppCompatActivity {

    String uname;
    String uabout;
    Uri uimg;
    String timestatus,uuid;
    TextView u_lastseen,u_name,u_about,textblock;
    CircleImageView u_image;
    ImageView cover;
    LinearLayout lblock;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    String block;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_profile_view);
        uname=getIntent().getStringExtra("uname");
        uabout=getIntent().getStringExtra("uabout");
        uimg= Uri.parse(getIntent().getStringExtra("uimg"));
        timestatus=getIntent().getStringExtra("timestatus");
        u_image=findViewById(R.id.u_image);
        u_name=findViewById(R.id.u_name);
        u_about=findViewById(R.id.u_about);
        u_lastseen=findViewById(R.id.u_lastseen);
        textblock=findViewById(R.id.textblock);
        lblock=findViewById(R.id.lblock);
        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uuid=getIntent().getStringExtra("uuid");
        cover=findViewById(R.id.cover);

        Picasso.get().load(uimg).resize(400,400).centerCrop().into(u_image);
        u_name.setText(uname);
        u_lastseen.setText(timestatus);
        u_about.setText(uabout);
        DatabaseReference reference = database.getReference().child("user").child(uuid);
        DatabaseReference blockreference = database.getReference().child("user").child(firebaseAuth.getUid());

        u_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(profile_viewActivity.this,ImageViewerActivity.class);
                i.putExtra("u_id",uuid);
                startActivity(i);
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Cover-Pic")){
                    Uri coverpic= Uri.parse((Objects.requireNonNull(snapshot.child("Cover-Pic").getValue())).toString());
                    Picasso.get().load(coverpic).into(cover);

                }else{
                    cover.setImageResource(R.drawable.bg2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        blockreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("BlockList")){
                    if(snapshot.child("BlockList").child(uuid).hasChild("BlockingStatus")){
                        block= snapshot.child("BlockList").child(uuid).child("BlockingStatus").getValue().toString();
                        textblock.setText(block);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        String blk=textblock.getText().toString();
        lblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(profile_viewActivity.this);

                builder.setIcon(R.drawable.block);
                if (block==null){
                    builder.setTitle("Block");
                    builder.setMessage("Are you sure to block"+" "+uname+"?");
                }else {
                    builder.setTitle("unblock");
                    builder.setMessage("Are you sure to unblock"+" "+uname+"?");
                }

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (block==null){
                            Blockingtodo();
                            Toast.makeText(profile_viewActivity.this, "User Blocked", Toast.LENGTH_SHORT).show();
                        } else  {
                            Query query=blockreference.child("BlockList").child(uuid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        dataSnapshot.getRef().removeValue();
                                        textblock.setText("Block");
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
                builder.create().show();
            }
        });
    }
    private void Blockingtodo(){
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("BlockingStatus","Unblock");
        hashMap.put("userid",uuid);
        database.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid())).child("BlockList").child(uuid).updateChildren(hashMap);
    }


}