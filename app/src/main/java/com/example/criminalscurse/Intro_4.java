package com.example.criminalscurse;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class Intro_4 extends Fragment {
    static FirebaseFirestore database;
    static int count;

    public Intro_4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro_4, container, false);
        final Button clear1=view.findViewById(R.id.clear1);
        final Button clear2=view.findViewById(R.id.clear2);
        final EditText username = view.findViewById(R.id.username);
        final EditText password = view.findViewById(R.id.pass);
        clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("");
            }
        });
        clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText("");
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!username.getText().toString().equals("")){
                    clear1.setVisibility(View.VISIBLE);
                }else{
                    clear1.setVisibility(View.GONE);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!password.getText().toString().equals("")){
                    clear2.setVisibility(View.VISIBLE);
                }else{
                    clear2.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    public static void submit(final Activity context, final RelativeLayout rel) {
        final EditText username = rel.findViewById(R.id.username);
        final EditText password = rel.findViewById(R.id.pass);
        final Button clear1=rel.findViewById(R.id.clear1);
        final Button clear2=rel.findViewById(R.id.clear2);
        closeKeyboard(context);
        final ProgressBar progressBar;
        final TextView info;
        final TableLayout table;
        count = 0;
        TextView textt = rel.findViewById(R.id.invalid);
        textt.clearAnimation();
        textt.setVisibility(View.GONE);
        progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setId(View.generateViewId());
        username.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        clear1.setVisibility(View.GONE);
        clear2.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        info = new TextView(context);
        info.setText(context.getString(R.string.signing));
        Typeface typeface = ResourcesCompat.getFont(context, R.font.acrade);
        info.setTypeface(typeface);
        info.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.BELOW, progressBar.getId());
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rel.addView(info, params1);
        rel.addView(progressBar, params);
        database = FirebaseFirestore.getInstance();
        final String usertext = username.getText().toString();
        final String passtext = password.getText().toString();
        database.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getData().get("username").equals(usertext) && document.getData().get("password").equals(passtext)) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", usertext);
                        editor.putString("password", passtext);
                        editor.putString("station",document.getData().get("station").toString());
                        editor.apply();
                        count++;
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                info.setVisibility(View.INVISIBLE);
                username.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                username.setText("");
                password.setText("");
                clear1.setVisibility(View.GONE);
                clear2.setVisibility(View.GONE);
                if (count == 1) {
                    ((Introduction) context).setPosition(5);
                    ((Introduction) context).group.clearCheck();
                    ((Introduction) context).r5.setChecked(true);
                    ((Introduction) context).transaction = ((Introduction) context).manager.beginTransaction();
                    ((Introduction) context).transaction.setCustomAnimations(R.anim.animation_enter, R.anim.animation_leave);
                    ((Introduction) context).transaction.replace(R.id.frag, new Intro_5());
                    ((Introduction) context).transaction.commit();
                    ((Introduction) context).back.setVisibility(View.VISIBLE);
                    ((Introduction) context).next.setVisibility(View.INVISIBLE);
                } else if (count == 0) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink);
                    TextView textt = rel.findViewById(R.id.invalid);
                    textt.setVisibility(View.VISIBLE);
                    textt.startAnimation(animation);
                }
            }
        });


    }

    private static void closeKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
