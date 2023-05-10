package com.example.insien.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.insien.R;

import java.net.MalformedURLException;
import java.net.URL;

public class CallActivity extends AppCompatActivity {

    EditText ed_room;
    Button btn_join;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        URL serverurl;
        try {
            serverurl=new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }


        ed_room=findViewById(R.id.ed_room);
        btn_join=findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
