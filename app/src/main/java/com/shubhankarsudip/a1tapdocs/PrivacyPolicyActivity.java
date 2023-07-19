package com.shubhankarsudip.a1tapdocs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;



public class PrivacyPolicyActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        webView = (WebView) findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/policy.html");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PrivacyPolicyActivity.this,SettingActivity.class);
        Animatoo.animateSlideRight(this);
        startActivity(intent);
        finish();
    }

    public void goToHome (View view){
        startActivity(new Intent(this, DashboardActivity.class));
        Animatoo.animateSlideRight(this);
        finish();
    }




}