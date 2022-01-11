package com.example.criminalscurse;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Intro_6 extends Fragment {


    public Intro_6() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_intro_6, container, false);
        TextView t=v.findViewById(R.id.done);
        Animation anim= AnimationUtils.loadAnimation(getActivity(),R.anim.wobble);
        t.startAnimation(anim);
        return v;
    }

}
