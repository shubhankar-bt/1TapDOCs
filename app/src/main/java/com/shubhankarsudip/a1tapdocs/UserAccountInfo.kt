package com.shubhankarsudip.a1tapdocs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_user_account_info.*

import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.android.gms.tasks.OnCompleteListener




class UserAccountInfo : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account_info)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken( "1057641205795-vu94s74o3pqa44e8lb4nuat4t2u7hat2.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)




        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        userProfileName.text = currentUser?.displayName
        userProfileEmail.text = currentUser?.email
        Glide.with(this).load(currentUser?.photoUrl).into(userProfileImage)

    }

    fun goToHome(view: View?) {
        startActivity(Intent(this, DashboardActivity::class.java))
        Animatoo.animateSlideRight(this)
    }

    fun Signout(view: android.view.View) {
        mAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        revokeAccess()
        Animatoo.animateSlideRight(this)
        finishAffinity()
        Toast.makeText(this@UserAccountInfo, "Signed out, sign in again!!", Toast.LENGTH_SHORT).show()


    }

    private fun revokeAccess() {
        googleSignInClient.revokeAccess()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                // ...
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(this)
    }

}