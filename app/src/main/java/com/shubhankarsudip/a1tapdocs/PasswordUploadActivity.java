package com.shubhankarsudip.a1tapdocs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class PasswordUploadActivity extends AppCompatActivity {

    EditText editWebsiteName;
    EditText editWebsiteUserName;
    EditText getEditWebsiteUserPassword;
    Button PasswordAddedButton;
    StorageReference storageReference;
    private ProgressDialog pd;
    private DatabaseReference reference, dbRef;
    private Bitmap bitmap = null;
    private String WebsiteName,WebsiteUser,WebsitePassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_upload);

        editWebsiteName = findViewById(R.id.WebsiteName);
        editWebsiteUserName = findViewById(R.id.password_user_name);
        getEditWebsiteUserPassword = findViewById(R.id.user_password);
        PasswordAddedButton = findViewById(R.id.everyPasswordAddButton);
        pd = new ProgressDialog(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
        }

        reference = FirebaseDatabase.getInstance().getReference("Passwords");
        storageReference = FirebaseStorage.getInstance().getReference();




        PasswordAddedButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                checkValidation();

            }

        });
    }

    private void checkValidation() {

        WebsiteName = editWebsiteName.getText().toString();
        WebsiteUser =  editWebsiteUserName.getText().toString();
        WebsitePassword = getEditWebsiteUserPassword.getText().toString();

        if (WebsiteName.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();
            editWebsiteName.requestFocus();
        }else  if (!WebsiteName.endsWith(".in") && !WebsiteName.endsWith(".com")){
            Toast.makeText(this, "Provide Valid Website Name!!  example- google.com / amazon.in ", Toast.LENGTH_LONG).show();
            editWebsiteName.requestFocus();
        }else  if (WebsiteUser.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();
            editWebsiteUserName.requestFocus();
        }else  if (WebsitePassword.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();
            getEditWebsiteUserPassword.requestFocus();
        }
        else if (bitmap == null){
            pd.setMessage("Adding...");
            pd.show();
            insertData();
        }



    }


    private void insertData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
        }

        dbRef = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final String uniqueKey = dbRef.push().getKey();


        uploadPASSWORDS uploadPASSWORDS = new uploadPASSWORDS(WebsiteName,WebsiteUser,WebsitePassword,uniqueKey);

        dbRef.child(uniqueKey).setValue(uploadPASSWORDS).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(PasswordUploadActivity.this, "Password added", Toast.LENGTH_SHORT).show();
                editWebsiteName.setText(null);
                editWebsiteUserName.setText(null);
                getEditWebsiteUserPassword.setText(null);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception ) {
                pd.dismiss();
                Toast.makeText(PasswordUploadActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }





    public void goToHome (View view){
        startActivity(new Intent(this, DashboardActivity.class));
        Animatoo.animateSlideRight(this);

    }
    public void OpenPasswordViewActivity (View view){
        startActivity(new Intent(this, ViewPasswordsActivity.class));
        Animatoo.animateSlideLeft(this);

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

}