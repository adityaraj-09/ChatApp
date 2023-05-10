package com.example.insien.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;


import com.devlomi.circularstatusview.CircularStatusView;
import com.example.insien.Adapter.StatusAdapter;
import com.example.insien.PrivacyActivity;
import com.example.insien.R;
import com.example.insien.Status;
import com.example.insien.UserStatus;
import com.example.insien.Users;
import com.example.insien.chatListActivity;
import com.example.insien.firebaseAuth;
import com.example.insien.password_changedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

public class SettingActivity extends AppCompatActivity {
    public  static final int CAMERA_CODE=200;
    public  static final int Gallery_CODE=100;
    CircleImageView setting_image;
    EditText setting_name,setting_status;
    TextView UP,coverpic,statuses;
    CircularStatusView circular_status_view;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri setimageUri,coverUri,statusUri;
    ActivityResultLauncher<String> mTakePhoto;
    ActivityResultLauncher<String> cover;
    ActivityResultLauncher<String> statusupload;
    StatusAdapter statusAdapter;

    AppCompatButton save;
    ImageView add;
    String email;
    String name,status,image;
    LinearLayout ll_privacy;
    ProgressDialog progressDialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressDialog=new ProgressDialog(this);
        circular_status_view=findViewById(R.id.circular_status_view);

        Window window = SettingActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(SettingActivity.this,R.color.status_bar));

        getWindow().setExitTransition(new Explode());
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        UP=findViewById(R.id.UP);
        add=findViewById(R.id.add);
        statuses=findViewById(R.id.statuses);
        ll_privacy=findViewById(R.id.ll_privacy);
        Objects.requireNonNull(getSupportActionBar()).hide();

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        coverpic=findViewById(R.id.coverpic);

        setting_image=findViewById(R.id.setting_image);
        setting_name=findViewById(R.id.setting_name);
        setting_status =findViewById(R.id.setting_status);
        save=findViewById(R.id.save);
        mTakePhoto =registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        setimageUri=result;
                        setting_image.setImageURI(result);
                    }
                }

        );
        statusupload =registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        statusUri=result;
                        if(statusUri!=null){
                            Date date=new Date();
                            progressDialog.setMessage("Sending Status");
                            progressDialog.show();
                            StorageReference storageReference=storage.getReference().child("Status").child(String.valueOf(date.getTime()));
                            storageReference.putFile(statusUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                progressDialog.dismiss();
                                                UserStatus userStatus=new UserStatus();
                                                userStatus.setName(name);
                                                userStatus.setProfileImage(image);
                                                userStatus.setLastupdated(date.getTime());
                                                HashMap<String,Object>obj=new HashMap<>();
                                                obj.put("name",userStatus.getName());
                                                obj.put("profileImage",userStatus.getProfileImage());
                                                obj.put("lastupdated",userStatus.getLastupdated());
                                                String imageurl=uri.toString();
                                                Status status1=new Status(imageurl,userStatus.getLastupdated());
                                                database.getReference().child("Status").child(Objects.requireNonNull(auth.getUid()))
                                                        .updateChildren(obj);

                                                database.getReference().child("Status").child(auth.getUid()).child("statuses").push()
                                                        .setValue(status1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(SettingActivity.this, "Status sent", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }

        );
        ll_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PrivacyActivity.class));
            }
        });

        cover=registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        progressDialog.show();
                        coverUri=result;
                        if(coverUri!=null){
                            StorageReference storageReference=storage.getReference().child("coverpic").child(Objects.requireNonNull(auth.getUid()));
                            DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
                            storageReference.putFile(coverUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String Uri=uri.toString();
                                            HashMap<String,Object>hashMap=new HashMap<>();
                                            hashMap.put("Cover-Pic",Uri);
                                            database.getReference().child("user").child(auth.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SettingActivity.this, "Cover Pic updated", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }else{
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SettingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                    }
                }

        );
        DatabaseReference statusref=database.getReference().child("Status").child(Objects.requireNonNull(auth.getUid()));
        statusref.keepSynced(true);
        statusref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    ArrayList<Status>statuses=new ArrayList<>();
                    for (DataSnapshot dataSnapshot1: snapshot.child("statuses").getChildren()){
                        Status sampleStatus=dataSnapshot1.getValue(Status.class);
                        statuses.add(sampleStatus);
                    }
                    circular_status_view.setVisibility(View.VISIBLE);
                    circular_status_view.setPortionsCount(statuses.size());
                    setting_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<MyStory> myStories = new ArrayList<>();
                            for(Status status1:statuses){
                                long timeStamp=status1.getTimeStamp();
                                DateFormat simple=new SimpleDateFormat("dd hh:mm aa");
                                Date result=new Date(timeStamp);
                                myStories.add(new MyStory(status1.getImageUrl(),result));
                            }
                            new StoryView.Builder((SettingActivity.this).getSupportFragmentManager())
                                    .setStoriesList(myStories) // Required
                                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                    .setTitleText(name) // Default is Hidden
                                    .setSubtitleText("INSIEN") // Default is Hidden
                                    .setTitleLogoUrl(image) // Default is Hidden
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


        statuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Add Status", "Remove Status"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("Choose an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            statusupload.launch("image/*");
                        }
                        if (i == 1) {
                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Status").child(Objects.requireNonNull(auth.getUid()));
                            Query query=reference.orderByChild("statuses");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        if(snapshot.exists()){
                                            dataSnapshot.getRef().removeValue();
                                        }else{
                                            Toast.makeText(SettingActivity.this, "No status Found", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
                builder.create().show();
            }
        });
        coverpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cover.launch("image/*");

            }
        });
        StorageReference storageReference=storage.getReference().child("uplod").child(Objects.requireNonNull(auth.getUid()));
        DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email=snapshot.child("email").getValue().toString();
                 name=snapshot.child("name").getValue().toString();
                 status=snapshot.child("status").getValue().toString();
                 image=snapshot.child("imageUri").getValue().toString();
                setting_name.setText(name);
                setting_status.setText(status);
                Picasso.get().load(image).resize(400,400).centerCrop().into(setting_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String name,status;
                name= setting_name.getText().toString();
                status= setting_status.getText().toString();

                if(setimageUri!=null){
                    storageReference.putFile(setimageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri=uri.toString();
                                    Users users=new Users(auth.getUid(),name,email,finalImageUri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                                finish();
                                            }else{
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                }else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri=uri.toString();
                            Users users=new Users(auth.getUid(),name,email,finalImageUri,status);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                changeImage();
            }


        });
        UP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

    }
    private void changeImage() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Choose an Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    openCamera();

                }
                if (i == 1) {
                    openGallery();
                }

            }


        });
        builder.create().show();

    }

    private void openGallery() {
        mTakePhoto.launch("image/*");


    }

    private void openCamera() {




    }


    private  void showChangePasswordDialog(){
        View view= LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_update_password,null);
        EditText pass=view.findViewById(R.id.pass);
        EditText npass=view.findViewById(R.id.npass);
        Button UpdateP=view.findViewById(R.id.UpdateP);
        AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();


        UpdateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword=pass.getText().toString().trim();
                String newPassword=npass.getText().toString().trim();
                if(TextUtils.isEmpty(oldPassword)){
                    Toast.makeText(SettingActivity.this, "Enter your current Password..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length()<6){
                    Toast.makeText(SettingActivity.this, "password lenth should be atleast 6", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                updatePassword(oldPassword,newPassword);

            }
        });




    }

    private void updatePassword(String oldPassword, String newPassword) {
        progressDialog.show();
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()),oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        finish();
                                        Intent intent=new Intent(SettingActivity.this, password_changedActivity.class);
                                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SettingActivity.this).toBundle());


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
