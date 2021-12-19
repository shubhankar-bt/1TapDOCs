package com.shubhankarsudip.a1tapdocs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class ViewCardActivity extends AppCompatActivity {

    private RecyclerView DebitCardPart,CreditCardPart;
    private LinearLayout DebitNoData, CreditNoData;
    private List<uploadCARDS> list1 , list2;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference,dbRef;
    private CardsAdapter adapter;
    ProgressBar ProgressBarloader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);
        CreditNoData = findViewById(R.id.CreditNoData);
        DebitNoData = findViewById(R.id.DebitNoData);
        DebitCardPart = findViewById(R.id.DebitCardPart);
        CreditCardPart = findViewById(R.id.CreditCardPart);
        ProgressBarloader = findViewById(R.id.cardViewLoader);


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
                    adapter = new CardsAdapter(list1, ViewCardActivity.this);
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
                    adapter = new CardsAdapter(list2, ViewCardActivity.this);
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
        Bungee.slideRight(this); //fire the slide left animation
    }
}