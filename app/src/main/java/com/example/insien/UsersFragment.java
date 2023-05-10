package com.example.insien;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insien.Activity.HomeActivity;
import com.example.insien.Activity.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersFragment extends Fragment {
    FirebaseAuth auth;
    RecyclerView mainrec;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView img_logout,chatlist;
    CircleImageView imgSetting;
    String currentUserID;
    SearchView search;
    String image;
    ImageView reqA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_home, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        search=view.findViewById(R.id.search);
        search.clearFocus();


        mainrec = view.findViewById(R.id.mainrec);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                fileList(newText);
                return true;
            }
        });
        usersArrayList = new ArrayList<>();
        DatabaseReference reference = database.getReference().child("user");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if(!users.getUid().equals(auth.getUid()))
                        usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference.keepSynced(true);


        mainrec.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserAdapter(getActivity(), usersArrayList);
        mainrec.setAdapter(adapter);

    }
    private void fileList(String text ) {
        List<Users> filteredList=new ArrayList<>();
        for(Users users:usersArrayList){
            if(users.getName().toLowerCase().startsWith(text.toLowerCase())){
                filteredList.add(users);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(getActivity(), "No data Found", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(filteredList);
        }
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
        currentUserID = auth.getUid();
        database = FirebaseDatabase.getInstance();
        if(auth.getUid()!=null){
            database.getReference().child("Activity").child(currentUserID).child("userState")
                    .updateChildren(onlineStateMap);
        }

    }
    private  void logout(){
        Dialog dialog = new Dialog(getActivity(), R.style.Dialoge);
        dialog.setContentView(R.layout.dialog_layout);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.yesBtn);
        noBtn = dialog.findViewById(R.id.noBtn);
        updateUsersStatus("offline");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getActivity(), loginActivity.class));
                if(getActivity()!=null){
                    getActivity().finish();
                }

            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                updateUsersStatus("online");
            }
        });
        dialog.show();
    }
}