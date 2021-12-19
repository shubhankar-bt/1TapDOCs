package com.shubhankarsudip.a1tapdocs.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.shubhankarsudip.a1tapdocs.DashboardActivity;
import com.shubhankarsudip.a1tapdocs.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class View_PDF_Files extends AppCompatActivity {

    ListView myPDfListView;
    DatabaseReference databaseReference;
    List<uploadPDF> uploadPDFS;
    String[] uploads;
    ProgressBar ProgressBarloader;
    LottieAnimationView noDataAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf_files);

        myPDfListView = (ListView) findViewById(R.id.myListView);
        ProgressBarloader = (ProgressBar)findViewById(R.id.pdfViewLoader);
        noDataAnimation = findViewById(R.id.noDataAnimation);

        uploadPDFS = new ArrayList<>();




        viewAllFiles();

        myPDfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                uploadPDF uploadPDF = uploadPDFS.get(position);
                Uri uri = Uri.parse(uploadPDF.getUrl());

                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(uploadPDF.getUrl()), "application/*");
                startActivity(intent);

            }
        });



    }

    private void viewAllFiles() {
        ProgressBarloader.setVisibility(View.VISIBLE);
        noDataAnimation.setVisibility(View.VISIBLE);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

        DatabaseReference mDatabase;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("uploads").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

       // databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    ProgressBarloader.setVisibility(View.GONE);

                    noDataAnimation.setVisibility(View.VISIBLE);
                }else {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        uploadPDF uploadPDF = postSnapshot.getValue(com.shubhankarsudip.a1tapdocs.ui.uploadPDF.class);
                        uploadPDFS.add(uploadPDF);
                    }
                    uploads = new String[uploadPDFS.size()];

                    for (int i = 0; i < uploads.length; i++) {
                        uploads[i] = uploadPDFS.get(i).getName();
                    }

                    customAdapter customAdapter = new customAdapter();
                    noDataAnimation.setVisibility(View.GONE);

                    myPDfListView.setAdapter(customAdapter);
                    ProgressBarloader.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ProgressBarloader.setVisibility(View.GONE);


            }
        });
    }
    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return uploads.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           @SuppressLint("ViewHolder") View myView = getLayoutInflater().inflate(R.layout.list_pdf_item, null);
           TextView textView = myView.findViewById(R.id.pdfNameText);
           textView.setSelected(true);
           textView.setText(uploads[position]);

           return myView;
        }
    }


    public void goToHome (View view){
        startActivity(new Intent(this, DashboardActivity.class));
        Bungee.inAndOut(this);

    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Bungee.slideRight(this); //fire the slide left animation
    }
}