package com.example.insien.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insien.R;
import com.example.insien.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class registerActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText name,email,password,cpassword;
    TextView btn_register,btn_login;
    CircleImageView img;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;
    String imageURI;
    ActivityResultLauncher<String>mTakePhoto;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String currentUserID;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        auth= FirebaseAuth.getInstance();
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.login_password);
        btn_login=findViewById(R.id.btn_login);
        database= FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        cpassword=findViewById(R.id.cpassword);
        btn_register=findViewById(R.id.btn_register);
        mTakePhoto =registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageUri=result;
                        img.setImageURI(result);
                    }
                }

        );
        Objects.requireNonNull(getSupportActionBar()).hide();
        img=findViewById(R.id.img);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registerActivity.this,loginActivity.class));
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1,email1,password1,cpassword1,status;
                progressDialog.show();
                name1=String.valueOf(name.getText());
                email1=email.getText().toString();
                password1=password.getText().toString();
                cpassword1=cpassword.getText().toString();
                status = "-->Hey there";

                if(TextUtils.isEmpty(name1) || TextUtils.isEmpty(email1)|| TextUtils.isEmpty(password1) ||TextUtils.isEmpty(cpassword1)){
                    progressDialog.dismiss();
                    Toast.makeText(registerActivity.this, "enter data", Toast.LENGTH_SHORT).show();
                } else if (!email1.matches(emailPattern)) {
                    progressDialog.dismiss();
                    Toast.makeText(registerActivity.this, "enter valid email", Toast.LENGTH_SHORT).show();
                } else if (!password1.equals(cpassword1)) {
                    progressDialog.dismiss();
                    Toast.makeText(registerActivity.this, "password doesnot match", Toast.LENGTH_SHORT).show();
                } else if (password1.length()<6) {
                    progressDialog.dismiss();
                    Toast.makeText(registerActivity.this, "enter 6 digit password", Toast.LENGTH_SHORT).show();
                } else {

                    auth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                DatabaseReference reference =database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                                StorageReference storageReference= storage.getReference().child("upload").child(auth.getUid());
                                if(imageUri!=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI=uri.toString();
                                                        Users users=new Users(auth.getUid(),name1,email1,imageURI,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(registerActivity.this, "account created", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(registerActivity.this, loginActivity.class));
                                                                }else{
                                                                    Toast.makeText(registerActivity.this, "error ", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }else {
                                                progressDialog.dismiss();
                                                Toast.makeText(registerActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }else {
                                     String status = "-->Hey there";
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/insien-9497e.appspot.com/o/user.png?alt=media&token=26015f24-c215-4dfb-af9d-d50482d5ed9c";
                                    Users users=new Users(auth.getUid(),name1,email1,imageURI,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(registerActivity.this,loginActivity.class));
                                                finish();
                                            }else{
                                                Toast.makeText(registerActivity.this, "error ", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }

                            }


                        }
                    });
                }

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTakePhoto.launch("image/*");
            }
        });

    }
}




