package com.shubhankarsudip.a1tapdocs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class ViewPasswordsActivity extends AppCompatActivity {

    private RecyclerView PasswordViewPart;
    private LinearLayout passwordNoData;
    private List<uploadPASSWORDS> list;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference,dbRef;
    private PasswordViewAdapter adapter;
    ProgressBar ProgressBarloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_passwords);

        passwordNoData = findViewById(R.id.passwordNoData);
        PasswordViewPart = findViewById(R.id.PasswordViewPart);
        ProgressBarloader = findViewById(R.id.PasswordViewLoader);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }
        reference = FirebaseDatabase.getInstance().getReference().child("Passwords");

        passwordViewpart();

    }
    private void passwordViewpart() {
        ProgressBarloader.setVisibility(View.VISIBLE);
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                list = new ArrayList<>();
                if(!datasnapshot.exists()){
                    passwordNoData.setVisibility(View.VISIBLE);
                    PasswordViewPart.setVisibility(View.GONE);
                    ProgressBarloader.setVisibility(View.GONE);
                }else {
                    passwordNoData.setVisibility(View.GONE);
                    PasswordViewPart.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        uploadPASSWORDS data = snapshot.getValue(uploadPASSWORDS.class);
                        list.add(data);
                    }
                    ProgressBarloader.setVisibility(View.GONE);

                    PasswordViewPart.setHasFixedSize(true);
                    PasswordViewPart.setLayoutManager(new LinearLayoutManager(ViewPasswordsActivity.this));
                    adapter = new PasswordViewAdapter(list, ViewPasswordsActivity.this);
                    PasswordViewPart.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPasswordsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                ProgressBarloader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Bungee.slideRight(this);
    }


}