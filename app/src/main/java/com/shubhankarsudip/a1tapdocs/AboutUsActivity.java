package com.shubhankarsudip.a1tapdocs;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Hey welcome to 1TapDOCs. Make sure to rate us on PlayStore")
                .addItem(new Element().setTitle("Version 1.0.5").setGravity(Gravity.CENTER))
                .addGroup("Developer - Shubhankar Das")
                .addGroup("Idea - Sanket Tarafder, Sudip Maiti, Pritam Saha, Shubhankar Das\n" +
                        "\n" +
                        "College - Kalyani Government Engineering College, West Bengal")
                .addGroup("CONNECT WITH US!")
                .addEmail("shubhankardas.kgec@gmail.com")
                .addWebsite("https://matias.ma/nsfw/")
                .addYoutube("https://www.youtube.com/channel/UCmEm0I1-9ZreRE38VKbRkmg/featured")   //Enter your youtube link here (replace with my channel link)
                .addPlayStore("https://play.google.com/store/apps/details?id=com.shubhankarsudip.a1tapdocs")   //Replace all this with your package name
                .addInstagram("_iamshubhankar")    //Your instagram id
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by @Shubhankar", Calendar.getInstance().get(Calendar.YEAR));
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
        Animatoo.animateSlideRight(this);
        finish();
    }

    public void goToHome (View view){
        startActivity(new Intent(this, DashboardActivity.class));
        Animatoo.animateSlideRight(this);

    }
}

