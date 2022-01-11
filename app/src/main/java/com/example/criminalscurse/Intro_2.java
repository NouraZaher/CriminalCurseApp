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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Intro_2 extends Fragment {


    public Intro_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int selecteditem;
        View layout= inflater.inflate(R.layout.fragment_intro_2, container, false);
        ListView v=layout.findViewById(R.id.list);
        RelativeLayout rel=layout.findViewById(R.id.rel);
       fetch f=new fetch(getActivity(),v,rel);
       f.execute();
        return layout;
    }


}
class fetch extends AsyncTask<Void,Void,Void> {
    ProgressBar progressBar;
    Activity context;
    RelativeLayout rel;
    ListView v;
    int selecteditem;
    TextView info;
    ArrayList<String> languages;
    public fetch(Activity context, ListView v,RelativeLayout rel){
        this.context=context;
        this.v=v;
        this.rel=rel;
        selecteditem=-1;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        languages=new ArrayList<>();
        progressBar=new ProgressBar(context);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setId(View.generateViewId());
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        info=new TextView(context);
        info.setText(context.getString(R.string.fetchlan));
        Typeface typeface= ResourcesCompat.getFont(context,R.font.acrade);
        info.setTypeface(typeface);
        info.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.BELOW,progressBar.getId());
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rel.addView(info,params1);
        rel.addView(progressBar,params);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Document doc = Jsoup.connect("https://cloud.google.com/translate/docs/languages").get();
            Element b=doc.select("body").get(0);
            Element table=b.select("table").get(0);
            Element tr=table.select("tbody").get(0);
            Elements rows=tr.select("tr");
            for(int i=1;i<rows.size();i++){
                String y=rows.get(i).select("td").get(0).text();
                if(y.contains("(")){
                    y=y.substring(0,y.indexOf("("));
                }
                String x=rows.get(i).select("td").get(1).text();
                if(x.contains("(")) {
                    x = x.substring(0, x.indexOf("("));
                }
                if(x.contains(" or")&&!x.equals("or")){
                    x = x.substring(0, x.indexOf("or"));
                }
                languages.add(y+" ("+x+")");
            }
        }catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        final LanguageAdapter adapter=new LanguageAdapter(context,languages,selecteditem);
        v.setAdapter(adapter);
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(R.id.text);
                String language = (String) text.getText();
                selecteditem = position;
                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);;
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("language", language);
                editor.apply();
                adapter.changeSelectedItem(selecteditem);
                adapter.notifyDataSetChanged();
            }
        });
        progressBar.setVisibility(View.INVISIBLE);
        info.setVisibility(View.INVISIBLE);
        Animation anim= AnimationUtils.loadAnimation(context,R.anim.zoomin);
        anim.setDuration(450);
        v.startAnimation(anim);
    }
}
class LanguageAdapter extends ArrayAdapter {
    ArrayList<String> lan;
    Context context;
    int selecteditem;
    public LanguageAdapter(@NonNull Context context,ArrayList<String> lan,int selecteditem) {
        super(context, 0,lan);
        this.lan=lan;
        this.context=context;
       this.selecteditem=selecteditem;
    }
public void changeSelectedItem(int s){
       selecteditem=s;
}
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.languages,parent,false);
        }
        String c=lan.get(position);
        if(selecteditem==position){
            TextView t=convertView.findViewById(R.id.text);
            LinearLayout rel=convertView.findViewById(R.id.main);
            t.setText(c);
            rel.setBackgroundColor(Color.WHITE);
            t.setTextColor(Color.BLACK);
            return convertView;
        }else {
            TextView t = convertView.findViewById(R.id.text);
            t.setText(c);
            LinearLayout rel = convertView.findViewById(R.id.main);
            rel.setBackgroundColor(Color.BLACK);
            t.setTextColor(Color.WHITE);
            return convertView;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

