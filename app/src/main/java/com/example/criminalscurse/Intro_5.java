package com.example.criminalscurse;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * A simple {@link Fragment} subclass.
 */
public class Intro_5 extends Fragment {


    public Intro_5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_intro_5, container, false);
        RelativeLayout rel=v.findViewById(R.id.relative);
        final ScrollView scrollView=rel.findViewById(R.id.scroll);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollView.getChildAt(0).getBottom()<=(scrollView.getHeight()+scrollView.getScrollY())){
                    getActivity().findViewById(R.id.next).setVisibility(View.VISIBLE);
                }
            }
        });
        return v;
    }


}
