package com.example.insien;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


import com.example.insien.Activity.HomeActivity;
import com.squareup.picasso.Picasso;

public class password_changedActivity extends AppCompatActivity {
    Button chatback;
    ImageView okanime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_changed);


        chatback=findViewById(R.id.chatback);
        okanime=findViewById(R.id.okanime);

        chatback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(password_changedActivity.this,HomeActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(password_changedActivity.this).toBundle());
                finish();
            }
        });
    }
}