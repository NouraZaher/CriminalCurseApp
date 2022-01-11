package com.example.criminalscurse;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FirstFragment extends Fragment {

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_first, container, false);
        final ViewFlipper flipper=v.findViewById(R.id.flipper);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        SharedPreferences shared= PreferenceManager.getDefaultSharedPreferences(getActivity());
        storage.getReferenceFromUrl("gs://criminals-curse-1592081187332.appspot.com").child("stations/").child(shared.getString("station","")+"/").child("Announcements").listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                for (StorageReference s : task.getResult().getItems()) {
                    s.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            final ImageView image = new ImageView(getActivity());
                            image.setScaleType(ImageView.ScaleType.FIT_XY);
                            Picasso.get().load(task.getResult()).into(image);
                            flipper.addView(image);
                            startFlipper(flipper);
                        }
                        });
                    }
            }
        });
        return v;
    }
    public void startFlipper(ViewFlipper flipper){
        flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_up));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_from_up));
        if(flipper.getChildCount()==1||flipper.getChildCount()==0){
            flipper.stopFlipping();
        }else {
            flipper.setFlipInterval(5000);
            flipper.startFlipping();
        }
    }
}