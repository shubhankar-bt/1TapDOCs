package com.shubhankarsudip.a1tapdocs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.nav_header_drawer.*

import android.view.ViewGroup

import android.os.Build
import android.widget.*
import androidx.core.app.ActivityCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.rejowan.cutetoast.CuteToast
import com.waspar.gradientbutton.GradientButton
import kotlinx.android.synthetic.main.activity_drawer.drawer_layout
import kotlinx.android.synthetic.main.custom_dialog_profile.*
import kotlinx.android.synthetic.main.nav_activity_main.*
import spencerstudios.com.bungeelib.Bungee
import java.net.URI.create
import java.util.ArrayList
import com.google.android.gms.tasks.Task

import androidx.annotation.NonNull
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.android.gms.tasks.OnCompleteListener






class DashboardActivity : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth
    private var dialog: Dialog? = null
    var openDialog = true
    private var mPrefs: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private lateinit var googleSignInClient: GoogleSignInClient



    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken( "1057641205795-vu94s74o3pqa44e8lb4nuat4t2u7hat2.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)




        val imageSlider = findViewById<ImageSlider>(R.id.topViewSlider)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.anytime))
        imageList.add(SlideModel(R.drawable.upload))
        imageList.add(SlideModel(R.drawable.redpicture_storage2))
        imageList.add(SlideModel(R.drawable.trialdesign))
        imageSlider.setImageList(imageList,ScaleTypes.FIT)



        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_signOut) {
                Toast.makeText(this@DashboardActivity, "Signed out, sign in again!!", Toast.LENGTH_SHORT).show()
                mAuth.signOut()
                revokeAccess()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                Bungee.inAndOut(this)
                finishAffinity()
            }
            if (item.itemId == R.id.nav_settings) {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                Bungee.slideLeft(this);
            }

            if (item.itemId == R.id.nav_Help_centre) {

            }
            if (item.itemId == R.id.nav_about_us) {
                val intent = Intent(this, AboutUsActivity::class.java)
                startActivity(intent)
                Bungee.slideLeft(this);
            }
            if (item.itemId == R.id.nav_User_Account) {
                val intent = Intent(this, UserAccountInfo::class.java)
                startActivity(intent)
                Bungee.slideLeft(this);
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }







      // profileImage.setOnClickListener {
      // }

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser


        Glide.with(this).load(currentUser?.photoUrl).into(profileImage)

    }

    private fun revokeAccess() {
        googleSignInClient.revokeAccess()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                // ...
            })
    }


    fun openCloseNavigationDrawerClick(view: android.view.View) {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    fun documentClick(view: android.view.View) {
        val intent = Intent(this, DocumentActivity2::class.java)
        startActivity(intent)
        Bungee.slideLeft(this)
    }

    fun cardsClick(view: android.view.View) {
        val intent = Intent(this, CardUploadActivity::class.java)
        startActivity(intent)
        Bungee.slideLeft(this)
    }

    fun passWordsClick(view: android.view.View) {

        val intent = Intent(this, PasswordUploadActivity::class.java)
        startActivity(intent)
        Bungee.slideLeft(this);
    }

   /* private fun showPopup(V: View) {
          val popup = PopupMenu(this, V)
          popup.inflate(R.menu.header_menu_try)

          popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

              when (item!!.itemId) {
                  R.id.header1 -> {

                  }
                  R.id.header2 -> {

                  }

              }

              true
          })

          popup.show()
      }  */





}

