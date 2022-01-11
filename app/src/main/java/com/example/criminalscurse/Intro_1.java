package com.example.criminalscurse;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Intro_1 extends Fragment {


    public Intro_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_intro_1, container, false);
        Animation anim= AnimationUtils.loadAnimation(getActivity(),R.anim.wobble);
        TextView textView=layout.findViewById(R.id.welcome);
        textView.startAnimation(anim);
        return layout;
    }

}
