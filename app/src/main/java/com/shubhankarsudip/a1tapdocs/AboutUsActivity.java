package com.shubhankarsudip.a1tapdocs;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import spencerstudios.com.bungeelib.Bungee;

import android.os.Bundle;
import android.widget.Toolbar;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);





        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("In our App a user can upload their all necessary things and they can access those from anywhere anytime without any trouble. Store As much files you want.")
                .addItem(new Element().setTitle("Version 1.0").setGravity(Gravity.CENTER))
                .addGroup("Developer - Shubhankar Das")
                .addGroup("Idea - Sanket Tarafder, Sudip Maiti, Pritam Saha, Shubhankar Das\n" +
                        "College - Kalyani Government Engineering College, West Bengal")
                .addGroup("CONNECT WITH US!")
                .addEmail("shubhankardas.kgec@gmail.com")
                .addWebsite("https://picsum.photos/200")
                .addYoutube("www.youtube.com/channel/UCmEm0I1-9ZreRE38VKbRkmg")   //Enter your youtube link here (replace with my channel link)
                .addPlayStore("com.example.yourprojectname")   //Replace all this with your package name
                .addInstagram("www.instagram.com/_iamshubhankar/")    //Your instagram id
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by Shubhankar", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AboutUsActivity.this,DashboardActivity.class);
        startActivity(intent);
        Bungee.slideRight(this);
        finish();
    }

    public void goToHome (View view){
        startActivity(new Intent(this, DashboardActivity.class));
        Bungee.inAndOut(this);

    }
}

