package com.example.insien;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.insien.Adapter.StatusAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    RecyclerView chatrec,statusrec;
    chatlistAdapter chatadapter;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<Users> chatuserArrayList;
    ArrayList<Users>filteredList;
    String sroom,rroom;
    String id;
    ArrayList<UserStatus> userStatuses;
    StatusAdapter statusAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_chat_list, container, false);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        chatrec = view.findViewById(R.id.chatrec);
        statusrec=view.findViewById(R.id.statusrec);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userStatuses=new ArrayList<>();

        DatabaseReference statusreference=database.getReference().child("Status");
        statusreference.keepSynced(true);
        statusreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserStatus status=new UserStatus();
                        status.setName(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        status.setProfileImage(Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString());
                        if(dataSnapshot.child("lastupdated").exists()){
                            status.setLastupdated(dataSnapshot.child("lastupdated").getValue(Long.class));
                        }
                        ArrayList<Status>statuses=new ArrayList<>();
                        for (DataSnapshot dataSnapshot1: dataSnapshot.child("statuses").getChildren()){
                            Status sampleStatus=dataSnapshot1.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        statusrec.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        statusAdapter=new StatusAdapter(getActivity(),userStatuses);
        statusrec.setAdapter(statusAdapter);

        chatuserArrayList=new ArrayList<>();
        chatuserArrayList.clear();
        DatabaseReference reference = database.getReference().child("user");
        DatabaseReference chatreference=database.getReference().child("chats");
        if(auth.getUid()!=null){
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatuserArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Users users=dataSnapshot.getValue(Users.class);
                        String  sroom=auth.getUid()+ Objects.requireNonNull(users).uid;
                        hasMessage(sroom,users,chatuserArrayList);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    private  void hasMessage(String sroom,Users users,ArrayList<Users> list){
        DatabaseReference chatreference=database.getReference().child("chats");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(sroom).hasChild("messages")){
                    list.add(users);
                    chatrec.setLayoutManager(new LinearLayoutManager(getActivity()));
                    chatadapter= new chatlistAdapter(getActivity(), chatuserArrayList);
                    chatrec.setAdapter(chatadapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}