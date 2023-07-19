package com.shubhankarsudip.a1tapdocs.Activity;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity  {
    private static final String TAG = "BaseActivity";

    public void recreateActivity() {
        this.recreate();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}


