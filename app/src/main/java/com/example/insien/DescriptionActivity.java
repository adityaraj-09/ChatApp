package com.example.insien;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.insien.Activity.loginActivity;

import java.util.Objects;

public class DescriptionActivity extends AppCompatActivity {
        TextView start;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        start=findViewById(R.id.start);

        Objects.requireNonNull(getSupportActionBar()).hide();


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DescriptionActivity.this, loginActivity.class));
                finish();
            }
        });
    }
}