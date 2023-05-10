package com.example.insien;

import static android.net.Uri.parse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class cropActivity extends AppCompatActivity {

    String cropI;
    Uri resultUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if(getIntent().getExtras()!=null){
            cropI=getIntent().getStringExtra("cropI");
            Uri img= parse(cropI);
            if(img!=null){
                String dest_uri= UUID.randomUUID().toString() + ".jpg";

                UCrop.Options options=new UCrop.Options();
                UCrop.of(img, Uri.fromFile(new File(getCacheDir(),dest_uri)))
                        .withOptions(options)
                        .withAspectRatio(0,0)
                        .useSourceImageAspectRatio()
                        .withMaxResultSize(2000,2000)
                        .start(cropActivity.this);
            }
            else{
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            }

        }






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP ){
            final Uri resultUri=UCrop.getOutput(Objects.requireNonNull(data));
            Intent returnIntent=new Intent();
            returnIntent.putExtra("RESULT",resultUri+"");
            setResult(-1,returnIntent);
            finish();
        }
        else if(resultCode==UCrop.RESULT_ERROR){
            final Throwable croperror=UCrop.getError(data);
        }

    }
}