package com.shubhankarsudip.a1tapdocs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhankarsudip.a1tapdocs.Adapters.CardViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ViewCardActivity extends AppCompatActivity {

    private RecyclerView DebitCardPart,CreditCardPart;
    private LinearLayout DebitNoData, CreditNoData;
    private List<uploadCARDS> list1 , list2;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference,dbRef;
    private CardsAdapter adapter;
    ProgressBar ProgressBarloader;

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    float v = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);
        CreditNoData = findViewById(R.id.CreditNoData);
        DebitNoData = findViewById(R.id.DebitNoData);
        DebitCardPart = findViewById(R.id.DebitCardPart);
        CreditCardPart = findViewById(R.id.CreditCardPart);
        ProgressBarloader = findViewById(R.id.cardViewLoader);

        Intent intent = getIntent();
        String type = intent.getStringExtra("cardType");




        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        tabLayout.addTab(tabLayout.newTab().setText("Debit Card"));
        tabLayout.addTab(tabLayout.newTab().setText("Credit Card"));

        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        PagerAdapter adapter = new CardViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (Objects.equals(type, "1")){
            viewPager.setCurrentItem(1);
        }else{
            viewPager.setCurrentItem(0);
        }



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }
        reference = FirebaseDatabase.getInstance().getReference().child("World").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        creditCardDepertment();
        debitCardDepertment();



    }

    private void debitCardDepertment() {
        ProgressBarloader.setVisibility(View.VISIBLE);
        dbRef= reference.child("Debit Card");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                list1 = new ArrayList<>();
                if(!datasnapshot.exists()){
                    DebitNoData.setVisibility(View.VISIBLE);
                    DebitCardPart.setVisibility(View.GONE);
                    ProgressBarloader.setVisibility(View.GONE);
                }else {
                    DebitNoData.setVisibility(View.GONE);
                    DebitCardPart.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        uploadCARDS data = snapshot.getValue(uploadCARDS.class);
                        list1.add(data);
                    }
                    ProgressBarloader.setVisibility(View.GONE);

                    DebitCardPart.setHasFixedSize(true);
                    DebitCardPart.setLayoutManager(new LinearLayoutManager(ViewCardActivity.this));
                    //adapter = new CardsAdapter(list1, ViewCardActivity.this);
                    DebitCardPart.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewCardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                ProgressBarloader.setVisibility(View.GONE);
            }
        });
    }

    private void creditCardDepertment() {
        ProgressBarloader.setVisibility(View.VISIBLE);
        dbRef= reference.child("Credit Card");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                list2 = new ArrayList<>();
                if(!datasnapshot.exists()){
                    CreditNoData.setVisibility(View.VISIBLE);
                    CreditCardPart.setVisibility(View.GONE);
                    ProgressBarloader.setVisibility(View.GONE);
                }else {
                    CreditNoData.setVisibility(View.GONE);
                    CreditCardPart.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        uploadCARDS data = snapshot.getValue(uploadCARDS.class);
                        list2.add(data);
                    }
                    ProgressBarloader.setVisibility(View.GONE);
                    CreditCardPart.setHasFixedSize(true);
                    CreditCardPart.setLayoutManager(new LinearLayoutManager(ViewCardActivity.this));
                   // adapter = new CardsAdapter(list2, ViewCardActivity.this);
                    CreditCardPart.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewCardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                ProgressBarloader.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Animatoo.animateSlideRight(this);    }
}