package com.shubhankarsudip.a1tapdocs

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.shubhankarsudip.a1tapdocs.Activity.NoteMainActivity
import com.shubhankarsudip.a1tapdocs.Adapters.PdfListAdapter2
import com.shubhankarsudip.a1tapdocs.ui.BottomSheetDialog
import com.shubhankarsudip.a1tapdocs.ui.View_PDF_Files
import com.shubhankarsudip.a1tapdocs.ui.uploadPDF
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_drawer.*
import org.imaginativeworld.oopsnointernet.ConnectionCallback
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import java.util.*


class DashboardActivity : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth
    private var mPrefs: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var noInternetDialog: NoInternetDialog? = null
    private var doubleBackToExitPressedOnce = false
    private lateinit var  message: String;
    var databaseReference: DatabaseReference? = null
    var dbRef:DatabaseReference? = null
    lateinit var uploadPDFS: List<uploadPDF>
    lateinit var uploads: Array<String>
    lateinit var mAdapter: PdfListAdapter2
    lateinit var cardAdapter: CardsAdapter
    lateinit var passwordAdapter: PasswordViewAdapter

    //lateinit var ProgressBarloader: ProgressBar
    lateinit var noDataText: TextView
    lateinit var thoughtCard:RelativeLayout
    private lateinit var thought: String
    private var fetched:Boolean = false


    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_activity_main)

       FirebaseMessaging.getInstance().subscribeToTopic("notification")

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)



       checkPermission()
       // requestPermission()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken( "1057641205795-vu94s74o3pqa44e8lb4nuat4t2u7hat2.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        val imageSlider = findViewById<ImageSlider>(R.id.topViewSlider)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.anytime_photo))
        imageList.add(SlideModel(R.drawable.access_in_one_tap))
        imageList.add(SlideModel(R.drawable.get_rid))
        imageList.add(SlideModel(R.drawable.accesspasswords))
        imageList.add(SlideModel(R.drawable.card_save))
        imageSlider.setImageList(imageList,ScaleTypes.FIT)


        viewAllFiles()
        debitCardDepertment()
        CreditCardDepertment()
        passwordDepartment()

        if (feelingText.visibility == View.GONE){
            sliderProgress.visibility = View.VISIBLE
        }
        retriveFromFirebase()


        noDataText = findViewById(R.id.noDataText)
        thoughtCard = findViewById(R.id.thoughtCard);



        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_signOut) {
                Toast.makeText(this@DashboardActivity, "Signed out, sign in again!!", Toast.LENGTH_SHORT).show()
                mAuth.signOut()
                revokeAccess()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideRight(this)
                finishAffinity()
            }
            if (item.itemId == R.id.nav_settings) {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideLeft(this)

            }

            if (item.itemId == R.id.nav_Help_centre) {
                val uri =
                    Uri.parse("https://eastsitalkuchi.blogspot.com/2021/12/help-desk-for-1tapdocs.html")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)

            }
            if (item.itemId == R.id.rateButton) {
                val uri =
                    Uri.parse("https://play.google.com/store/apps/details?id=com.shubhankarsudip.a1tapdocs")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)

            }
            if (item.itemId == R.id.nav_about_us) {
                val intent = Intent(this, AboutUsActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideLeft(this)

            }
            if (item.itemId == R.id.nav_User_Account) {
                val intent = Intent(this, UserAccountInfo::class.java)
                startActivity(intent)
                Animatoo.animateSlideLeft(this)
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }



        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val name = currentUser?.displayName
        var parts  = name?.split(" ")?.toMutableList()
        val firstName = parts?.firstOrNull()

        Glide.with(this).load(currentUser?.photoUrl).into(profileImage)

        message = getGreetingMessage()+"\n"+firstName
        greetingText.text = message

        thoughtCard.setOnClickListener { view ->
            if (fetched){
                showBottomThemeDialog(thought)
            }else{
                Snackbar.make(view, "Just a second",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show()

            }
        }


    }




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
        noInternetDialog?.destroy()

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
        Animatoo.animateSlideLeft(this)
    }

    fun cardsClick(view: android.view.View) {
        val intent = Intent(this, CardUploadActivity::class.java)
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }

    fun passWordsClick(view: android.view.View) {

        val intent = Intent(this, PasswordUploadActivity::class.java)
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)

    }

    fun NotesClick(view: View) {
        val intent = Intent(this, NoteMainActivity::class.java)
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }



    fun getGreetingMessage():String{
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            else -> "Hello"
        }
    }


    private fun viewAllFiles() {
        progressbar_1.visibility = View.VISIBLE
        showmorebutton.visibility = View.GONE

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
        }
        val mAuth = FirebaseAuth.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        var mDatabase: DatabaseReference
        val pdfArray = ArrayList<uploadPDF>()
        var uploads: Array<String>
        databaseReference = FirebaseDatabase.getInstance().reference.child("uploads").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        databaseReference!!.orderByKey().limitToLast(3).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    progressbar_1.visibility = View.GONE
                    noDataText.visibility = View.VISIBLE
                    recyclerHome.visibility = View.GONE
                    showmorebutton.visibility = View.GONE

                } else {
                    for (postSnapshot in snapshot.children) {
                        val uploadPDF2 = postSnapshot.getValue(uploadPDF::class.java)
                        uploadPDF2!!.setKey(postSnapshot.key)
                        Log.d("View_PDF_Files", "getkey id of user pdf:" + postSnapshot.key)
                       // pdfArray = ArrayList<uploadPDF>()
                        pdfArray.add(uploadPDF2)
                    }
                    recyclerHome.layoutManager= LinearLayoutManager(this@DashboardActivity)
                    mAdapter = PdfListAdapter2(pdfArray, this@DashboardActivity)
                    recyclerHome.adapter = mAdapter


                    progressbar_1.visibility = View.GONE
                    noDataText.visibility =View.GONE
                    showmorebutton.visibility = View.VISIBLE
                    mAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Dashboard---->", error.toString())
                progressbar_1.visibility = View.GONE
                noDataText.visibility =View.VISIBLE
                noDataText.setText("Failed to load Data\n Check your Internet Connection")
            }
        })
    }


    private fun retriveFromFirebase() {
        //get data from firebase realtime database
        val user = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().reference.child("thoughts")
        dbRef = reference.child(FirebaseAuth.getInstance().currentUser!!.uid)
        dbRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                //list = new ArrayList<>();
                if (!datasnapshot.exists()) {
                    emoji.visibility = View.GONE
                    feelingText.text =  "No Data"
                    feelingText.visibility = View.VISIBLE
                    sliderProgress.visibility = View.GONE
                    thought = "No thought added"
                    fetched = true
                }
                else{

                    thought = datasnapshot.child("thoughts").value as String
                    val Mood = datasnapshot.child("mood").value

                    feelingText.text = Mood as CharSequence?
                    sliderProgress.visibility = View.GONE
                    feelingText.visibility = View.VISIBLE
                    Log.e("mood","mood"+Mood )
                    fetched = true
                    emoji.visibility = View.VISIBLE


                    if (Mood != null) {
                        if (Mood.equals("Happy")) {
                            emoji.setImageResource(R.drawable.happy)
                        } else if (Mood.equals("Funny")) {
                            emoji.setImageResource(R.drawable.laughing)
                        } else if (Mood.equals("Sad")) {
                            emoji.setImageResource(R.drawable.crying)
                        } else if (Mood.equals("Love")) {
                            emoji.setImageResource(R.drawable.love)
                        } else if (Mood.equals("Confused")) {
                            emoji.setImageResource(R.drawable.thinking)
                        }
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                fetched = true

            }
        })
    }



    private fun debitCardDepertment() {
        progressbar_2.visibility = View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().reference.child("World").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )

        dbRef = databaseReference!!.child("Debit Card")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
        }
        dbRef!!.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                val list1 = ArrayList<uploadCARDS>()
                if (!datasnapshot.exists()) {
                    progressbar_2.visibility = View.GONE
                    noDataText2.visibility = View.VISIBLE
                    showmorebutton2.visibility = View.GONE
                    recyclerDebitCard.visibility = View.GONE

                } else {
                    for (snapshot in datasnapshot.children) {
                        val data = snapshot.getValue(uploadCARDS::class.java)
                        if (data != null) {
                            list1.add(data)
                        }
                    }
                    progressbar_2.visibility = View.GONE
                    recyclerDebitCard.visibility = View.VISIBLE
                    recyclerDebitCard.layoutManager= LinearLayoutManager(this@DashboardActivity)
                    cardAdapter = CardsAdapter(list1, this@DashboardActivity, 0)
                    recyclerDebitCard.adapter = cardAdapter
                    cardAdapter.notifyDataSetChanged()

                    progressbar_2.visibility = View.GONE
                    noDataText2.visibility =View.GONE
                    showmorebutton2.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                progressbar_2.setVisibility(View.GONE)
            }
        })
    }

    private fun CreditCardDepertment() {
        progressbar_3.visibility = View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().reference.child("World").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )

        dbRef = databaseReference!!.child("Credit Card")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
        }
        dbRef!!.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                val list2 = ArrayList<uploadCARDS>()
                if (!datasnapshot.exists()) {
                    progressbar_3.visibility = View.GONE
                    noDataText3.visibility = View.VISIBLE
                    showmorebutton3.visibility = View.GONE
                    recyclerCrditCard.visibility = View.GONE

                } else {
                    for (snapshot in datasnapshot.children) {
                        val data = snapshot.getValue(uploadCARDS::class.java)
                        if (data != null) {
                            list2.add(data)
                        }
                    }
                    progressbar_3.visibility = View.GONE
                    recyclerCrditCard.visibility = View.VISIBLE
                    recyclerCrditCard.layoutManager= LinearLayoutManager(this@DashboardActivity)
                    cardAdapter = CardsAdapter(list2, this@DashboardActivity, 1)
                    recyclerCrditCard.adapter = cardAdapter
                    cardAdapter.notifyDataSetChanged()
                    noDataText3.visibility =View.GONE
                    showmorebutton3.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                progressbar_3.setVisibility(View.GONE)
            }
        })
    }

    private fun passwordDepartment() {
        progressbar_4.visibility = View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().reference.child("Passwords")
        dbRef = databaseReference!!.child(FirebaseAuth.getInstance().currentUser!!.uid)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
        }
        dbRef!!.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                val list3 = ArrayList<uploadPASSWORDS>()
                if (!datasnapshot.exists()) {
                    progressbar_4.visibility = View.GONE
                    noDataText4.visibility = View.VISIBLE
                    showmorebutton4.visibility = View.GONE
                    recyclerPassword.visibility = View.GONE

                } else {
                    for (snapshot in datasnapshot.children) {
                        val data = snapshot.getValue(uploadPASSWORDS::class.java)
                        if (data != null) {
                            list3.add(data)
                        }
                    }
                    progressbar_4.visibility = View.GONE
                    recyclerPassword.visibility = View.VISIBLE
                    recyclerPassword.layoutManager= LinearLayoutManager(this@DashboardActivity)
                    passwordAdapter = PasswordViewAdapter(list3, this@DashboardActivity)
                    recyclerPassword.adapter = passwordAdapter
                    passwordAdapter.notifyDataSetChanged()
                    noDataText4.visibility =View.GONE
                    showmorebutton4.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                progressbar_4.setVisibility(View.GONE)
            }
        })
    }


    fun gotoViewPdf(view: View) {
        val intent = Intent(this, View_PDF_Files::class.java)
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }

    fun showBottomThemeDialog(thought : String) {
        val bottomSheet = BottomSheetDialog(thought)
        bottomSheet.setCancelable(true)
        bottomSheet.show(this.getSupportFragmentManager(), "ModalBottomSheet")
    }

    fun gotoCard(view: View) {
        val intent = Intent(this, ViewCardActivity::class.java)
        intent.putExtra("cardType", "0");
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }

    fun gotoCard2(view: View) {
        val intent = Intent(this, ViewCardActivity::class.java)
        intent.putExtra("cardType", "1");
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this@DashboardActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this@DashboardActivity, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this@DashboardActivity,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    fun goToPassword(view: View) {
        val intent = Intent(this, ViewPasswordsActivity::class.java)
        intent.putExtra("cardType", "1");
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }


}

