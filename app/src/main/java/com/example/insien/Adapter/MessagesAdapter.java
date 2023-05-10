package com.example.insien.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.insien.Activity.ChatActivity.SenderImage;
import static com.example.insien.Activity.ChatActivity.rImage;
import static com.example.insien.Activity.ChatActivity.senderRoom;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insien.ImageViewerActivity;
import com.example.insien.ModelClass.Messages;
import com.example.insien.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Messages>messagesArrayList;
    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;




    public MessagesAdapter(Context context,ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList=messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return  new SenderViewHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_layout_item,parent,false);
            return  new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Messages messages=messagesArrayList.get(position);
        int react[]=new int[]{
                R.drawable.frownface,
                R.drawable.heart1,
                R.drawable.hands,
                R.drawable.hearteye,
                R.drawable.butterfly,
                R.drawable.sweatface,
                R.drawable.sparkheart,
                R.drawable.stareye,
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(react)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==SenderViewHolder.class){
                SenderViewHolder viewHolder=(SenderViewHolder ) holder;
                viewHolder.feeling.setImageResource(react[pos]);
                viewHolder.feeling.setVisibility(VISIBLE);
            }else {
                ReceiverViewHolder viewHolder=(ReceiverViewHolder) holder;
                viewHolder.feeling.setImageResource(react[pos]);
                viewHolder.feeling.setVisibility(VISIBLE);
            }
            return true;
        });


        long timeStamp=messages.getTimeStamp();
        DateFormat simple=new SimpleDateFormat("dd/MM hh:mm aa");
        Date result=new Date(timeStamp);
        String dateText=simple.format(result);

        if(holder.getClass()==SenderViewHolder.class){

            SenderViewHolder viewHolder=(SenderViewHolder ) holder;

            if(messages.getType().equals("text")){
                    viewHolder.txtmessage.setText(messages.getMessage());
                viewHolder.time.setText(dateText);
            }else{
                viewHolder.lay_txt.setVisibility(GONE);
                viewHolder.lay_img.setVisibility(VISIBLE);
                viewHolder.imgt.setText(dateText);
                Picasso.get().load(messages.getMessage()).placeholder(R.drawable.user).resize(1280,1280).centerInside().into(viewHolder.msgimg);
                viewHolder.msgl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imgv",messages.getMessage());
                        context.startActivity(intent);

                    }
                });
            }

            Picasso.get().load(SenderImage).resize(400,400).centerCrop().into(viewHolder.circleImageView);
            viewHolder.txtmessage.setText(messages.getMessage());


            viewHolder.msgl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setIcon(R.drawable.delete);
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String msgTimeStamp= String.valueOf(messagesArrayList.get(position).getTimeStamp());


                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");

                            Query query=reference.orderByChild("timeStamp").equalTo(messages.getTimeStamp());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            dataSnapshot.getRef().removeValue();
                                            Toast.makeText(context, "message deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return true;

                }
            });
            if(messages.isIsseen()){
                viewHolder.seen.setText("seen");
            }else{
                viewHolder.seen.setText("Delivered");
            }

        }else{
            ReceiverViewHolder viewHolder=(ReceiverViewHolder ) holder;

            if(messages.getType().equals("text")){
                viewHolder.txtmessage.setVisibility(VISIBLE);
                viewHolder.msgimg.setVisibility(GONE);
                viewHolder.txtmessage.setText(messages.getMessage());
            }else{
                viewHolder.txtmessage.setVisibility(GONE);
                viewHolder.msgimg.setVisibility(VISIBLE);
                Picasso.get().load(messages.getMessage()).placeholder(R.drawable.user).resize(1280,1280).centerInside().into(viewHolder.msgimg);

                viewHolder.msgl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imgv",messages.getMessage());
                        context.startActivity(intent);

                    }
                });
            }
            viewHolder.txtmessage.setText(messages.getMessage());
            viewHolder.time.setText(dateText);
            Picasso.get().load(rImage).resize(400,400).centerCrop().into(viewHolder.circleImageView);
            viewHolder.msgl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String msgTimeStamp= String.valueOf(messagesArrayList.get(position).getTimeStamp());

                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");
                            Query query=reference.orderByChild("timeStamp").equalTo(messages.getTimeStamp());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        dataSnapshot.getRef().removeValue();
                                        Toast.makeText(context, "message deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                    return true;
                }
            });


        }





    }



    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
       Messages messages=messagesArrayList.get(position);
       if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(messages.getSenderId())){
           return ITEM_SEND;
       }else {
           return ITEM_RECEIVE;
       }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtmessage,seen,time,imgt;
        RelativeLayout msgl;
        ImageView msgimg,feeling;
        LinearLayout lay_img,lay_txt;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.profile_image);
            txtmessage=itemView.findViewById(R.id.txtMessages);
            seen=itemView.findViewById(R.id.seen);
            msgl=itemView.findViewById(R.id.msgl);
            time=itemView.findViewById(R.id.time);
            msgimg=itemView.findViewById(R.id.msgimg);
            feeling=itemView.findViewById(R.id.feeling);
            lay_img=itemView.findViewById(R.id.lay_img);
            lay_txt=itemView.findViewById(R.id.lay_txt);
            imgt=itemView.findViewById(R.id.imgt);

        }
    }
    class  ReceiverViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtmessage,seen,time;
        RelativeLayout msgl;
        ImageView msgimg,feeling;




        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.profile_image);
            txtmessage=itemView.findViewById(R.id.txtMessages);

            msgl=itemView.findViewById(R.id.msgl);
            time=itemView.findViewById(R.id.time);
            msgimg=itemView.findViewById(R.id.msgimg);
            feeling=itemView.findViewById(R.id.feeling);

        }
    }
}
