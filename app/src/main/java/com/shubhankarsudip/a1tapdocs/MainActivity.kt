package com.shubhankarsudip.a1tapdocs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        Handler().postDelayed({
            if(user != null){
                val dashboardIntent = Intent(this, DashboardActivity::class.java)
                startActivity(dashboardIntent)
                Animatoo.animateSwipeRight(this)
                finish()
            }
            else{
                val signInIntent = Intent(this, SignInActivity::class.java)
                startActivity(signInIntent)
                Animatoo.animateSwipeRight(this)
                finish()
            }
        }, 1000)
    }
}