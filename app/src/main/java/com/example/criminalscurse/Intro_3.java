package com.example.criminalscurse;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Intro_3 extends Fragment {
boolean flag;
    public Intro_3() {
        flag=false;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_intro_3, container, false);
        Animation animation= AnimationUtils.loadAnimation(getActivity(),R.anim.blink);
        CheckBox camera=view.findViewById(R.id.cameracheck);
        CheckBox location=view.findViewById(R.id.locationcheck);
        CheckBox storage=view.findViewById(R.id.storagecheck);
        CheckBox microphone=view.findViewById(R.id.microphonecheck);
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            location.setChecked(false);
            location.startAnimation(animation);
        }else{
            location.setChecked(true);
            location.setEnabled(false);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            storage.setChecked(false);
            storage.startAnimation(animation);
        }else{
            storage.setChecked(true);
            storage.setEnabled(false);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            camera.setChecked(false);
            camera.startAnimation(animation);
        }else{
            camera.setChecked(true);
            camera.setEnabled(false);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            microphone.setChecked(false);
            microphone.startAnimation(animation);
        }else{
            microphone.setChecked(true);
            microphone.setEnabled(false);
        }
if(location.isChecked()&&camera.isChecked()&&storage.isChecked()&&microphone.isChecked()){
    getActivity().findViewById(R.id.next).setVisibility(View.VISIBLE);
}
camera.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        requestPermissions(new String[] {Manifest.permission.CAMERA},1);
        flag=true;

    }
});
storage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        flag=true;
    }
});
location.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
        flag=true;
    }
});
microphone.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},4);
        flag=true;
    }
});
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(flag==true) {
            FragmentManager manager=getFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.replace(R.id.frag,new Intro_3());
            transaction.commit();
            flag=false;
        }
    }
}
