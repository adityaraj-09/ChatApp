package com.example.insien;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.insien.Activity.ChatActivity.ReceiverName;
import static com.example.insien.Activity.ChatActivity.SenderUID;
import static com.example.insien.Activity.ChatActivity.receiverRoom;
import static com.example.insien.Activity.ChatActivity.senderRoom;
import static com.example.insien.Activity.ChatActivity.imageUri;



import com.example.insien.Activity.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class SendimageActivity extends AppCompatActivity {

    ProgressBar send_pb;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ImageView sendimg;
    TextView warning;
    Button send_btn;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendimage);


        send_pb=findViewById(R.id.send_pb);
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        sendimg=findViewById(R.id.sendimg);
        warning=findViewById(R.id.warning);
        send_btn=findViewById(R.id.send_btn);
        auth=FirebaseAuth.getInstance();

        send_btn.setText("Send Image to"+" "+ReceiverName);

        Picasso.get().load(imageUri).into(sendimg);


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.setVisibility(View.VISIBLE);
                sendImage();
            }
        });



    }

    private void sendImage() {

        if(imageUri!=null){
            send_pb.setVisibility(View.VISIBLE);
            storage=FirebaseStorage.getInstance();
            Date date = new Date();
            String f= String.valueOf(date.getTime());
            StorageReference storageReference= storage.getReference().child("chatimage").child(Objects.requireNonNull(auth.getUid())).child(f);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    String downloadurl=uriTask.getResult().toString();
                    if(uriTask.isSuccessful()){
                        database=FirebaseDatabase.getInstance();
                        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
                        Date date = new Date();

                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("message",downloadurl);
                        hashMap.put("timeStamp",date.getTime());
                        hashMap.put("senderId",SenderUID);
                        boolean isseen = false;
                        hashMap.put("isseen",isseen);
                        hashMap.put("type","image");
                        chatReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database=FirebaseDatabase.getInstance();
                                DatabaseReference chatReference = database.getReference().child("chats").child(receiverRoom).child("messages");
                                chatReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SendimageActivity.this, "Image sent", Toast.LENGTH_SHORT).show();
                                        send_pb.setVisibility(View.INVISIBLE);
                                        warning.setVisibility(View.INVISIBLE);

                                    }
                                });
                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        }else{

            Toast.makeText(SendimageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save) {
        }

        if (item.getItemId() == R.id.crop) {

        }
        if (item.getItemId() == R.id.share) {
            Toast.makeText(this, "Delete Image", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}