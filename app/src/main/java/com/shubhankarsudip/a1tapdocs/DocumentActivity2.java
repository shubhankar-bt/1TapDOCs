package com.shubhankarsudip.a1tapdocs;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.ProgressDialog;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.Continuation;
        import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shubhankarsudip.a1tapdocs.ui.View_PDF_Files;
import com.shubhankarsudip.a1tapdocs.ui.uploadPDF;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicMarkableReference;

import spencerstudios.com.bungeelib.Bungee;


public class DocumentActivity2 extends AppCompatActivity {


    ImageView upload;
    public String name;
    EditText editPdfName;
    StorageReference storageReference;
    DatabaseReference databaseReference,dbRef;
    FirebaseAuth firebaseAuth;
    private String title;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document2);
        upload = findViewById(R.id.uploadPdfBtn);
        editPdfName = (EditText)findViewById(R.id.txt_pdfName);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
        }


       storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editPdfName.getText().toString();
                if (title.isEmpty()){
                    editPdfName.setError("Empty");
                    editPdfName.requestFocus();

                }else {
                    selectPDFFile();
                }
            }
        });


   }



    private void selectPDFFile()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select pdf File"),1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
        && data !=null && data.getData()!=null){

            uploadPDFFile(data.getData());
        }
    }

    private void uploadPDFFile(Uri  data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();


        StorageReference reference = storageReference.child("uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri ) {
                                Uri url = uriTask.getResult();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    for (UserInfo profile : user.getProviderData()) {
                                        String providerId = profile.getProviderId();

                                        String userId = profile.getUid();

                                    }
                                }


                                uploadPDF uploadPDF = new uploadPDF(editPdfName.getText().toString(),url.toString(), user.getUid());

                                databaseReference.child(databaseReference.push().getKey()).setValue(uploadPDF);


                                Toast.makeText(DocumentActivity2.this, "File uploaded", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
                }).addOnProgressListener((snapshot -> {

                    double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded "+" "+(int) progress+"%");

                }));

    }

    public void openNewFile(View view){
        startActivity(new Intent(getApplicationContext(),View_PDF_Files.class));
        Bungee.slideLeft(this);
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

