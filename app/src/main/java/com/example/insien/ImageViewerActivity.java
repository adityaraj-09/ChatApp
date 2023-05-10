package com.example.insien;

import static com.example.insien.Activity.ChatActivity.imageUri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ImageViewerActivity extends AppCompatActivity {

    ImageView view_img;
    String image,u_id;
    Bitmap bitmap;
    Uri u_img;
    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_image_viewer);


        view_img = findViewById(R.id.view_img);
        image = getIntent().getStringExtra("imgv");
        u_id=getIntent().getStringExtra("u_id");
        if(u_id!=null){
            FirebaseDatabase.getInstance().getReference().child("user").child(u_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    u_img= Uri.parse(Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString());
                    Picasso.get().load(u_img).into(view_img);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if(image!=null){
            Picasso.get().load(image).into(view_img);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save) {
                saveToGallery();
        }

        if (item.getItemId() == R.id.crop) {
            Intent intent=new Intent(ImageViewerActivity.this,cropActivity.class);
            intent.putExtra("cropI",image);
            startActivityForResult(intent,101);
        }
        if (item.getItemId() == R.id.share) {
                shareImage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImage() {
        bitmap =((BitmapDrawable)view_img.getDrawable()).getBitmap();
        Uri uri=getImageTOShare(bitmap);
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent,"Share Image"));
    }

    private Uri getImageTOShare(Bitmap bitmap) {
        File folder=new File(getCacheDir(),"images");
        Uri uri=null;
        try {
        folder.mkdirs();
        File file=new File(folder,"shared_image_insien.jpg");
        FileOutputStream fileOutputStream= null;

            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            uri= FileProvider.getUriForFile(this,"com.example.insien",file);
        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return uri;
    }


    private void saveToGallery() {
        bitmap =((BitmapDrawable)view_img.getDrawable()).getBitmap();

            Date date=new Date();
            String time= String.valueOf(date.getTime());

            final  File dir =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/INSIEN/");
            if(!dir.exists()){
                dir.mkdirs();
            }

            String imagename="IMG-"+time+".jpg";
            File file=new File(dir,imagename);
            OutputStream out;
            try {
                out=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
                out.close();
                Toast.makeText(this, "File Saved ", Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1 && requestCode==101){
            String result= Objects.requireNonNull(data).getStringExtra("RESULT");
            Uri resultUri=null;
            if(result!=null){
                resultUri=Uri.parse(result);
            }
            view_img.setImageURI(resultUri);
        }
    }

}    