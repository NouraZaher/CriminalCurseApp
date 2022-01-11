package com.example.criminalscurse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Introduction extends LocalizationActivity {
    SharedPreferences shared;
    SharedPreferences.Editor editor;
FragmentManager manager;
FragmentTransaction transaction;
RadioButton r1;
RadioButton r2;
RadioButton r3;
RadioButton r4;
RadioButton r5;
RadioButton r6;
RadioGroup group;
int position;
ImageView back;
ImageView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        shared= PreferenceManager.getDefaultSharedPreferences(this);
        editor=shared.edit();
        r1=findViewById(R.id.radio1);
        r2=findViewById(R.id.radio2);
        r3=findViewById(R.id.radio3);
        r4=findViewById(R.id.radio4);
        r5=findViewById(R.id.radio5);
        r6=findViewById(R.id.radio6);
        group=findViewById(R.id.radiogroup);
         next=findViewById(R.id.next);
         back=findViewById(R.id.Back);
        position=1;
        manager=getSupportFragmentManager();
        transaction=manager.beginTransaction();
        transaction.replace(R.id.frag,new Intro_1());
        transaction.commit();

        next.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    next.setBackgroundResource(R.drawable.arrow_right_hold);
                    return false;
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    next.setBackgroundResource(R.drawable.arrow_right);
                    return false;
                }
                return false;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer=MediaPlayer.create(Introduction.this,R.raw.swipe);
                mediaPlayer.start();
switch(position){
    case 1:
        position=2;
        group.clearCheck();
        r2.setChecked(true);
        transaction=manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_enter, R.anim.animation_leave);
        transaction.replace(R.id.frag,new Intro_2());
        transaction.commit();
        back.setVisibility(View.VISIBLE);
        break;
    case 2:
        position=3;
        group.clearCheck();
        r3.setChecked(true);
        transaction=manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_enter, R.anim.animation_leave);
        transaction.replace(R.id.frag,new Intro_3());
        transaction.commit();
        next.setVisibility(View.INVISIBLE);
        String defaultlan=shared.getString("lan",getResources().getConfiguration().getLocales().get(0).getISO3Language());
        String newlan=shared.getString("language",getResources().getConfiguration().getLocales().get(0).getISO3Language());
        if(!defaultlan.equals(newlan)){
        shared.edit().putString("lan",newlan+"").commit();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=getIntent();
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    System.exit(0);
                }
            },500);
        }
        break;
    case 3:
        position=4;
        group.clearCheck();
        r4.setChecked(true);
        transaction=manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_enter, R.anim.animation_leave);
        transaction.replace(R.id.frag,new Intro_4());
        transaction.commit();
        back.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        break;
    case 4:
        Intro_4.submit(Introduction.this,(RelativeLayout)findViewById(R.id.relative));
        break;
    case 5:
        position=6;
        group.clearCheck();
        r6.setChecked(true);
        transaction=manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_enter, R.anim.animation_leave);
        transaction.replace(R.id.frag,new Intro_6());
        transaction.commit();
        back.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        break;
    case 6:
       shared.edit().putString("New","No").apply();
       startActivity(new Intent(Introduction.this,Home.class));
       finish();
       overridePendingTransition(0,R.anim.zoomout);
        break;
}
            }
        });
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    back.setBackgroundResource(R.drawable.arrow_left_hold);
                    return false;
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    back.setBackgroundResource(R.drawable.arrow_left);
                    return false;
                }
                return false;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer=MediaPlayer.create(Introduction.this,R.raw.swipe);
                mediaPlayer.start();
                switch(position){
                    case 2:
                        group.clearCheck();
                        r1.setChecked(true);
                        position=1;
                        transaction=manager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.animation_leave_reverse,R.anim.animation_enter_reverse);
                        transaction.replace(R.id.frag,new Intro_1());
                        transaction.commit();
                        back.setVisibility(View.INVISIBLE);
                        shared.edit().putString("language","English (en)").apply();
                        break;
                    case 3:
                        group.clearCheck();
                        r2.setChecked(true);
                        position=2;
                        transaction=manager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.animation_leave_reverse,R.anim.animation_enter_reverse);
                        transaction.replace(R.id.frag,new Intro_2());
                        transaction.commit();
                        next.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        group.clearCheck();
                        r3.setChecked(true);
                        position=3;
                        transaction=manager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.animation_leave_reverse,R.anim.animation_enter_reverse);
                        transaction.replace(R.id.frag,new Intro_3());
                        transaction.commit();
                        break;
                    case 5:
                        group.clearCheck();
                        r4.setChecked(true);
                        position=4;
                        transaction=manager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.animation_leave_reverse,R.anim.animation_enter_reverse);
                        transaction.replace(R.id.frag,new Intro_4());
                        transaction.commit();
                        next.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        editor=shared.edit();
                        editor.putString("username", "Empty");
                        editor.putString("password", "Empty");
                        editor.apply();
                        break;
                    case 6:
                        group.clearCheck();
                        r5.setChecked(true);
                        position=5;
                        transaction=manager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.animation_leave_reverse,R.anim.animation_enter_reverse);
                        transaction.replace(R.id.frag,new Intro_5());
                        transaction.commit();
                        next.setVisibility(View.INVISIBLE);
                        back.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}