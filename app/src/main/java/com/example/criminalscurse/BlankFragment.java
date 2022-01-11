package com.example.criminalscurse;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlankFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<Modelclass> userList;
    Adapter adapter;
    public View v;
    public FirebaseDatabase mFirebaseDatabase;
    public DatabaseReference mRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_blank, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        final ArrayList <Modelclass> list = new ArrayList<>();
       // initData();
        //initRecycleView();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
       mRef = mFirebaseDatabase.getReference("criminal/");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SharedPreferences shared= PreferenceManager.getDefaultSharedPreferences(getActivity());
                for (DataSnapshot ds:snapshot.getChildren()){
                    Modelclass data=ds.getValue(Modelclass.class);
                    if(data.getUsername().equals(shared.getString("station","Empty"))) {
                        list.add(data);
                        adapter = new Adapter(list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }
/*
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter <Modelclass, Adapter.ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Modelclass, Adapter.ViewHolder>(
                        Modelclass.class,
                        R.layout.item_design,
                        Adapter.ViewHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(Adapter.ViewHolder viewHolder, Modelclass modelclass, int i) {
                        viewHolder.setData(modelclass.getImage(),modelclass.getName(),modelclass.getAge()
                        ,modelclass.getLastSeen_Status(),modelclass.getDangerous_Status());

                    }
                };

        //setting adapter for the recycle view

      //  recyclerView.setAdapter(firebaseRecyclerAdapter);



    }
*/
    private void initRecycleView() {
       // recyclerView = v.findViewById(R.id.recyclerView);
       // layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(userList);
        recyclerView.setAdapter(adapter);

    }

   /* private void initData() {
        userList = new ArrayList<>();
        userList.add(new Modelclass(R.drawable.img1,"Criminal1","10:25 AM","Age : 42 years old","4"));
        userList.add(new Modelclass(R.drawable.img2,"Criminal1","10:25 AM","Age : 40 years old","6"));
        userList.add(new Modelclass(R.drawable.img3,"Criminal1","10:25 AM","Age : 35 years old","7"));
        userList.add(new Modelclass(R.drawable.img4,"Criminal1","10:25 AM","Age : 30 years old","1"));

    }
*/
}