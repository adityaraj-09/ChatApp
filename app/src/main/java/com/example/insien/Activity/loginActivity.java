package com.example.insien.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insien.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class loginActivity extends AppCompatActivity {

    TextView login,btn_reg;
    EditText login_password,login_email;
    TextView fp;
    ImageButton image;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    CircleImageView back;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login=findViewById(R.id.login);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        login_email=findViewById(R.id.email);
        login_password=findViewById(R.id.login_password);
        btn_reg=findViewById(R.id.btn_reg);
        back=findViewById(R.id.back);
        auth=FirebaseAuth.getInstance();

        fp=findViewById(R.id.fp);
        Objects.requireNonNull(getSupportActionBar()).hide();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginActivity.this, registerActivity.class));
                finish();
            }
        });
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=login_email.getText().toString().trim();
                String password=login_password.getText().toString();
                progressDialog.show();
                if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    login_email.setError("enter email");
                    Toast.makeText(loginActivity.this, "email not given", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    login_email.setError("mail must contain @ and .");
                    Toast.makeText(loginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                } else if (password.length()<6) {
                    progressDialog.dismiss();
                    login_password.setError("enter atleast 6 character password");
                    Toast.makeText(loginActivity.this, "please enter 6 digit password", Toast.LENGTH_SHORT).show();

                }else {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity((new Intent(loginActivity.this, HomeActivity.class)));
                                finish();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "invalid details", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(loginActivity.this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(loginActivity.this);
        EditText emailET=new EditText(loginActivity.this);
        emailET.setHint("Email");
        emailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailET.setMinEms(16);
        linearLayout.addView(emailET);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  email=emailET.getText().toString().trim();
                beginRecovery(email);

            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });
        builder.create().show();


    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(loginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(loginActivity.this, "Failed....", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(loginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}