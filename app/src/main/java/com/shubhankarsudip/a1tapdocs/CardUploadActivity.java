package com.shubhankarsudip.a1tapdocs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shubhankarsudip.a1tapdocs.ui.View_PDF_Files;
import com.shubhankarsudip.a1tapdocs.ui.uploadPDF;

import java.util.Objects;

import spencerstudios.com.bungeelib.Bungee;

public class CardUploadActivity extends AppCompatActivity {
    EditText editCardUserName;
    EditText cardNumber;
    EditText bankName;
    EditText ExpiryDate;
    EditText CVV;
    RelativeLayout expandableLayout;
    Button cardsAddedButton;
    StorageReference storageReference;
    private Spinner addCardCategory;
    private ProgressDialog pd;
    String category;
    private DatabaseReference reference, dbRef;
    private Bitmap bitmap = null;
    private String CardSaverName,CardNumber,BankName,ExpDate,CVVno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_upload);

        editCardUserName = findViewById(R.id.cardSaverName);
        cardNumber = findViewById(R.id.cardNumber);
        bankName = findViewById(R.id.cardBankName);
        ExpiryDate = findViewById(R.id.CardExpiryDate);
        CVV = findViewById(R.id.CardCvvNumber);
        addCardCategory = findViewById((R.id.categoryCard));

        String[] items = new String[]{"Select Category", "Credit Card", "Debit Card"};
        addCardCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));

        addCardCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = addCardCategory.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        expandableLayout = findViewById(R.id.CardsDetailsSection);
        cardsAddedButton = findViewById(R.id.everyCardAddButton);
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

        reference = FirebaseDatabase.getInstance().getReference("World").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference();


        cardsAddedButton.setOnClickListener(new View.OnClickListener() {
           // @Override
           public void onClick(View v) {
               checkValidation();

            }

        });


        }

    private void checkValidation() {
        CardSaverName = editCardUserName.getText().toString();
       CardNumber =  cardNumber.getText().toString();
        BankName = bankName.getText().toString();
       ExpDate = ExpiryDate.getText().toString();
       CVVno = CVV.getText().toString();

        if (CardSaverName.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();

            editCardUserName.requestFocus();
        }else  if (CardNumber.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();
            cardNumber.requestFocus();
        }else  if (BankName.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();
            bankName.requestFocus();
        }else  if (ExpDate.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();
            ExpiryDate.requestFocus();
        }else  if (CVVno.isEmpty()){
            Toast.makeText(this, "Please Provide All The Details", Toast.LENGTH_SHORT).show();
            CVV.requestFocus();
        }else if (category.equals("Select Category")) {
            Toast.makeText(this, "Please Provide Card Category", Toast.LENGTH_SHORT).show();
        }
        else if (bitmap == null){
            pd.setMessage("Adding...");
            pd.show();
            insertData();
        }

    }


    private void insertData() {

        dbRef = reference.child(category);
        final String uniqueKey = dbRef.push().getKey();


        uploadCARDS uploadCARDS = new uploadCARDS(CardSaverName,CardNumber,BankName,ExpDate,CVVno,uniqueKey);

        dbRef.child(uniqueKey).setValue(uploadCARDS).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(CardUploadActivity.this, "Card Added", Toast.LENGTH_SHORT).show();
                editCardUserName.setText(null);
                cardNumber.setText(null);
                bankName.setText(null);
                ExpiryDate.setText(null);
                CVV.setText(null);
                addCardCategory.setSelection(0);

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception ) {
                        pd.dismiss();
                      Toast.makeText(CardUploadActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void goToHome (View view){
            startActivity(new Intent(this, DashboardActivity.class));
            Bungee.inAndOut(this);

        }


    public void openViewCardActivity(View view) {
        startActivity(new Intent(this, ViewCardActivity.class));
        Bungee.slideLeft(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Bungee.slideRight(this); //fire the slide left animation
    }


}