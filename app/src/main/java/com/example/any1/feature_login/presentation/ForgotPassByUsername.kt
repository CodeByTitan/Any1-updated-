package com.example.any1.feature_login.presentation

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doAfterTextChanged
import com.example.any1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPassByUsername : AppCompatActivity() {
    var email = ""
    private lateinit var dialog : Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences2 = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences2.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass_by_username)
        val username = findViewById<EditText>(R.id.forgotusername)
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        var usernamestr = ""
        val useemailinstead = findViewById<TextView>(R.id.textView17)
        username.doAfterTextChanged {
            text: Editable? ->  usernamestr = text.toString()
        }
        val submit = findViewById<Button>(R.id.submit)
        val backbutton = findViewById<ImageView>(R.id.bb)
        useemailinstead.setOnClickListener {
            val intent = Intent(this, ForgotPass::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_bottom)
        }
        backbutton.setOnClickListener {
           slideleft()
        }
        submit.setOnClickListener {
            if(usernamestr!=""){
                showProgressBarDialog()
                firestore.collection("usernames").document(usernamestr).get().addOnSuccessListener {
                    if(it.exists()) {
                        email = it.getString("email").toString()
                        if (email != "") {
                            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val isNewUser = task.result.signInMethods!!.isEmpty()
                                    if (!isNewUser) {
                                        auth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    dialog.dismiss()
                                                    val intent =
                                                        Intent(this, EmailRecovery::class.java)
                                                    val bndlAnimation = ActivityOptions.makeCustomAnimation(
                                                        this,
                                                        R.anim.anim_enter,
                                                        R.anim.anim_exit
                                                    ).toBundle()
                                                    intent.putExtra("email", email)
                                                    intent.putExtra("username","username")
                                                    startActivity(intent, bndlAnimation)
                                                }
                                            }
                                    } else {
                                        dialog.dismiss()
                                        Toast.makeText(
                                            this,
                                            "Sorry, This email id is not registered with us",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }else{
                        dialog.dismiss()
                        showUsernameDialog()
                    }
                }.addOnFailureListener{
                    dialog.dismiss()
                    showUsernameDialog()
                }
            }
        }
    }

    private fun showProgressBarDialog(){
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progressbardialog)
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun showUsernameDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.incorrectusernamedialog)
        dialog.findViewById<TextView>(R.id.tryagain).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun slideleft(){
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }
    override fun onBackPressed() {
        super.onBackPressed()
        slideleft()
    }
}