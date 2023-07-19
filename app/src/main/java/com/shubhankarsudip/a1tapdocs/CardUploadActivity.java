package com.shubhankarsudip.a1tapdocs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidwidgets.formatedittext.widgets.FormatEditText;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CardUploadActivity extends AppCompatActivity {
    EditText editCardUserName;
    FormatEditText cardNumber;
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

        cardNumber.setFormat("---- ---- ---- ----");
        ExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = s.toString();
                if (current.length() == 2 && start == 1) {
                    ExpiryDate.setText(current + "/");
                    ExpiryDate.setSelection(current.length() + 1);
                }
                else if (current.length() == 2 && before == 1) {
                    current = current.substring(0, 1);
                    ExpiryDate.setText(current);
                    ExpiryDate.setSelection(current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        else{
            checkDataFormat();
        }

    }

    private void checkDataFormat() {
        if (!CardSaverNameis(CardSaverName)){
            Toast.makeText(this, "Please enter the valid name as per your card", Toast.LENGTH_SHORT).show();
            editCardUserName.requestFocus();
        }else if (!(CardNumber.length() == 16) && !(CardNumber.length() == 17) && !(CardNumber.length() == 18) && !(CardNumber.length() == 19)){
            Toast.makeText(this, "Please enter the valid Card number", Toast.LENGTH_SHORT).show();
        } else if (!BankName.equals("Bank of Baroda") && !BankName.equals("Bank of India") && !BankName.equals("Bank of Maharashtra") && !BankName.equals("Bank of India") && !BankName.equals("Canara Bank")
                && !BankName.equals("Central Bank of India") && !BankName.equals("Indian Bank") && !BankName.equals("Indian Overseas Bank")
                && !BankName.equals("Punjab & Sind Bank") && !BankName.equals("Punjab National Bank") && !BankName.equals("State Bank of India")
                && !BankName.equals("UCO Bank") && !BankName.equals("Union Bank of India") && !BankName.equals("Axis Bank Ltd.")
                && !BankName.equals("Axis Bank") && !BankName.equals("Bandhan Bank Ltd.") && !BankName.equals("Bandhan Bank")
                && !BankName.equals("CSB Bank Ltd.") && !BankName.equals("City Union Bank Ltd.") && !BankName.equals("DCB Bank Ltd.")
                && !BankName.equals("Dhanlaxmi Bank Ltd.") && !BankName.equals("Federal Bank Ltd.") && !BankName.equals("HDFC Bank Ltd")
                && !BankName.equals("HDFC Bank") && !BankName.equals("ICICI Bank Ltd.") && !BankName.equals("ICICI Bank")
                && !BankName.equals("Induslnd Bank Ltd") && !BankName.equals("IDFC First Bank Ltd.") && !BankName.equals("Jammu & Kashmir Bank Ltd.")
                && !BankName.equals("Karnataka Bank Ltd.") && !BankName.equals("Karur Vysya Bank Ltd.") && !BankName.equals("Kotak Mahindra Bank Ltd")
                && !BankName.equals("Lakshmi Vilas Bank Ltd.") && !BankName.equals("Kotak Mahindra Bank") && !BankName.equals("Nainital Bank Ltd.")
                && !BankName.equals("IDBI Bank Ltd.") && !BankName.equals("RBL Bank Ltd.") && !BankName.equals("South Indian Bank Ltd.") && !BankName.equals("India Post Payments Bank Limited")
                && !BankName.equals("Fino Payments Bank Limited") && !BankName.equals("Paytm Payments Bank Limited") && !BankName.equals("Airtel Payments Bank Limited")
                && !BankName.equals("Punjab Gramin Bank") && !BankName.equals("Maharashtra Gramin Bank") && !BankName.equals("Kerala Gramin Bank")
                && !BankName.equals("Karnataka Vikas Grameena Bank") && !BankName.equals("United Overseas Bank Ltd") && !BankName.equals("Standard Chartered Bank")
                && !BankName.equals("SBM Bank (India) Limited") && !BankName.equals("Qatar National Bank") && !BankName.equals("First Abu Dhabi Bank PJSC")
                && !BankName.equals("J.P. Morgan Chase Bank N.A.") && !BankName.equals("J.P. Morgan Chase Bank") && !BankName.equals("American Express Banking Corporation")
                && !BankName.equals("Bank of China") && !BankName.equals("Bank of America") && !BankName.equals("Australia and New Zealand Banking Group Ltd.")
                && !BankName.equals("Abu Dhabi Commercial Bank Ltd.") && !BankName.equals("AB Bank Ltd.") && !BankName.equals("state bank of india")
                && !BankName.equals("sbi") && !BankName.equals("SBI") && !BankName.equals("Sbi")
                && !BankName.equals("CBI") && !BankName.equals("cbi") && !BankName.equals("Cbi")
                && !BankName.equals("Canara") && !BankName.equals("Pnb") && !BankName.equals("pnb") && !BankName.equals("PNB") && !BankName.equals("IB")
                && !BankName.equals("Bandhan") && !BankName.equals("Axis") && !BankName.equals("Union") && !BankName.equals("UNION")
                && !BankName.equals("BOI") && !BankName.equals("Boi") && !BankName.equals("BOB") && !BankName.equals("CB")
                && !BankName.equals("UCO") && !BankName.equals("Uco") && !BankName.equals("UBI") && !BankName.equals("Ubi")
                && !BankName.equals("HDFC") && !BankName.equals("ICICI") && !BankName.equals("Kotak Mahindra") && !BankName.equals("YES Bank")
                && !BankName.equals("PSB") && !BankName.equals("Bank of Bahrain & Kuwait BSC") && !BankName.equals("Bank of Ceylon") && !BankName.equals("DBS Bank India Limited")
                && !BankName.equals("Cooperatieve Rabobank U.A.") && !BankName.equals("Societe Generale") && !BankName.equals("Shinhan Bank") && !BankName.equals("Sonali Bank Ltd.")
                && !BankName.equals("State bank of india") && !BankName.equals("Central bank of india") && !BankName.equals("Bank of baroda") && !BankName.equals("Bank of india") && !BankName.equals("Bank of maharashtra")
                && !BankName.equals("BOM") && !BankName.equals("Bom") && !BankName.equals("Canara bank") && !BankName.equals("IOB")
                && !BankName.equals("Iob") && !BankName.equals("Punjab national bank") && !BankName.equals("Uco bank") && !BankName.equals("Union bank of india")) {
            Toast.makeText(this, "Please enter the valid Bank Name as per your card", Toast.LENGTH_LONG).show();
            bankName.requestFocus();
        }
        else if (bitmap == null){
            pd.setMessage("Adding");
            pd.show();
            insertData();
        }
    }

    private boolean CardSaverNameis(String cardSaverName) {
            String regx = "^[A-Z][a-zA-Z]{2,}(?: [A-Z][a-zA-Z]*){0,2}$";
            Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(cardSaverName);
            return matcher.find();

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
            Animatoo.animateSlideRight(this);

        }


    public void openViewCardActivity(View view) {
        startActivity(new Intent(this, ViewCardActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }


}