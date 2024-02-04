package com.example.any1.feature_signup.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.any1.R


class Setup : AppCompatActivity(){
    private var activityReferences = 0
    private val isActivityChangingConfigurations = false
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences2 = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences2.getString("theme","")=="dark"){
            delegate.localNightMode = MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup)
        /* 3 fragments
        * 1st fragment is for setting up username and password
        * 2nd fragment will ask for setting display name and photo
        * 3rd fragment will be for uploading memes that describe you*/
        window.statusBarColor = ContextCompat.getColor(this, R.color.app_background)
        /*In case of sign up with email, the user will have to view 4 fragments. One for verification of code sent to email.*/

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
//        val username = sharedPreferences?.getString("username", "")
//        if (username != null) {
//            val firestore = FirebaseFirestore.getInstance()
//            val auth = FirebaseAuth.getInstance()
//            firestore.collection("usernames").document(username).get()
//                .addOnSuccessListener {
//                    firestore.collection("usernames").document(username).delete()
//                        .addOnSuccessListener {
//                            Log.d("appclosed", "username deleted succesfully")
//                        }.addOnFailureListener {
//                        Log.d("appclosed", "username deletion unsucessful")
//                    }
//                    val user = auth.currentUser
//                    if (user != null) {
//                        user.delete()
//                    }
//                }
//        }
//    }

    fun getActiveFragment(): Fragment? {
        if (supportFragmentManager.backStackEntryCount == 0) {
            return null
        }
        val tag =
            supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
        return supportFragmentManager.findFragmentByTag(tag)
    }

//    override fun onBackPressed() {
////        super.onBackPressed()
//        if(getActiveFragment() == email()|| getActiveFragment() == Emailverification()|| getActiveFragment() == setname()|| getActiveFragment() == displaynamepic()){
//            val dialog = Dialog(this)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCancelable(false)
//            dialog.setContentView(R.layout.profiledialog)
//            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            val stopcreation = dialog.findViewById<TextView>(R.id.stopcreating)
//            val continuee = dialog.findViewById<TextView>(R.id.continuee)
//            continuee.setOnClickListener { dialog.dismiss() }
//            stopcreation.setOnClickListener {
//                dialog.dismiss()
//                val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
//                val username = sharedPreferences?.getString("username","")
//                if (username != null) {
//                    val firestore = FirebaseFirestore.getInstance()
//                    firestore.collection("usernames").document(username).get().addOnSuccessListener{
//                        firestore.collection("usernames").document(username).delete()
//                    }
//                }
//                finish()
//            }
//            dialog.show()
//        }

//    }
}

