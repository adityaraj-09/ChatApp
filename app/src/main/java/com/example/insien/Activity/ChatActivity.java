package com.example.insien.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.insien.Adapter.MessagesAdapter;
import com.example.insien.CallActivity;
import com.example.insien.ModelClass.Messages;
import com.example.insien.R;
import com.example.insien.SendimageActivity;
import com.example.insien.profile_viewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    public  static String ReceiverImage, ReceiverUID, ReceiverName;
    public static String SenderImage;
    public static String rImage;
    public static String senderRoom;
    public  static String receiverRoom;
    public  static String SenderUID;
    public  static Uri imageUri;
    Uri pdfuri;
    FirebaseStorage storage;
    FirebaseAuth auth;
    RelativeLayout rl;
    FloatingActionButton fbtn;

    String url;
    CircleImageView profile_image;
    TextView rname,online;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    CardView sendBtn,msg_img,voice;
    EditText editMessage;
    String currentUserID;
    String imageURI;
    String about,ReceiverToken,sendername;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;
    MessagesAdapter Adapter;
    ValueEventListener seenlistener;
    DatabaseReference chatReference;
    ActivityResultLauncher<String> mTakePhoto;
    ActivityResultLauncher<Intent> mTakePdf;
    ProgressDialog progressDialog;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout rinf,nc;
    ImageView call;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Window window = ChatActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ChatActivity.this,R.color.status_bar_chat));

        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        voice=findViewById(R.id.voice);
        rname = findViewById(R.id.rname);
        call=findViewById(R.id.call);
        nc=findViewById(R.id.nc);
        rinf=findViewById(R.id.rinf);
        fbtn=findViewById(R.id.fbtn);
        online=findViewById(R.id.online);
        sendBtn = findViewById(R.id.sendBtn);
        editMessage = findViewById(R.id.editMessage);
        msg_img=findViewById(R.id.msg_img);
        messagesArrayList = new ArrayList<>();
        ReceiverName = getIntent().getStringExtra("name");
        ReceiverImage = getIntent().getStringExtra("ReceiverImage");
        ReceiverUID = getIntent().getStringExtra("uid");
        ReceiverToken=getIntent().getStringExtra("token");
        profile_image = findViewById(R.id.profile_image);
        Picasso.get().load(ReceiverImage).resize(400, 400).centerCrop().into(profile_image);
        rname.setText(ReceiverName);
        SenderUID = firebaseAuth.getUid();
        senderRoom = SenderUID + ReceiverUID;
        receiverRoom = ReceiverUID + SenderUID;
        rl=findViewById(R.id.rl);

        mTakePhoto =registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result!=null){
                            imageUri=result;

                            Intent intent =new Intent(ChatActivity.this, SendimageActivity.class);
                            startActivity(intent);
                        }




                    }
                }


        );
        mTakePdf =registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data=result.getData();
                        if(data!=null){
                            pdfuri=data.getData();


                        }



                    }
                }

        );
        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0){
                    sendBtn.setVisibility(View.VISIBLE);
                    voice.setVisibility(View.GONE);
                    msg_img.setVisibility(View.GONE);
                }else {
                    sendBtn.setVisibility(View.GONE);
                    voice.setVisibility(View.VISIBLE);
                    msg_img.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, 20);
                }
                catch (Exception e) {
                    Toast
                            .makeText(ChatActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, CallActivity.class));
            }
        });

        messageAdapter = findViewById(R.id.messageAdapter);
        messageAdapter.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);

        Adapter = new MessagesAdapter(ChatActivity.this, messagesArrayList);
        messageAdapter.setAdapter(Adapter);
        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               sendername= Objects.requireNonNull(snapshot.child("name").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference Rreference = database.getReference().child("user").child(Objects.requireNonNull(ReceiverUID));
        Rreference.keepSynced(true);
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatReference.keepSynced(true);
        DatabaseReference stateReference=database.getReference().child("user").child(ReceiverUID);
        stateReference.keepSynced(true);
        chatReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);

                    messagesArrayList.add(messages);

                }
                if(messagesArrayList.size()==0){
                    nc.setVisibility(View.VISIBLE);
                }

                Adapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Rreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                about= Objects.requireNonNull(snapshot.child("status").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SenderImage = Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();
                rImage = ReceiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Rreference.child("BlockList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(SenderUID)){
                    rinf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String timestatus=online.getText().toString();
                            Intent intent=new Intent(ChatActivity.this, profile_viewActivity.class);
                            intent.putExtra("uname",ReceiverName);
                            intent.putExtra("uabout",about);
                            intent.putExtra("timestatus",timestatus);
                            intent.putExtra("uimg",ReceiverImage);
                            intent.putExtra("uuid",ReceiverUID);
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rreference.child("BlockList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(SenderUID)){
                            Toast.makeText(ChatActivity.this, "You are blocked by the user", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                reference.child("BlockList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(ReceiverUID)){
                            Toast.makeText(ChatActivity.this, "Unblock to send message", Toast.LENGTH_SHORT).show();

                        }else{
                            String message = editMessage.getText().toString();
                            if (message.isEmpty()) {
                                Toast.makeText(ChatActivity.this, "Enter  message", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            editMessage.setText("");
                            Date date = new Date();
                            boolean isseen = false;
                            String  type="text";

                            Messages messages = new Messages(message, SenderUID, date.getTime(),isseen,type);
                            database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference=database.getReference().child("chats")
                                    .child(senderRoom)
                                    .child("messages");
                            databaseReference.keepSynced(true);

                              databaseReference.push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database=FirebaseDatabase.getInstance();
                                            database.getReference().child("chats")
                                                    .child(receiverRoom)
                                                    .child("messages")
                                                    .push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                           nc.setVisibility(View.GONE);
                                                            sendNotification(sendername, messages.getMessage(),ReceiverToken);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    checkTypingStatus("noOne");

                }else{
                    checkTypingStatus("typing");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        stateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("userState").hasChild("state")) {
                    String state = Objects.requireNonNull(snapshot.child("userState").child("state").getValue()).toString();
                    String date = Objects.requireNonNull(snapshot.child("userState").child("date").getValue()).toString();
                    String time = Objects.requireNonNull(snapshot.child("userState").child("time").getValue()).toString();
                    if(state.equals("online")){
                        online.setText("online");
                    }else{
                        online.setText("Last Seen :" + ""+date+ "  "+time);
                    }

                }else {
                    online.setText("Offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        stateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("userState").hasChild("typingTo")){
                    String typing= Objects.requireNonNull(snapshot.child("userState").child("typingTo").getValue()).toString();

                    if(typing.equals("typing")){
                        online.setText("typing");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        msg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog=new BottomSheetDialog(ChatActivity.this,R.style.BottomSheetStyle);
                View view1= LayoutInflater.from(ChatActivity.this).inflate(R.layout.bottomsheet,(LinearLayout)findViewById(R.id.sheet));
                bottomSheetDialog.setContentView(view1);
                LinearLayout lay_img=(LinearLayout) bottomSheetDialog.findViewById(R.id.lay_img);
                LinearLayout lay_pdf=(LinearLayout) bottomSheetDialog.findViewById(R.id.lay_pdf);
                assert lay_pdf != null;
                lay_pdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        mTakePdf.launch(intent);
                    }
                });
                Objects.requireNonNull(lay_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        openGallery();
                    }
                });

                bottomSheetDialog.show();

            }

        });
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Wallpaper", "Clear Chat"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setIcon(R.drawable.menu3line);
                builder.setTitle("Choose an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            setwallp();

                        }
                        if (i == 1) {
                            clearchat();
                        }


                    }
                });
                builder.create().show();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                editMessage.setText(result.get(0));
            }
        }
    }

    void sendNotification(String name,String message,String token){

        try {
            RequestQueue queue= Volley.newRequestQueue(this);

            url="https://fcm.googleapis.com/fcm/send";
            JSONObject data=new JSONObject();
            data.put("title",name);
            data.put("body",message);
            JSONObject notificationdata=new JSONObject();
            notificationdata.put("notification",data);
            notificationdata.put("to",token);
            notificationdata.put("direct_boot_ok",true);

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,url, notificationdata, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                        public Map<String ,String > getHeaders()throws AuthFailureError{
                    HashMap<String ,String >map=new HashMap<>();
                    String key="key=AAAAlaBemgY:APA91bH-irWRIOmJ4vMrM0KLpSC-J3yzYF9X7jBoXexG-nXhdaTtoKV39MzSN1JscHFCaW4LCNj8p8PG233C016E-oDm9kz9hOZk-XbORzXuLLSXgA7V9aLDvfA-WI4EiyYj6QSeFTHe";
                    map.put("Authorization",key);
                    map.put("Content-Type","application/json");
                    return map;

                };

            };
            queue.add(request);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    private void setwallp() {
    }

    private void clearchat() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom);
        Query query=reference.child("messages");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void openGallery() {
        mTakePhoto.launch("image/*");
    }






    private void updateUsersStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);


        database.getReference().child("Activity").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }


    private  void checkTypingStatus(String typing){
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("typingTo",typing);
        database.getReference().child("user").child(SenderUID).child("userState")
                .updateChildren(hashMap);
    }
}