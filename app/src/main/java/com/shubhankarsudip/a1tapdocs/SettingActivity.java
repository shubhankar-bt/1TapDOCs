package com.shubhankarsudip.a1tapdocs;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.ButtonBarLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.auth.api.credentials.IdToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.rejowan.cutetoast.CuteToast;

import spencerstudios.com.bungeelib.Bungee;

public class SettingActivity extends AppCompatActivity {

    ToggleButton toggleButton3, toggleButton4;
    TextView darkModeSwitch;
    private SeekBar seekBar;
    private SharedPreferences prefs;
    private EditText edittext;
    TextView view;
    FirebaseAuth firebaseAuth;
    private Object IdToken;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    GoogleSignIn googleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        toggleButton3 = findViewById(R.id.toggleButton3);
        toggleButton4 = findViewById(R.id.toggleButton4);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        view = findViewById(R.id.txtPercentage);
        LinearLayout app_layer = (LinearLayout) findViewById(R.id.privacySwitch);
        TextView TermsButton = (TextView) findViewById(R.id.TermsButton);
        pd = new ProgressDialog(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1057641205795-vu94s74o3pqa44e8lb4nuat4t2u7hat2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);


        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkModeSwitch.setText("Disable Dark Mode");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            darkModeSwitch.setText("Enable Dark Mode");
        }

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            toggleButton3.setChecked(true);

        toggleButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                    darkModeSwitch.setText("Enable Dark Mode");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                    darkModeSwitch.setText("Disable Dark Mode");
                }
            }
        });

        toggleButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(SettingActivity.this, "Notification Allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingActivity.this, "Notification Disabled", Toast.LENGTH_SHORT).show();

                }
            }
        });


        app_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
                Bungee.slideLeft(getApplicationContext());
                finish();
            }
        });

        TermsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://eastsitalkuchi.blogspot.com/2021/12/1tapdocs-terms-and-conditions.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }


    public void goToHome(View view) {
        startActivity(new Intent(this, DashboardActivity.class));
        Bungee.inAndOut(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(this); //fire the slide left animation
    }

    public void deleteAccount(View view) {
        pd.setTitle("Deleting...");
        pd.setMessage("Just a moment");
        pd.show();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && user != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            user.reauthenticate(credential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "reAuthenticated");
                            pd.setMessage("Account deleted successfully");

                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User account deleted.");
                                        revokeAccess();
                                        Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
                                        pd.dismiss();
                                        startActivity(intent);
                                    }

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "Failed to delete");
                    pd.setMessage("Failed to delete account");
                    pd.dismiss();
                    Toast.makeText(SettingActivity.this, "Please sign out and try after signing in again", Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

}

