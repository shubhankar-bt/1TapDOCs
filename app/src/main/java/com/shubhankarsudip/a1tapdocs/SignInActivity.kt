package com.shubhankarsudip.a1tapdocs

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rejowan.cutetoast.CuteToast
import com.waspar.gradientbutton.GradientButton
import kotlinx.android.synthetic.main.activity_sign_in.*
import spencerstudios.com.bungeelib.Bungee


class SignInActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 150
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var dialog: Dialog? = null
    private lateinit  var mPrefs: SharedPreferences
    private  lateinit  var mEditor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        mEditor = mPrefs.edit()



        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken( "1057641205795-vu94s74o3pqa44e8lb4nuat4t2u7hat2.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()


       signInBtn.setOnClickListener {

           val isUsedBefore = mPrefs.getBoolean("FirstUseDialog", false)

           if (isUsedBefore == true) {
               signIn()
           } else {
               dialog!!.show()
           }

        }

        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.custom_dialog_profile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog!!.getWindow()!!
                .setBackgroundDrawable(getDrawable(R.drawable.dialog_background))
        }
        dialog!!.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.setCancelable(false)

        dialog!!.getWindow()!!.attributes.windowAnimations =
            R.style.dialoganimation //Setting the animations to dialog

        dialog!!.findViewById<GradientButton>(R.id.dialogAcceptButton).setOnClickListener {
            mEditor.putBoolean( "FirstUseDialog", true)
            mEditor.apply()
            signIn()
            CuteToast.ct(this, "Just a moment", CuteToast.LENGTH_SHORT, CuteToast.HAPPY, true).show()
            dialog!!.dismiss()
            finishActivity(1)

        }
        dialog!!.findViewById<TextView>(R.id.readmore).setOnClickListener {
            val uri =
                Uri.parse("https://eastsitalkuchi.blogspot.com/2021/12/1tapdocs-privacy-policy.html")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            dialog!!.hide()
        }
        dialog!!.findViewById<TextView>(R.id.dialogDenyButton).setOnClickListener {
            Bungee.slideUp(this)
            this.finishAffinity()
            System.exit(0)


        }



    }

    private fun signIn() {
            val signInIntent = googleSignInClient.signInIntent
           startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)

                    firebaseAuthWithGoogle(account.idToken!!)

                }


                catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInActivity", "Google sign in failed", e)
                }
            }
            else{
                Log.w("SignInActivity", exception.toString())
            }



        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signInBtn.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    Bungee.slideLeft(this)
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                    signInBtn.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
    }




}