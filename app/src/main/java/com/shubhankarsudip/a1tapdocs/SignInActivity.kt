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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rejowan.cutetoast.CuteToast
import com.shubhankarsudip.a1tapdocs.Util.AppStatus
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.imaginativeworld.oopsnointernet.ConnectionCallback
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import java.util.*


class SignInActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 150
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var dialog: Dialog? = null
    private lateinit  var mPrefs: SharedPreferences
    private  lateinit  var mEditor: SharedPreferences.Editor
    private var noInternetDialog: NoInternetDialog? = null
    private lateinit var emailImage: ImageView


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


        emailImage = findViewById(R.id.emailImage)




        layout.setOnClickListener {

           val isUsedBefore = mPrefs.getBoolean("FirstUseDialog", false)

           if (isUsedBefore == true) {
               signIn()
           } else {
               showDialog()
           }

        }

//        emailImage.setOnClickListener(View.OnClickListener {
//            val isUsedBefore = mPrefs.getBoolean("FirstUseDialog", false)
//            if (isUsedBefore == true) {
//                EmailSignin()
//            } else {
//                showDialog()
//            }
//
//
//        })

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )





    }

//    fun EmailSignin() {
//        if (AppStatus.getInstance(this).isOnline) {
//
//            val providers = arrayListOf(
//                AuthUI.IdpConfig.EmailBuilder().build()
//            )
//
//
//            // Create and launch sign-in intent
//            val signInIntent = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .setTosAndPrivacyPolicyUrls(
//                    "https://eastsitalkuchi.blogspot.com/2021/12/1tapdocs-terms-and-conditions.html",
//                    "https://eastsitalkuchi.blogspot.com/2021/12/1tapdocs-privacy-policy.html")
//                .build()
//            signInLauncher.launch(signInIntent)
//        } else {
//            Toast.makeText(this@SignInActivity, "no internet", Toast.LENGTH_SHORT).show()
//        }
//    }


    override fun onResume() {
        super.onResume()

        // No Internet Dialog
        noInternetDialog = NoInternetDialog.Builder(this)
            .apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        // ...
                    }
                }
                cancelable = false // Optional
                noInternetConnectionTitle = "No Internet" // Optional
                noInternetConnectionMessage =
                    "Check your Internet connection and try again." // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Please turn on" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
            .build()
    }


    override fun onPause() {
        super.onPause()

        // No Internet Dialog
        noInternetDialog?.destroy()

    }

    private fun signIn() {
        if (AppStatus.getInstance(this).isOnline()) {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

//            val providers = arrayListOf(
//                AuthUI.IdpConfig.GoogleBuilder().build()
//            )
//
//
//            // Create and launch sign-in intent
//            val signInIntent = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .setTosAndPrivacyPolicyUrls(
//                    "https://eastsitalkuchi.blogspot.com/2021/12/1tapdocs-terms-and-conditions.html",
//                    "https://eastsitalkuchi.blogspot.com/2021/12/1tapdocs-privacy-policy.html")
//                .build()
//            signInLauncher.launch(signInIntent)


        } else {
            Toast.makeText(this@SignInActivity, "no internet", Toast.LENGTH_SHORT).show()
        }

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



//    private val signInLauncher = registerForActivityResult(
//        FirebaseAuthUIActivityResultContract()) { res ->
//        signInBtn.visibility = View.GONE
//        emailImage.visibility = View.GONE
//        progressBar.visibility = View.VISIBLE
//        this.onSignInResult(res)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        // callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)
//            Log.i("", "onActivityResult: ---$resultCode")
//            if (resultCode == RESULT_OK) {
//                // Successfully signed in
//                val user = FirebaseAuth.getInstance().currentUser
//                if (user != null){
//                    user!!.displayName
//                    user.email
//                    user.phoneNumber
//                    Log.i(
//                        "",
//                        "onActivityResult: ---already signed in" + "----" + user.displayName + "----" + user.email + "----" + user.phoneNumber
//                    )
//                }
//
//                // ...
//                // else {
//                    val mainIntent = Intent(this@SignInActivity, DashboardActivity::class.java)
//                    // ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( SplashScreen.this, pairs);
//                if (user != null) {
//                    mainIntent.putExtra("username", user.displayName)
//                }
//                    mainIntent.putExtra("fromLogin", true)
//                    startActivity(mainIntent)
//                    finish()
//            } else if (resultCode == RESULT_CANCELED) {
//                if (response != null) {
//                    //loadingLayout.setVisibility(View.GONE)
//                    Toast.makeText(
//                        this@SignInActivity,
//                        "Sign in failed please try again with different email Id " + response.error,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
//        val response = result.idpResponse
//        if (result.resultCode == RESULT_OK) {
//            // Successfully signed in
//            val user = FirebaseAuth.getInstance().currentUser
//            val intent = Intent(this, DashboardActivity::class.java)
//            intent.putExtra("username", user!!.displayName)
//            intent.putExtra("fromLogin", true)
//            startActivity(intent)
//            Animatoo.animateSwipeLeft(this)
//            finish()
//
//            // ...
//        } else {
//            // Sign in failed. If response is null the user canceled the
//            // sign-in flow using the back button. Otherwise check
//            // response.getError().getErrorCode() and handle the error.
//            // ...
//            Log.w("SignInActivity", "signInWithCredential:failure")
//            signInBtn.visibility = View.VISIBLE
//            emailImage.visibility = View.VISIBLE
//            progressBar.visibility = View.GONE
//        }
//    }



    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        //signInBtn.visibility = View.GONE
        emailImage.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        textSignWith.text = "Loading app..."
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    Animatoo.animateSwipeLeft(this)
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                    signInBtn.visibility = View.VISIBLE
                    emailImage.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun showDialog(){
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

        dialog!!.findViewById<Button>(R.id.dialogAcceptButton).setOnClickListener {
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

            this.finishAffinity()
            System.exit(0)


        }

        dialog!!.show()

    }




}