package com.example.criminalscurse;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class LocalizationActivity extends AppCompatActivity {

    public LocalizationActivity() {

    }

    // We only override onCreate
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleUtils.updateConfiguration(this);
        super.attachBaseContext(newBase);
    }
}