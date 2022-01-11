package com.example.criminalscurse;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Viewhold>  {

    private List<Modelclass> userList;
    private Viewhold holder;

    public Adapter (List<Modelclass> userList){
        this.userList = userList;

    }

    @NonNull
    @Override
    public Viewhold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mView= inflater.inflate(R.layout.item_design, parent, false);

      //  Modelclass fetchdata =
        holder = new Viewhold(mView);
        return new Viewhold(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewhold holder, int position) {
        String resource = userList.get(position).getImage();
        String name = userList.get(position).getName();
        String lastseen = userList.get(position).getPlace();
        String age = userList.get(position).getAge();
        String Dangerous_Status = userList.get(position).getStatus();
        String username=userList.get(position).getUsername();
        holder.setData(resource,name,age,
                lastseen
                ,Dangerous_Status,username);
    }

    @Override
    public int getItemCount() {
       // Log.i("user",userList.size()+" " );
        return userList.size();

    }}
