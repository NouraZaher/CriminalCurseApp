package com.example.criminalscurse;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

    public class Viewhold  extends RecyclerView.ViewHolder{

        private ImageView Image;
        private TextView Name;
        private TextView Age;
        private TextView LastSeen_Status;
        private TextView Dangerous_Status;

        public RatingBar ratingBar;
        public Viewhold(@NonNull View itemView) {
            super(itemView);
         //   Log.i("rating ", ""+ratingBar.getRating());
            Image = itemView.findViewById(R.id.imageview1);
            Name = itemView.findViewById(R.id.name);
            LastSeen_Status = itemView.findViewById(R.id.LastSeen_Status);
            Age = itemView.findViewById(R.id.age);
            Dangerous_Status = itemView.findViewById(R.id.dangerous_status);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
        public void setData (String resource, String name, String age, String lastseen, String dangerous_Status,String username){
            // Image.setImageResource(Integer.parseInt(resource));
            Name.setText(name);
            Age.setText(age+"");
            LastSeen_Status.setText(lastseen+"");
            Dangerous_Status.setText(dangerous_Status+"");
            FirebaseStorage storage=FirebaseStorage.getInstance();
            storage.getReference().child("stations").child(username).child("Wanted").child(resource).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Picasso.get().load(task.getResult()).into(Image);;
                }
            });
            if (Integer.parseInt(dangerous_Status) != ratingBar.getRating()){
                ratingBar.setRating(Integer.parseInt(dangerous_Status));
            }

        }
    }





