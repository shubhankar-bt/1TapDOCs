package com.shubhankarsudip.a1tapdocs.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shubhankarsudip.a1tapdocs.Adapters.PdfListAdapter;
import com.shubhankarsudip.a1tapdocs.Adapters.PdfListAdapter2;
import com.shubhankarsudip.a1tapdocs.DashboardActivity;
import com.shubhankarsudip.a1tapdocs.DocumentActivity2;
import com.shubhankarsudip.a1tapdocs.PasswordViewAdapter;
import com.shubhankarsudip.a1tapdocs.R;
import com.shubhankarsudip.a1tapdocs.ViewPasswordsActivity;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;


public class View_PDF_Files extends AppCompatActivity {

    ListView myPDfListView;
    RecyclerView recyclerPdf;
    DatabaseReference databaseReference,dbRef;
    List<uploadPDF> uploadPDFS;
    String[] uploads;
    ProgressBar ProgressBarloader;
    LottieAnimationView noDataAnimation;
    String Key;
    String placeId = "";
    private PdfListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf_files);

        myPDfListView = (ListView) findViewById(R.id.myListView);
        ProgressBarloader = (ProgressBar)findViewById(R.id.pdfViewLoader);
        noDataAnimation = findViewById(R.id.noDataAnimation);
        recyclerPdf = findViewById(R.id.recyclerPdf);

        uploadPDFS = new ArrayList<>();
        viewAllFiles();

//        myPDfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//               // uploadPDF uploadPDF = uploadPDFS.get(position);
//               // Uri uri = Uri.parse(uploadPDF.getUrl());
//
//                try {
//                    uploadPDF uploadPDF = uploadPDFS.get(position);
//                    Uri uri = Uri.parse(uploadPDF.getUrl());
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
//                        // Word document
//                        intent.setDataAndType(uri, "application/msword");
//                    } else if (uri.toString().contains(".pdf")) {
//                        // PDF file
//                        intent.setDataAndType(uri, "application/pdf");
//                    } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
//                        // Powerpoint file
//                        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//                    } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
//                        // Excel file
//                        intent.setDataAndType(uri, "application/vnd.ms-excel");
//                    } else {
//                        intent.setDataAndType(uri, "*/*");
//                    }
//
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    View_PDF_Files.this.startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(View_PDF_Files.this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });



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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    ProgressBarloader.setVisibility(View.GONE);
                    recyclerPdf.setVisibility(View.GONE);
                    noDataAnimation.setVisibility(View.VISIBLE);
                }else {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        uploadPDF uploadPDF2 = postSnapshot.getValue(uploadPDF.class);
                        uploadPDF2.setKey(postSnapshot.getKey());
                        Log.d("View_PDF_Files", "getkey id of user pdf:" + postSnapshot.getKey());
                        uploadPDFS.add(uploadPDF2);

                    }
                    uploads = new String[uploadPDFS.size()];

                    for (int i = 0; i < uploads.length; i++) {
                        uploads[i] = uploadPDFS.get(i).getName();
                    }

                    noDataAnimation.setVisibility(View.GONE);
                    ProgressBarloader.setVisibility(View.GONE);




                    recyclerPdf.setHasFixedSize(true);
                    recyclerPdf.setLayoutManager(new LinearLayoutManager(View_PDF_Files.this));
                    adapter = new PdfListAdapter(uploadPDFS, View_PDF_Files.this);
                    recyclerPdf.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


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
           uploadPDF data = uploadPDFS.get(position);
           String Key = data.getKey();
            return myView;

        }



    }



    public void goToHome (View view){
        startActivity(new Intent(this, DashboardActivity.class));
        Animatoo.animateSlideRight(this);

    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}