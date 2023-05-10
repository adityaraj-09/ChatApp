package com.example.insien.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insien.ModelClass.Blocked;
import com.example.insien.PrivacyActivity;
import com.example.insien.R;
import com.example.insien.databinding.ItemBlockBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder>{
    Context privacyActivity;
    ArrayList<Blocked> blocklist;
    String name;
    String photo;

    public BlockAdapter(PrivacyActivity privacyActivity, ArrayList<Blocked> blocklist) {
        this.privacyActivity=privacyActivity;
        this.blocklist=blocklist;
    }

    @NonNull
    @Override
    public BlockAdapter.BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(privacyActivity).inflate(R.layout.item_block,parent,false);
        return new BlockAdapter.BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockAdapter.BlockViewHolder holder, int position) {
        Blocked blocked=blocklist.get(position);
        if(blocklist.size()>0){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(blocked.getUserid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name= Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    photo= Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();
                    Picasso.get().load(photo).resize(400,400).centerCrop().into(holder.blockBinding.img);
                    holder.blockBinding.username.setText(name);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    @Override
    public int getItemCount() {
         return blocklist==null?0 :blocklist.size();
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder{
        ItemBlockBinding blockBinding;
        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);
            blockBinding=ItemBlockBinding.bind(itemView);
        }
    }
}
