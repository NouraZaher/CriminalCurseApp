package com.example.criminalscurse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends LocalizationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this,Home.class));
        finish();
        overridePendingTransition(0,0);
    }

    @Override
    public void onBackPressed() {

    }
}
